
/** Creates a guitar with 37 strings.
 * @author Kwasi Buansi
 * Time Spent: 6 Hours
 */

public class Guitar37 implements Guitar {

    // keyboard layout
    public static final String KEYBOARD = 
        "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    
    // COMPLETE ME!
    GuitarString GuitarStrings[] = new GuitarString[KEYBOARD.length()]; // make an array of guitar strings
    final int NOT_IN = -1;
    

    public Guitar37() { // constructor
        for (int i = 0; i < KEYBOARD.length(); ++i){ // iterate through keyboard
            double CONCERT_CURRENT = 440 * Math.pow(2, (i - 24)/12.0); // calculate hertz for each index
            GuitarStrings[i] = new GuitarString(CONCERT_CURRENT); // put the hertz value into the corresponding guitar string
        }
    }

    public boolean hasString(char string){
        int checkKeyboard = KEYBOARD.indexOf(string); // get the index of whatever key is pressed
        if (checkKeyboard == NOT_IN){ // return false if the key is not in the string
            return false;
        }
            return true;
    }

    public void pluck(char string){
        int checkKeyboard = KEYBOARD.indexOf(string); // get the index of whatever key is pressed
        if (checkKeyboard == NOT_IN){
            throw new IllegalArgumentException(); // if the key is not in the string, return an error
        }
        for (int strings = 0; strings < GuitarStrings.length; ++strings){ // iterate through GuitarStrings
            GuitarString guitarStringAtIndex = GuitarStrings[checkKeyboard]; // refer to the guitarstring that corresponds with the keypress
            guitarStringAtIndex.pluck(); // pluck if string index = character index
        }
    }

    public void play(){
        double sampleTotal = 0;
        for (int strings = 0; strings < GuitarStrings.length; ++strings){ // iterate through GuitarStrings
            sampleTotal = sampleTotal + GuitarStrings[strings].sample(); // sum all the values in GuitarStrings array
        }
        StdAudio.play(sampleTotal); // put summation into play function
    }

    public void tic(){
        for(int strings = 0; strings < GuitarStrings.length; ++strings){
            GuitarStrings[strings].tic(); // tic +1 for each string
        }
    }
}