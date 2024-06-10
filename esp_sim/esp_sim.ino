#include <PPPOS.h>
#include <PPPOSClient.h>
#include <PubSubClient.h>
#include <SPI.h>
#include <LoRa.h>
#include <EEPROM.h>
#include <ArduinoJson.h>
#include <string.h>

//----------------------------------------------------------Manager ID-------------------------------------------------------------------------------------------
#define ID "GW9783"
typedef struct id{
  int numberID;
  int addr;
  int addrNumberID;
}id;

id managerID;
char *nodeID[1000];
JsonDocument idJson;
//---------------------------------------------------------define EEPROM--------------------------------------------------------------------------------
#define EEPROM_SIZE 1024

//------------------------------------------------------------Module simA7680C---------------------------------------------------------------------------
#define GSM_SERIAL          1
#define GSM_RX              16      // ESP32 TX - SIM RX
#define GSM_TX              17      // ESP32 RX - SIM TX
#define GSM_POWER           15      // GSM Power Enable
#define GSM_BR              115200

//--------------------------------------------------------------LoRa sx1278 Ra-02------------------------------------------------------------------------
#define ss 5
#define rst 14
#define dio0 2

//-------------------------------------------------------------JSON---------------------------------------------------------------------------------
// DynamicJsonDocument managerID(1024);
int numberPartners = 0;
JsonDocument recJson;
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

const char* publishTopic ="main";
const unsigned long postingInterval = 20L * 1000L;
unsigned long lastUploadedTime = 0;
//----------------------------------------------------------------function sim------------------------------------------------------------------------
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
        delay(500);
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

PPPOSClient ppposClient;
PubSubClient client(ppposClient);
// char* server = "mqtt3.thingspeak.com";
char* server = "127.0.0.1";

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i=0;i<length;i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

//======================================= publising as string
void publishMessage(const char* topic, String payload , boolean retained){
  if (client.publish(topic, payload.c_str()))
      Serial.println("Message publised ["+String(topic)+"]: "+payload);
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    if (client.connect("JxUNHxwxFgYBEjkBFBUqGi4", "JxUNHxwxFgYBEjkBFBUqGi4","4Fusq7vyk/SWXKS19EGQLRWQ")) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      // client.publish("outTopic135","hello world from module sim");
      // ... and resubscribe
      // client.subscribe("inTopic");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

//---------------------------------------------------------------------------function LoRa------------------------------------------------------------------

void sendMessage(String Outgoing) {
  LoRa.beginPacket();             //--> start packet
  LoRa.print(Outgoing);           //--> add payload
  LoRa.endPacket();               //--> finish packet and send it
}

void onReceive(int packetSize) {
  if (packetSize == 0) return;  //--> if there's no packet, return
  Incoming = "";

  while (LoRa.available()) {
    Incoming += (char)LoRa.read();
  }
  deserializeJson(recJson, Incoming);
  serializeJson(recJson, Serial);
  if(recJson["ID"] == "ND7969"){
    Serial.println("Received");
    sendMessage("Received");
  }

  const char *recID = recJson["ID"];
  Serial.println("Received from: " + String(strlen(recID)));
  Serial.println("Message length: " + String(Incoming.length()));
  Serial.println("RSSI: " + String(LoRa.packetRssi()));
  Serial.println("Snr: " + String(LoRa.packetSnr()));
  //---------------------------------------- 
}

//-------------------------------------------------------------------------------MANAGE ID-------------------------------------------------------------------
void writeIdToRom(char *id){
  // writing byte-by-byte to EEPROM
  idJson[managerID.numberID] = id;
  for (int i = 0; i < strlen(id); i++) {
      EEPROM.write(managerID.addr, id[i]);
      managerID.addr += 1;
  }
  nodeID[managerID.numberID] = id;
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

void readIdfromRom(){
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
    nodeID[k] = readValueChar;
  }
  Serial.println("All ID: ");
  serializeJson(idJson, Serial);
}


//--------------------------------------------------------------------------------setup---------------------------------------------------------------------
void setup()
{
  managerID.addr = 0;
  managerID.numberID = 0;
  Serial.begin(115200);
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
  //============================================================================Init SIM======================================================================
  /*  Init PPP  */
  // PPPOS_init(GSM_TX, GSM_RX, GSM_BR, GSM_SERIAL, ppp_user, ppp_pass);
  // pinMode(GSM_POWER, OUTPUT);

  // // SIM_connect_PPP();

  // // client.setServer(server, 1883);
  // // client.setCallback(callback);

  //=============================================================================Init Lora====================================================================
  LoRa.setPins(ss, rst, dio0);
  
  Serial.println("Start LoRa init...");
  if (!LoRa.begin(433E6)) {             // initialize ratio at 915 or 433 MHz
    Serial.println("LoRa init failed. Check your connections.");
    while (true);                       // if failed, do nothing
  }
  LoRa.setSpreadingFactor(7);
  LoRa.setSignalBandwidth(125E3) ;
  LoRa.setCodingRate4(5);
  LoRa.setSyncWord(0x12);
  Serial.println("LoRa init succeeded.");
  // sendMessage("ND7969");
}

void loop()
{
  if (millis() - lastSendTime > interval) {
    sendMessage("ND7969");
    lastSendTime = millis();
  }
  onReceive(LoRa.parsePacket());

  // if (!PPPOS_isConnected() || atMode){
  //   data = PPPOS_read();
  //   if (data != NULL){
  //     Serial.println(data);  
  //   }
  // }

  // if(PPPOS_isConnected())
  // {
  //   if (!client.connected()) {
  //     reconnect();
  //   }
  //   client.loop();
  //   if (millis() - lastUploadedTime > postingInterval) 
  //   { // The uploading interval must be > 15 seconds 
  //     int sensorValue_1 = random(100); // replace with your sensor value
  //     int sensorValue_2=random(100);  // replace with your sensor value
  //     //int sensorValue_3=random(100);  // if you want to use three sensors enable this line
      
  //     String dataText = String("field1=" + String(sensorValue_1)+ "&field2=" + String(sensorValue_2)+"&status=MQTTPUBLISH");
  //     //String dataText = String("field1=" + String(sensorValue_1)+ "&field2=" + String(sensorValue_2)+"&field3=" + String(sensorValue_3)); // example for publish tree sensors
  //     publishMessage(publishTopic,dataText,true);    
  //     lastUploadedTime = millis();
  //   }    
  // }

}