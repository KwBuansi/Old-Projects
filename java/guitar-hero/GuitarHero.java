
/** A client class for Guitar ADTs that allows the user to play one.
  * 
  * @author Stuart Reges
  * @author Raghuram Ramanujan
*/
public class GuitarHero {

    /** Main tester method */
    public static void main(String[] args) {
        
        // Once you've written your Guitar37 class, you'll want to modify the
        // the following line to test it out.
        Guitar g = new Guitar37();

        // This is an infinite loop - user must quit the application
        for (;;) {

            // Check if the user has typed a key; if so, process it   
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (g.hasString(key)) {
                    g.pluck(key);
                } else {
                    System.out.println("bad key: " + key);
                }
            }

            g.play();
            g.tic();
        }
    }
}
