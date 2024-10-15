#include <PPPOS.h>
#include <PPPOSClient.h>
#include <PubSubClient.h>
#include <SPI.h>
#include <LoRa.h>
#include <EEPROM.h>
#include <ArduinoJson.h>
#include <string.h>
#include "ThingSpeak.h"
#include <WiFi.h>


#include "soc/rtc_cntl_reg.h"
#include "soc/rtc.h"
#include "driver/rtc_io.h"

//=========================================================================Sleep mode==================================================================================
#define BUTTON_PIN_BITMASK 0x4 // 2^2 in hex
#define uS_TO_S_FACTOR 1000000ULL  // biến chuyển từ micro giây sang giây
#define TIME_TO_SLEEP  180     //Thời gian thức dậy
#define BUTTON_PIN_BITMASK 0x200000000 

// #define SIM
#define WIFI


void print_wakeup_reason(){
  esp_sleep_wakeup_cause_t wakeup_reason;
  wakeup_reason = esp_sleep_get_wakeup_cause();
  switch(wakeup_reason)
  {
    case ESP_SLEEP_WAKEUP_EXT0 : 
      Serial.println("Wakeup caused by external signal using RTC_IO"); 
      break;
    case ESP_SLEEP_WAKEUP_EXT1 : 
      Serial.println("Wakeup caused by external signal using RTC_CNTL"); 
      break;
    case ESP_SLEEP_WAKEUP_TIMER : 
      Serial.println("Wakeup caused by timer"); 
      break;
    case ESP_SLEEP_WAKEUP_TOUCHPAD : 
      Serial.println("Wakeup caused by touchpad"); 
      break;
    case ESP_SLEEP_WAKEUP_ULP : 
      Serial.println("Wakeup caused by ULP program"); 
      break;
    default : 
      Serial.printf("Wakeup was not caused by deep sleep: %d\n",wakeup_reason); 
      break;
  }
}

//----------------------------------------------------------Manager ID-------------------------------------------------------------------------------------------
#define ID "GW9783"
typedef struct id{
  int numberID;
  int addr;
  int addrNumberID;
  JsonDocument idJson;
}id;

id managerID;
//---------------------------------------------------------define EEPROM--------------------------------------------------------------------------------
#define EEPROM_SIZE 1024

//------------------------------------------------------------Module simA7680C---------------------------------------------------------------------------
#ifdef SIM
#define GSM_SERIAL          1
#define GSM_RX              16      // ESP32 TX - SIM RX
#define GSM_TX              17      // ESP32 RX - SIM TX
#define GSM_POWER           32      // GSM Power Enable
#define GSM_BR              115200
#endif

//=====================================================================WIFI=============================================================================
#ifdef WIFI
const char* ssid =  "Hoi Thuy";
const char* password = "12345678@";

WiFiClient  espClient;
#endif

//--------------------------------------------------------------LoRa sx1278 Ra-02------------------------------------------------------------------------
#define ss 5
#define rst 14
#define dio0 2
//-------------------------------------------------------------JSON---------------------------------------------------------------------------------
// DynamicJsonDocument managerID(1024);
// int numberPartners = 0;
JsonDocument dataFull;
JsonDocument recData;

void IRAM_ATTR interrupt() {
  uint64_t GPIO_reason = esp_sleep_get_ext1_wakeup_status();
  Serial.print("GPIO that triggered the wake up: GPIO ");
  Serial.println((log(GPIO_reason)) / log(2), 0);
}
//================================================================Time=====================================================================================================
int interval = 5000;          // interval between sends
long lastSendTime = 0;        // time of last packet send


//--------------------------------------------------------------Variable declaration to hold incoming and outgoing data.---------------------------------

String Incoming = "";
String Message = "";            
//----------------------------------------

char* ppp_user = "";
char* ppp_pass = "";

String buffer = "";
char *data = (char *) malloc(1024); 
bool atMode = true;

const char* publishTopic = "channels/2606003/publish";
const unsigned long postingInterval = 20L * 1000L;
unsigned long lastUploadedTime = 0;

#ifdef WIFI
PubSubClient client(espClient);
char* server = "mqtt3.thingspeak.com";
#endif

#ifdef SIM
PPPOSClient ppposClient;
PubSubClient client(ppposClient);
char* server = "mqtt3.thingspeak.com";
#endif

