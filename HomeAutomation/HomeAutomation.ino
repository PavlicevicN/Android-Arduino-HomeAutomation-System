String instruction;
#define relay1 2

void setup(){
  Serial.begin(9600);
  pinMode(relay1, OUTPUT);
  digitalWrite(relay1, LOW);
}
void loop()
{
  while(Serial.available()) 
  {
    delay(10);
    char c = Serial.read();
    if (c == '#'){
      break;
    }
    instruction += c;
  }
    if (instruction.length() >0){
      Serial.println(instruction);
      if(instruction == "1"){
        switchon();
      } 
      else if(instruction == "0"){
        switchoff();
      }
      instruction="";
    }
}
void switchon()
{
  digitalWrite(relay1, HIGH);
}
void switchoff() 
{
  digitalWrite(relay1, LOW);
}
