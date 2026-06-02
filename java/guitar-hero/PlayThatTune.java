
/** Program that plays a selected song using the Guitar ADT
  * 
  * @author Stuart Reges
  * @author Raghuram Ramanujan
*/ 

import javax.swing.*;              // for JFileChooser
import javax.swing.filechooser.*;  // for FileNameExtensionFilter
import java.io.*;                  // for FileNotFoundException
import java.util.*;                // for Scanner

public class PlayThatTune {
    public static final String KEYBOARD =
        "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";  // keyboard layout

    /** Main wrapper method
      * 
      * It prompts the user to select a text file (containing audio sample
      * values) and then plays it by building an appropriate Guitar object.
      * 
      * @param args disregarded
    */
    public static void main(String[] args) throws FileNotFoundException {

        JFileChooser chooser = new JFileChooser(new File("."));
        FileNameExtensionFilter filter =
            new FileNameExtensionFilter("music files", "txt");
        chooser.setFileFilter(filter);
        int result = chooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION)
            return;
        Scanner input = new Scanner(chooser.getSelectedFile());

        Guitar g = new Guitar37();
        int time = 0;

        // repeat as long as there are more integers to read in
        while (input.hasNextInt()) {

            // read in the pitch, where 0 = Concert A (A4)
            int pitch = input.nextInt();
            // convert the pitch to the appropriate guitar string index
            int index = pitch + 12;
            // if the note is included in our keyboard, play it
            if (index >= 0 && index < KEYBOARD.length()) {
                g.pluck(KEYBOARD.charAt(index));
            }

            // read in duration in seconds and convert to a tic count
            double duration = input.nextDouble();
            int target = time + 
                (int) Math.round(duration * StdAudio.SAMPLE_RATE);

            // keep playing the guitar for that duration
            while (time < target) {
                g.play();
                g.tic();
                time++;
            }
        }
    }
}
