
/** An implementation of a 2-string Guitar.
  * 
  * THIS CLASS IS NOT WELL-DOCUMENTED!!
*/

public class GuitarLite implements Guitar {
    private GuitarString stringA;
    private GuitarString stringC;

    // create two guitar strings, for concert A and C
    public GuitarLite() {
        double CONCERT_A = 440.0;
        double CONCERT_C = CONCERT_A * Math.pow(2, 3.0/12.0);  
        stringA = new GuitarString(CONCERT_A);
        stringC = new GuitarString(CONCERT_C);
    }

    public boolean hasString(char string) {
        return (string == 'a' || string == 'c');
    }
    
    public void pluck(char string) {
        if (string == 'a') {
            stringA.pluck();
        } else if (string == 'c') {
            stringC.pluck();
        }
    }

    public void play() {
        // compute the superposition of samples
        double sample = stringA.sample() + stringC.sample();
        // send the result to the sound card
        StdAudio.play(sample);
    }

    public void tic() {
        stringA.tic();
        stringC.tic();
    }
}
