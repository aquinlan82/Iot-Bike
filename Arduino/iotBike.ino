#include <Adafruit_NeoPixel.h>

int r;
int g;
int b;
int PIN = 6;
bool rainbow = false;
uint16_t i = 0;
uint16_t j = 0;
Adafruit_NeoPixel strip = Adafruit_NeoPixel(16, PIN, NEO_GRB + NEO_KHZ800);

void setup() {
   Serial.begin(9600);
  strip.begin();
  strip.setBrightness(20); 
  strip.show(); // Initialize all pixels to 'off'
}

void loop() {
    Serial.println(rainbow + " " + String(r) + " " + String(g) + " " + String(b));
    rawToColor(read());
    if(!rainbow) {
        //figure out adafruit, set by rgb
       for(uint16_t i=0; i<strip.numPixels(); i++) {
          strip.setPixelColor(i, strip.Color(r,g,b));
          strip.show();
          delay(50);
        }    
    } else {
        rainbowCycle();
    }
    rainbow = (r == 255 && g == 255 && b==255);
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

void rainbowCycle() {
    //for(j=0; j<256*5; j++) { // 5 cycles of all colors on wheel
    //for(i=0; i< strip.numPixels(); i++) {
    if (j >= 256*5) {
        i = 0;
        j = 0;
    }
    if (i >= strip.numPixels()) {
        i = 0;
        j++;
    }
    strip.setPixelColor(i, Wheel(((i * 256 / strip.numPixels()) + j) & 255));
    strip.show();
    delay(50);
    i++;
}


// Input a value 0 to 255 to get a color value.
// The colours are a transition r - g - b - back to r.
uint32_t Wheel(byte WheelPos) {
  if(WheelPos < 85) {
   return strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
  } else if(WheelPos < 170) {
   WheelPos -= 85;
   return strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  } else {
   WheelPos -= 170;
   return strip.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
}
  
