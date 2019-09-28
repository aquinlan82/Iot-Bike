#include <Adafruit_NeoPixel.h>

int r;
int g;
int b;
int PIN = 6;
Adafruit_NeoPixel strip = Adafruit_NeoPixel(16, PIN, NEO_GRB + NEO_KHZ800);

void setup() {
   Serial.begin(9600);
  strip.begin();
  strip.setBrightness(20); 
  strip.show(); // Initialize all pixels to 'off'
}

void loop() {
    rawToColor(read());
    //figure out adafruit, set by rgb
   for(uint16_t i=0; i<strip.numPixels(); i++) {
      strip.setPixelColor(i, strip.Color(r,g,b));
      strip.show();
      delay(50);
    }
}

String read() {
    String command="";
    if (Serial.available()) {
        delay(20); //keeps lines together
        while(Serial.available()) { 
            command += (char)Serial.read();
        }
        //Serial.println(command);
    }
    return command;
}

void rawToColor(String input) {
    //input is in form 'r b g'
    if (input != "") {
        int space1 = input.indexOf("/");
        int space2 = input.lastIndexOf("/");
        
        r = input.substring(0, space1).toDouble();
        g = input.substring(space1 + 1, space2).toDouble();
        b = input.substring(space2 + 1).toDouble();
    }

}
  
