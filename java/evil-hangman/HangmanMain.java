
/** The main driver class for the Evil Hangman program.
  * 
  * This class handles all the user interaction and file I/O operations for
  * the "cheating" computer Hangman player.
  * 
  * @author Raghuram Ramanujan
*/

import java.util.*;
import java.io.*;

public class HangmanMain  {

    /* Modify the following constant appropriately while testing.
     * "dictionary2.txt" is the small 9-word dictionary used in the handout.
     * "dictionary.txt" is the 127k word Scrabble dictionary. */
    public static final String DICTIONARY_FILE = "dictionary2.txt";
    
    /* SHOW_COUNT = true --> the number of words currently being considered by
     * the program are shown after every guess (useful for debugging purposes).
     * SHOW_COUNT = false --> the word count is not shown (the setting you want
     * for demo purposes).*/
    public static final boolean SHOW_COUNT = true;
    
    /** Main wrapper method for the Evil Hangman program.
      * 
      * This method opens the specified dictionary file, initializes the
      * computer Hangman player and then manages the game.
      * 
      * @param args command-line parameters to the program (ignored)
      * @throws FileNotFoundException if dictionary file is not found
    */
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Welcome to the CSC 221 Hangman game.");
        System.out.println();
        
        // open the dictionary file and read dictionary into an ArrayList
        Scanner input = new Scanner(new File(DICTIONARY_FILE));
        List<String> dictionary = new ArrayList<String>();
        while (input.hasNext()) {
            dictionary.add(input.next().toLowerCase());
        }
        
        // set basic parameters
        Scanner console = new Scanner(System.in);
        System.out.print("What length word do you want to use? ");
        int length = console.nextInt();
        System.out.print("How many wrong answers allowed? ");
        int max = console.nextInt();
        System.out.println();
        
        // set up the HangmanManager and start the game
        List<String> dictionary2 = Collections.unmodifiableList(dictionary);
        HangmanManager hangman = new HangmanManager(dictionary2, length, max);
        if (hangman.words().isEmpty()) {
            System.out.println("No words of that length in the dictionary.");
        } 
        else {
            playGame(console, hangman);
            showResults(hangman);
        }
    }
    
    
    /** Plays a single game of Hangman with the user
      * 
      * The method processes user guesses and tracks the state of the game,
      * ending when either the user runs out of guesses, or correctly finds the
      * word.
      * 
      * @param console a Scanner object that streams user input
      * @param hangman the Hangman AI
    */
    public static void playGame(Scanner console, HangmanManager hangman) {
        while (hangman.guessesLeft() > 0 && hangman.pattern().contains("-")) {
            // show current state of game
            System.out.println("guesses : " + hangman.guessesLeft());
            if (SHOW_COUNT) {
                System.out.println("words   : " + hangman.words().size());
            }
            System.out.println("guessed : " + hangman.guesses());
            System.out.println("current : " + hangman.pattern());
            System.out.print("Your guess? ");
            
            // process the next guess
            char ch = console.next().toLowerCase().charAt(0);
            if (hangman.guesses().contains(ch)) {
                System.out.println("You already guessed that");
            } else {
                int count = hangman.record(ch);
                if (count == 0) {
                    System.out.println("Sorry, there are no " + ch + "'s");
                } else if (count == 1) {
                    System.out.println("Yes, there is one " + ch);
                } else {
                    System.out.println("Yes, there are " + count + " " + ch +
                                       "'s");
                }
            }
            System.out.println();
        }
    }
    
    /** Reports the final results of the Hangman game, including the answer.
      * 
      * @param hangman the Hangman AI
    */
    public static void showResults(HangmanManager hangman) {
        // when the game is over, the answer is the first word in the list
        // of words
        String answer = hangman.words().iterator().next();
        System.out.println("answer = " + answer);
        if (hangman.guessesLeft() > 0) {
            System.out.println("You beat me");
        } else {
            System.out.println("Sorry, you lose");
        }
    }
}
