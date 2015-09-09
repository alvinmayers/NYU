#include <Servo.h>

//Transistor pins
const int TBP_A = 2;
const int TBP_B = 3;

//Button pins
const int button_A = 9;
const int button_B = 8;
const int button_C = 7;
const int button_D = 4;

//Servo variables
const int servoPin = 5;
const int servoPause = 100;

//Speaker pin
const int speaker = 10;

//Angle of the servo motor
int servoAngle = 0;

//Last readings of a button
int lA = 0, lB = 0, lC = 0, lD = 0;

//Current readings of a button
int cA = 0, cB = 0, cC = 0, cD = 0;

//Servo object
Servo servo;

//led status
char led_stat; 


//Updates the current reading variables
void updateCurrentReadings() {
	cA = digitalRead(button_A);
	cB = digitalRead(button_B);
	cC = digitalRead(button_C);
	cD = digitalRead(button_D);
}

//Handle button inputs
void handleButtons() {
    //Button presses
	if (cA == HIGH && lA == LOW) { //If A was pressed
		Serial.println("a");
	} else if (cB == HIGH && lB == LOW) { //If B was pressed
		Serial.println("b");
	} else if (cC == HIGH && lC == LOW) { //If C was pressed
		Serial.println("c");
	} else if (cD == HIGH && lD == LOW) { //If D was pressed
		Serial.println("d");
	//Double button presses
	} else if (cC == HIGH && cD == HIGH) { //If C and D are being held
		Serial.println("cd");
	}
}

//Updates the last readings varibles with the current readings
void updateLastReadings() {
	lA = cA;
	lB = cB;
	lC = cC;
	lD = cD;
}

void setup() {
	Serial.begin(9600);
	pinMode(TBP_A, OUTPUT);
	pinMode(TBP_B, OUTPUT);

	pinMode(button_A, INPUT);
	pinMode(button_B, INPUT);
	pinMode(button_C, INPUT);
	pinMode(button_D, INPUT);

	servo.attach(servoPin);
	pinMode(speaker, OUTPUT);
}

void loop() {
    updateCurrentReadings();
    handleButtons();
    updateLastReadings();
    
	//tone(speaker, servoAngle);
   if(Serial.available() > 0){
       led_stat=Serial.read();
       Serial.println(led_stat);
       if(led_stat == 'G') {
           digitalWrite(TBP_B,HIGH);
       } else if(led_stat == 'R') {
           digitalWrite(TBP_A,HIGH);
       }
       delay(500);
       digitalWrite(TBP_B,LOW); digitalWrite(TBP_A,LOW);
    }
}
