
/** Models a vibrating guitar of a given frequency.
  * @author Kwasi Buansi
  Time Spent: 6 Hours
*/
import java.util.*;

public class GuitarString {
    int tic = 0;
    Queue<Double> queueFrequency = new LinkedList<Double>(); //initialize queue
    public double DECAY_FACTOR = 0.996;

    public GuitarString(double frequency){
        double n = Math.round(StdAudio.SAMPLE_RATE/frequency); //get the length (n) of the queue
        if (frequency <= 0 || n < 2){ //throw error if the frequency is less than or equal to 0
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < n; i++){ //enqueue n zeros
            queueFrequency.add(0.0);
        }
    }

    public GuitarString(double[] init){ //constructor
        //put elements in init into the queue
        if (init.length < 2){ //throw error if queue has fewer than two elements
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < init.length; ++i){ //put variables in init to queueFrequency
            queueFrequency.add(init[i]);
        }
    }

    public void pluck(){
        for (int i = 0; i < queueFrequency.size(); ++i){
            queueFrequency.remove(); //remove a zero
            queueFrequency.add(0.5 - Math.random()); //replace with a valube between 0.5 and -0.5
        }
    }

    public void tic(){
        double applyUpdate = (queueFrequency.poll() + queueFrequency.peek())/2* DECAY_FACTOR; //apply the Karplus-Strong update
        queueFrequency.add(applyUpdate);
        tic ++;
    }

    public double sample(){
        return queueFrequency.peek(); //return the current sample
    }

    public int time(){
        return tic; //return the time
    }
}
