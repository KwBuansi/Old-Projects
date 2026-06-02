import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class HangmanManager{
    /** Manager class for HamganMain.
     * 
     * Time spent: 12 Hours
     * 
     * @author Kwasi Buansi 
    */
    int IS_EMPTY = 0;
    int BASE_POSITION = 1;
    int INCREMENT = 1;
    String displayMostOccurrent = "";
    Set<String> wordSet = new HashSet<String>();
    SortedSet<Character> lettersGuessed = new TreeSet<>();
    int currentGuess;
    char currentPattern[];
    char mostOccurrentPattern[] = currentPattern;
    int length;
    Map<String, Integer> occurancesMap = new HashMap<String, Integer>();
    
    public HangmanManager(List<String> dictionary, int length, int max){
        /** Constructor for HangmanManager.
         * 
         * If the input length and max are valid, this method puts words of size
         * length into wordSet to be used in the record method. It also
         * crease an array currentPattern and initializes it with all dashes,
         * and creates the string to be displayed after each guess.
         * 
         * @param dictionary the dictionary from which to get words of size length.
         * @param length input length.
         * @param max max number of allowed attempts.
         * @throws IllegalArgumentException if the length/max are less than 1/0 respectively.
        */
        if (length < 1 || max < 0){ //if the input length/max is less that 1/0
            throw new IllegalArgumentException(); //throw an error
        }
        currentGuess = max;
        for (int i = 0; i < dictionary.size(); i++){ //iterates through dictionary and adds all words of the defined length to wordSet
            if ((dictionary.get(i)).length() == length){ //if the length of the word is equal to the length variable
                wordSet.add(dictionary.get(i)); //add it to wordSet
            }
        }
        currentPattern = new char[length];
        for(int i = 0; i < currentPattern.length; ++i){// initialize currentPattern with all dashes
            currentPattern[i] = '-';
        }
        mostOccurrentPattern = currentPattern;
        createDisplay();
        displayMostOccurrent += mostOccurrentPattern[mostOccurrentPattern.length - 1];
    }

    public Set<String> words(){
        /** Returns the wordSet.
         * 
         * Returns the wordSet which is modified after each guess.
         * 
         * @return wordSet the next set of words to be considered.
        */
        return wordSet;
    }

    public int guessesLeft(){
    /** Return the number of guesses left.
     * 
     * Returns the number of guesses the user has left. If the number of
     * guesses reaches 0, the program ends and the user loses the game.
     * 
     * @return currentGuess the current number of guesses left.
    */
        return currentGuess;
    }

    public SortedSet<Character> guesses(){
    /** Returns the letters guessed.
     * 
     * Returns a list of letters that have been guessed so far.
     * 
     * @return lettersGuessed the list of letters.
    */
        return lettersGuessed;
    }

    public String pattern(){
    /** Returns the largest word family.
     * 
     * If the wordset still has words in it, this method will
     * return the largest word family and the game continues.
     * 
     * @return displayMostOccurrent the largest word family
     * @throws IllegalStateException if the wordset is empty
    */
        if (wordSet.size() == 0){
            throw new IllegalStateException();
        }
        return displayMostOccurrent;
    }

    public int record(char guess){
        /** Find the largest word family and count occurances of guess.
         * 
         * If the current guess is a valid number greater than 0 and
         * wordSet is not empty, then calculate the number of occurances
         * of guess in each word in the wordSet and store them, calculate
         * the most recurring word family based on the stored values, and
         * return the number of occurances of the guessed word in the
         * largest word family.
         * 
         * @param guess the character to be added to the list of guessed words.
         * @return countOccurances the number of times guess appears in the largest word family.
        */
        if (currentGuess < BASE_POSITION || wordSet.size() == IS_EMPTY){
            throw new IllegalStateException();
        }
        if (wordSet.size() > IS_EMPTY && lettersGuessed.contains(guess)){
            throw new IllegalArgumentException();
        }
        int countOccurances = 0;
        int mostOccurrentValue = 1;
        char newPattern[] = new char[currentPattern.length];
        for(int i = 0; i < newPattern.length; ++i){// initialize newPatterPattern with all dashes
            newPattern[i] = '-';
        }
        lettersGuessed.add(guess); //add the user's guess to the guess count list
        String[] wordArray = wordSet.toArray(new String[wordSet.size()]); //turn wordSet into an array
        List<Character> lettersGuessedList = new ArrayList<>(lettersGuessed); //turn lettersGuessed set into a list
        makePattern(wordArray, lettersGuessedList, newPattern, mostOccurrentValue);
        for (int i = 0; i < currentPattern.length; ++i){
            if (mostOccurrentPattern[i] == guess){ //count the number of times the input appears in the most recurrent pattern
                countOccurances++; //increment each time the input variable appears in the pattern
            }
        }
    if (countOccurances == IS_EMPTY){
        currentGuess -= INCREMENT; //lower the number of guesses
    }
    checkRemove(guess, wordArray);
    occurancesMap.clear(); //reset the map
    return countOccurances;
    }

    public void makePattern(String wordArray[], List<Character> lettersGuessedList, char newPattern[], int mostOccurrentValue){
        /** Helper that finds the most common word family.
         * 
         * This method is a helper for the record method. It finds and constructs
         * patterns based on the letters in lettersGuessedList, and
         * finds the most frequently occuring pattern.
         * 
         * @param wordArray an array version of wordSet that has all the words being considered
         * @param lettersGuessedList a list of the letters that have been guessed
         * @param newPattern has the pattern that represents the occurances of the letters in letterGuessedList for each letter in the wordSet
         * @param mostOccurentValue is tied to the pattern that appears the most frequently
        */
        for (int i = 0; i < wordSet.size(); ++i){ //loop throught wordArray
            displayMostOccurrent = "";
            String wordAtIndex = wordArray[i];
            for (int j = 0; j < wordAtIndex.length(); ++j){
                char characterAtIndex = wordAtIndex.charAt(j); //get word at index i, and get character in the word at index j
                for (int k = 0; k < lettersGuessedList.size(); ++k){
                    if (lettersGuessedList.get(k) == characterAtIndex){
                        newPattern[j] = lettersGuessedList.get(k);
                    }
                }
            }
            if (occurancesMap.containsKey(Arrays.toString(newPattern))){ //if the pattern is already in the key
                int incrementOccurrances = occurancesMap.get(Arrays.toString(newPattern)); //increment the value
                incrementOccurrances += 1;
                if (incrementOccurrances > mostOccurrentValue){ //update mostOccuranctValue with the largest number of occurances and mostOccurantPattern with the associated pattern
                    for (int j = 0; j < mostOccurrentPattern.length; ++j){
                        mostOccurrentPattern[j] = newPattern[j]; //set mostOccurentPattern equal to newPattern
                    }
                    mostOccurrentValue = incrementOccurrances;
                }
                occurancesMap.replace(Arrays.toString(newPattern), incrementOccurrances); //replace it
            }else{ //make a new entry in the map
                occurancesMap.put(Arrays.toString(newPattern), BASE_POSITION);
                if (wordSet.size() == BASE_POSITION){
                    for (int j = 0; j < mostOccurrentPattern.length; ++j){
                        mostOccurrentPattern[j] = newPattern[j]; //set mostOccurentPattern equal to newPattern
                    }                 
                }
            }
            createDisplay();
            displayMostOccurrent += mostOccurrentPattern[mostOccurrentPattern.length - 1];
            for (int j = 0; j < newPattern.length; ++j){ //reset the pattern
                newPattern[j] = '-';
            }
        }
    }
    
    public void checkRemove(int guess, String wordArray[]){
        /** Helper that refreshes the wordSet with the appropriate words.
         * 
         * This is a helper method for the record method. This method
         * removes every word from the wordSet that does not correspond
         * with the most common word family.
         * 
         * @param guess the user-inputted guess.
         * @param wordArray the array form of wordSet that contains all of the words being considered.
        */
        for (int i = 0; i < wordArray.length; ++i){ //check each word in the array
            String wordAtIndex = wordArray[i];
            for (int j = 0; j < wordAtIndex.length(); ++j){
                char charAtIndex = wordAtIndex.charAt(j);
                if ((mostOccurrentPattern[j] == '-' && charAtIndex == guess) || (mostOccurrentPattern[j] 
                == guess && charAtIndex != mostOccurrentPattern[j])){ //and is not a dash
                    wordSet.remove(wordAtIndex); //remove the word from wordSet
                    break;
                }
            }
        }
    }
    public void createDisplay(){
        /** Creates the string to be displayed.
         * 
         * This method is a helper for HangmanManager
         * and makePattern. It converts the current
         * largest word family in string format.
         */
        for(int i = 0; i < (mostOccurrentPattern.length - BASE_POSITION); ++i){
            displayMostOccurrent += mostOccurrentPattern[i];
            displayMostOccurrent += ' ';
        }
    }
}