//---------------------------------------- Include Library.
#include <SPI.h>
#include <LoRa.h>
//----------------------------------------

//---------------------------------------- LoRa Pin / GPIO configuration.
#define ss 5
#define rst 14
#define dio0 2
//----------------------------------------

//---------------------------------------- Variable declaration to hold incoming and outgoing data.
String Incoming = "";
String Message = "";            
//----------------------------------------

//---------------------------------------- Variable declaration for Millis/Timer.
unsigned long previousMillis_SendMSG = 0;
const long interval_SendMSG = 1000;
//---------------------------------------- 

//________________________________________________________________________________ Subroutines for receiving data (LoRa Ra-02).
void onReceive(int packetSize) {
  if (packetSize == 0) return;  //--> if there's no packet, return

  //---------------------------------------- read packet header bytes:
  // byte recipient = LoRa.read();        //--> recipient address
  // byte sender = LoRa.read();          //--> sender address
  // byte incomingLength = LoRa.read();  //--> incoming msg length
  //---------------------------------------- 
  Serial.println(Incoming.length());
  // Clears Incoming variable data.
  Incoming = "";

  //---------------------------------------- Get all incoming data.
  while (LoRa.available()) {
    Incoming += (char)LoRa.read();
  }
  Serial.println(Incoming);
  // Serial.println(Incoming.length());
  // //---------------------------------------- 

  // //---------------------------------------- Check length for error.
  // if (incomingLength != Incoming.length()) {
  //   Serial.println("error: message length does not match length");
  //   return; //--> skip rest of function
  // }
  //---------------------------------------- 

  //---------------------------------------- Checks whether the incoming data or message for this device.
  // if (recipient != LocalAddress) {
  //   Serial.println("This message is not for me.");
  //   return; //--> skip rest of function
  // }
  //---------------------------------------- 

  //---------------------------------------- if message is for this device, or broadcast, print details:
  // Serial.println();
  // Serial.println("Received from: 0x" + String(sender, HEX));
  // Serial.println("Message length: " + String(incomingLength));
  // Serial.println("Message: " + Incoming);
  // Serial.println("RSSI: " + String(LoRa.packetRssi()));
  // Serial.println("Snr: " + String(LoRa.packetSnr()));
  //---------------------------------------- 
}
//________________________________________________________________________________ 

//________________________________________________________________________________ VOID SETUP
void setup() {
  // put your setup code here, to run once:

  Serial.begin(115200);

  //---------------------------------------- Settings and start Lora Ra-02.
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
  //---------------------------------------- 
}
//________________________________________________________________________________ 

//________________________________________________________________________________ VOID LOOP
void loop() {
  // put your main code here, to run repeatedly:

  //---------------------------------------- Millis or Timer to send message / command data to slaves every 1 second (see interval_SendCMD variable).
  // Messages are sent every one second is alternately.
  // > Master sends message to Slave 1, delay 1 second.
  // > Master sends message to Slave 2, delay 1 second.

  //---------------------------------------- parse for a packet, and call onReceive with the result:
  onReceive(LoRa.parsePacket());
  //----------------------------------------
}
//________________________________________________________________________________ 
//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