//================================================================function WiFi========================================================================
#ifdef WIFI
void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (client.connect("OjcNEyo1Iy0xFTApIzoVBgg", "OjcNEyo1Iy0xFTApIzoVBgg", "qDYTsURL6GZrknqpBlPRkiU6")) {
      Serial.println("connected");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void publishMessage(const char* topic, String payload , boolean retained){
  if (client.publish(topic, payload.c_str()))
      Serial.println("Message publised ["+String(topic)+"]: "+payload);
}

void callback(char* topic, byte* payload, unsigned int length) {
  String incommingMessage = "";
  for (int i = 0; i < length; i++) incommingMessage+=(char)payload[i];
  
  Serial.println("Message arrived ["+String(topic)+"]"+incommingMessage);
  
  //--- check the incomming message
    if( strcmp(topic,publishTopic) == 0){
     if (incommingMessage.equals("1")) digitalWrite(BUILTIN_LED, LOW);   // Turn the LED on 
     else digitalWrite(BUILTIN_LED, HIGH);  // Turn the LED off 
  }
}
#endif

//----------------------------------------------------------------function sim------------------------------------------------------------------------
#ifdef SIM
bool sendCommandWithAnswer(String cmd, String ans){
   PPPOS_write((char *)cmd.c_str());
   unsigned long _tg = millis();
   while(true){
    data = PPPOS_read();
    if (data != NULL){
      char* command = strtok(data, "\n");
      while (command != 0)
      {
        buffer = String(command);
        buffer.replace("\r", "");
        command = strtok(0, "\n");
        if (buffer != "") { Serial.println(buffer); }
        if (buffer == ans) {buffer = ""; return true; }
        buffer = "";
      } 
    }
    if (millis() > (_tg + 5000)) { buffer = ""; return false; } 
   }
   buffer = "";
   return false;
}

int8_t  AT_CheckCSQ(void){
   int csq = 0;
   PPPOS_write("AT+CSQ\r\n");
   unsigned long _tg = millis();
   while(true){
    data = PPPOS_read();
    if (data != NULL){
      char* command = strtok(data, "\n");
      while (command != 0)
      {
        buffer = String(command);
        buffer.replace("\r", "");
        command = strtok(0, "\n");
        if (buffer != "") { Serial.println(buffer); }
        if(buffer.indexOf("+CSQ:") >= 0)
        {
          sscanf(buffer.c_str(),"+CSQ: %d", &csq);
          if(csq == 99) csq = 0;
          return csq;
        }
        buffer = "";
      } 
    }
    if (millis() > (_tg + 5000)) { buffer = ""; return false; } 
   }
   buffer = "";
   return csq;
}

int8_t  AT_CheckCLK(void){
   int csq = 0;
   PPPOS_write("AT+CLK?\r\n");
   unsigned long _tg = millis();
   while(true){
    data = PPPOS_read();
    if (data != NULL){
      char* command = strtok(data, "\n");
      while (command != 0)
      {
        buffer = String(command);
        buffer.replace("\r", "");
        command = strtok(0, "\n");
        if (buffer != "") { Serial.println(buffer); }
        if(buffer.indexOf("+CSQ:") >= 0)
        {
          sscanf(buffer.c_str(),"+CSQ: %d", &csq);
          if(csq == 99) csq = 0;
          return csq;
        }
        buffer = "";
      } 
    }
    if (millis() > (_tg + 5000)) { buffer = ""; return false; } 
   }
   buffer = "";
   return csq;
}

bool startPPPOS(){  
  if (!sendCommandWithAnswer("ATD*99***1#\r\n", "CONNECT 115200")) { return false; }
  atMode = false;
  PPPOS_start(); 
  unsigned long _tg = millis();
  while(!PPPOS_isConnected()) {
    if (millis() > (_tg + 10000)) { PPPOS_stop();  atMode = true; return false; }
  }
  Serial.println("PPPOS Started");
  return true;
}

void SIM_reset(void)
{
  digitalWrite(GSM_POWER, LOW);
  delay(100);
  digitalWrite(GSM_POWER, HIGH);
}

void SIM_connect_PPP(void)
{
  while(true)
  {
      SIM_reset();
      bool SyncOK = false;
      Serial.println("SIM Sync"); 
      for(int i = 0; i < 100; i++)
      {
        delay(1000);
        Serial.print("."); 
        if(!sendCommandWithAnswer("AT\r\n", "OK")){
          continue;
        }
        if(!sendCommandWithAnswer("AT+CPIN?\r\n", "OK")){
          continue;
        }
        SyncOK = true;
        break;
      }

      Serial.println("Check Signal Quality"); 
      for(int i = 0; i < 50; i++){
        if(AT_CheckCSQ() > 0){
          break;   
        }
        delay(1000);
      }

      for(int i = 0; i < 50; i++){
        data = PPPOS_read();
        if (data != NULL){
          Serial.println(data);  
        }
        delay(100);
      }

      if(SyncOK){

        Serial.println("Start PPPOS"); 
        if (startPPPOS()) { 
          Serial.println("Entering PPPOS... OK"); 
          break;
        } else { Serial.println("Entering PPPOS... Failed"); }
      }
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i=0;i<length;i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

void publishMessage(const char* topic, String payload, boolean retained){
  if (client.publish(topic, payload.c_str(), retained))
    Serial.println("Message published ["+String(topic)+"]: "+payload);
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (client.connect("OjcNEyo1Iy0xFTApIzoVBgg", "OjcNEyo1Iy0xFTApIzoVBgg", "qDYTsURL6GZrknqpBlPRkiU6")) {
      Serial.println("connected");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}
#endif 

//===============================================================================Proceed Data Json========================================================
void merge(JsonObject& dataFull, JsonObjectConst& dataRec)
{
   for (JsonPairConst kvp : dataRec)
   {
    if (strcmp(kvp.key().c_str(), "ID") == 0) {
      continue;
    }
     dataFull[kvp.key()] = kvp.value();
   }
}

void resetData(JsonObject& jsonObject)
{
   for (JsonPair kvp : jsonObject)
   {
     kvp.value().set(0);
   }
}

//---------------------------------------------------------------------------function LoRa------------------------------------------------------------------

void sendMessage(String Outgoing) 
{
  LoRa.beginPacket();             //--> start packet
  LoRa.print(Outgoing);           //--> add payload
  LoRa.endPacket();               //--> finish packet and send it
}

void onReceive(int packetSize) 
{
  if (packetSize == 0) return;  //--> if there's no packet, return
  Incoming = "";

  while (LoRa.available()) {
    Incoming += (char)LoRa.read();
  }
  Serial.println("\n");
  deserializeJson(recData, Incoming);
  serializeJson(recData, Serial);
  JsonObjectConst object2 = recData.as<JsonObjectConst>();
  JsonObject object1 = dataFull.as<JsonObject>();
  merge(object1, object2);
  const char *recID = recData["ID"];
  
  Serial.println("\nReceived from: " + String(recID));
  // Serial.println("Message length: " + String(Incoming.length()));
  Serial.println("RSSI: " + String(LoRa.packetRssi()));
  //---------------------------------------- 
}

void receivedFromNodes()
{
  int times = 0;
  for(int element = 1; element < managerID.numberID; element++){
    while(times < 5){
      if (millis() - lastSendTime > interval) {
        sendMessage(managerID.idJson[element]);
        const char *tempID = managerID.idJson[element];
        Serial.println(String(times) + "st " + String(tempID)+ " request");
        times++;
        lastSendTime = millis();
      }
      onReceive(LoRa.parsePacket());
      if(recData["ID"] == managerID.idJson[element]){
        break;
      }
    }
    times = 0;
  }
  int sss = 
  Serial.println("data FULL: ");
  serializeJson(dataFull, Serial);
}

//-------------------------------------------------------------------------------MANAGE ID-------------------------------------------------------------------
void writeIdToRom(char *id)
{
  // writing byte-by-byte to EEPROM
  managerID.idJson[managerID.numberID] = id;
  for (int i = 0; i < strlen(id); i++) {
      EEPROM.write(managerID.addr, id[i]);
      managerID.addr += 1;
  }
  managerID.numberID++;
  if(EEPROM.read(1000) < managerID.numberID){
    EEPROM.write(1000, managerID.numberID);//write number ID in address 1000 
  }else{
    managerID.numberID = EEPROM.read(1000);
  }
  Serial.println("Add ID: " + String(id));
  Serial.println("Number ID: " + String(managerID.numberID));
  EEPROM.commit();
}

void readIdfromRom()
{
  int IDs = EEPROM.read(1000);
  Serial.println("\nNumber ID Read From ROM: " + String(managerID.numberID));
  for(int k = 0; k < IDs; k++){
    char readValueChar[10] = "";
    for(int j = k*6; j < (k*6 + 6); j++){
      byte readValue = EEPROM.read(j);
      if (readValue == 0) {
          break;
      }
      char tempStr[2];
      tempStr[0] = char(readValue);
      tempStr[1] = '\0';

      strcat(readValueChar, tempStr);
    }
  }
  Serial.println("All ID: ");
  serializeJson(managerID.idJson, Serial);
}

//--------------------------------------------------------------------------------HANDLE TIME--------------------------------------------------------------
String response = "";
String latitude = "";
String longitude = "";
String date = "";
String time1 = "";
void parseCLBSResponse(String response) {
    // Tìm vị trí các dấu phẩy
    int firstComma = response.indexOf(',');
    int secondComma = response.indexOf(',', firstComma + 1);
    int thirdComma = response.indexOf(',', secondComma + 1);
    int fourthComma = response.indexOf(',', thirdComma + 1);
    int fifthComma = response.indexOf(',', fourthComma + 1);
    int sixthComma = response.indexOf(',', fifthComma + 1);

    // Tách các phần tử từ chuỗi
    String latitude = response.substring(firstComma + 1, secondComma);
    String longitude = response.substring(secondComma + 1, thirdComma);
    String date = response.substring(fourthComma + 1, fifthComma);
    String time = response.substring(fifthComma + 1, sixthComma);

    // In kết quả ban đầu
    Serial.println("Latitude: " + latitude);
    Serial.println("Longitude: " + longitude);
    Serial.println("UTC Date: " + date);
    Serial.println("UTC Time: " + time);

    // Chuyển đổi thời gian từ UTC sang UTC+7
    int hour = time.substring(0, 2).toInt();
    int minute = time.substring(3, 5).toInt();
    int second = time.substring(6, 8).toInt();

    // Cộng thêm 7 giờ để chuyển sang múi giờ UTC+7
    hour += 7;

    // Kiểm tra nếu giờ vượt quá 24, thì điều chỉnh lại giờ và ngày
    if (hour >= 24) {
        hour -= 24;

        // Tăng ngày nếu cần
        int year = date.substring(0, 4).toInt();
        int month = date.substring(5, 7).toInt();
        int day = date.substring(8, 10).toInt();
        
        day += 1;

        // Kiểm tra nếu ngày vượt quá số ngày của tháng, thì điều chỉnh lại tháng và năm
        if (day > daysInMonth(year, month)) {
            day = 1;
            month += 1;

            // Kiểm tra nếu tháng vượt quá 12, thì điều chỉnh lại năm
            if (month > 12) {
                month = 1;
                year += 1;
            }
        }

        // Cập nhật lại chuỗi ngày
        date = String(year) + "/" +
               (month < 10 ? "0" : "") + String(month) + "/" +
               (day < 10 ? "0" : "") + String(day);
    }

    // Cập nhật lại chuỗi thời gian
    time = (hour < 10 ? "0" : "") + String(hour) + ":" +
           (minute < 10 ? "0" : "") + String(minute) + ":" +
           (second < 10 ? "0" : "") + String(second);

    // In kết quả sau khi chuyển đổi
    Serial.println("Local Date (UTC+7): " + date);
    Serial.println("Local Time (UTC+7): " + time);
}

// Hàm tính số ngày trong tháng (đã có sẵn trong đoạn code trước)
int daysInMonth(int year, int month) {
    if (month == 2) {
        // Kiểm tra năm nhuận
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            return 29;
        } else {
            return 28;
        }
    } else if (month == 4 || month == 6 || month == 9 || month == 11) {
        return 30;
    } else {
        return 31;
    }
}

//--------------------------------------------------------------------------------setup---------------------------------------------------------------------
void setup()
{
  Serial.begin(115200);
  managerID.addr = 0;
  dataFull["MyID"] = ID;
  managerID.numberID = 0;
  
  #ifdef WIFI
  WiFi.mode(WIFI_STA); 
    // Connect or reconnect to WiFi
  if(WiFi.status() != WL_CONNECTED){
    Serial.print("Attempting to connect");
    while(WiFi.status() != WL_CONNECTED){
      WiFi.begin(ssid, password); 
      delay(5000);     
    } 
    Serial.println("\nConnected.");
    }
  #endif 


  #ifdef SIM
  pinMode(GSM_POWER, OUTPUT);
  gpio_hold_dis((gpio_num_t)GSM_POWER);
  Serial.begin(115200);
  Serial2.begin(115200);
  digitalWrite(GSM_POWER, LOW);
  delaySIM(500);
  digitalWrite(GSM_POWER, HIGH);
  delaySIM(10000);
  Serial2.println("AT+CLBS=4");
  delaySIM(500);
  while (Serial2.available())
  {
    response = Serial2.readStringUntil('\n');
  }
  parseCLBSResponse(response);
  #endif

  char *id = ID;
  //============================================================================My Device ID================================================================
  Serial.println("My ID: " + String(ID));
  Serial.println("\n");
  if (!EEPROM.begin(EEPROM_SIZE)) {
      Serial.println("failed to init EEPROM");
      while(1);
  }
  EEPROM.write(1000, 0);
  //============================================================================Write MyID to EPPROM================================================================
  writeIdToRom(id);
  writeIdToRom("ND7969");
  writeIdToRom("ND7970");
  //============================================================================Init SIM======================================================================
  #ifdef SIM
  /*  Init PPP  */
  PPPOS_init(GSM_TX, GSM_RX, GSM_BR, GSM_SERIAL, ppp_user, ppp_pass);

  SIM_connect_PPP();
  client.setServer(server, 1883);
  client.setCallback(callback);
  #endif

  //============================================================================Init WIFI=======================================================================
  #ifdef WIFI
  client.setServer(server, 1883);
  client.setCallback(callback);
  #endif
  //=============================================================================Init Lora====================================================================
  LoRa.setPins(ss, rst, dio0);
  
  Serial.println("Start LoRa init...");
  if (!LoRa.begin(433E6)) {             // initialize ratio at 915 or 433 MHz
    Serial.println("LoRa init failed. Check your connections.");
    while (true);                       // if failed, do nothing
  }
  LoRa.setSpreadingFactor(12);
  LoRa.setSignalBandwidth(125E3) ;
  LoRa.setCodingRate4(5);
  LoRa.setSyncWord(0x12);
  Serial.println("LoRa init succeeded.");
  readIdfromRom();

  // gọi hàm thức dậy mỗi 5s
  esp_sleep_enable_timer_wakeup(TIME_TO_SLEEP * uS_TO_S_FACTOR);
  Serial.println("Setup ESP32 to sleep for every " + String(TIME_TO_SLEEP) + " Seconds");
}

void loop()
{
  dataFull["TEMP"] = "0";
  dataFull["HUM"] = "0";
  dataFull["SAL"] = "0";
  dataFull["Q"] = "0";
  receivedFromNodes();

  #ifdef WIFI
  if (!PPPOS_isConnected() || atMode){
    data = PPPOS_read();
    if (data != NULL){
      Serial.println(data);  
    }
  }

  if (!PPPOS_isConnected() || atMode){
      data = PPPOS_read();
      if (data != NULL){
        Serial.println(data);  
      }
    }

    if(PPPOS_isConnected()) {
      if (!client.connected()) {
        reconnect();
      }
      client.loop();
      if (millis() - lastUploadedTime > postingInterval) {
        const char* temp = dataFull["TEMP"];
        const char* hum = dataFull["HUM"];
        const char* sal = dataFull["SAL"];
        const char* q = dataFull["Q"];
        const char* id = dataFull["MyID"];
        const char* time = dataFull["Time"];
        const char* date = dataFull["Date"];

        String dataText = String("field1=") + temp + "&field2=" + hum + "&field3=" + sal + "&field4=" + q +"&status=MQTTPUBLISH";
        publishMessage(publishTopic, dataText, true);    
        lastUploadedTime = millis();
    }    
  }
  #endif 


  #ifdef SIM
  if (!PPPOS_isConnected() || atMode){
    data = PPPOS_read();
    if (data != NULL){
      Serial.println(data);  
    }
  }

  if (!PPPOS_isConnected() || atMode){
      data = PPPOS_read();
      if (data != NULL){
        Serial.println(data);  
      }
    }

    if(PPPOS_isConnected()) {
      if (!client.connected()) {
        reconnect();
      }
      client.loop();
      if (millis() - lastUploadedTime > postingInterval) {
        const char* temp = dataFull["TEMP"];
        const char* hum = dataFull["HUM"];
        const char* sal = dataFull["SAL"];
        const char* q = dataFull["Q"];
        const char* id = dataFull["MyID"];
        const char* time = dataFull["Time"];
        const char* date = dataFull["Date"];

        String dataText = String("field1=") + temp + "&field2=" + hum + "&field3=" + sal + "&field4=" + q +"&status=MQTTPUBLISH";
        publishMessage(publishTopic, dataText, true);    
        lastUploadedTime = millis();
    }    
  }
  #endif
  deserializeJson(recData, "{}");//clear Json
  deserializeJson(dataFull, "{}");//clear Json
  dataFull["MyID"] = ID;
  #ifdef SIM
  delay(1000);
  digitalWrite(GSM_POWER, LOW);
  gpio_hold_en((gpio_num_t)GSM_POWER);
  #endif
  esp_deep_sleep_start();
} 

void test_sim800_module()
{
  Serial2.println("AT+CLBS=4");
  delay(500);
  while (Serial2.available())
  {
    Serial.write(Serial2.read());//Forward what Software Serial received to Serial Port
  }
}

void delaySIM(int delay){
  int firstTime = millis();
  while((millis() - firstTime) < delay){
    // Do nothing, just wait
  }
}