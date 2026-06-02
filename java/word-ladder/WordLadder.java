import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class WordLadder {


	/** Returns true if the words are one letter different
	 * 
	 * @param word1
	 * @param word2
	 * @return
	 */
	private boolean oneAway(String word1, String word2){
		int count = 0;
		if (word1.length() == word2.length()){
			for (int i = 0; i < word1.length() ;++i){
				if(word1.charAt(i) == word2.charAt(i)){
					count ++;
				}
			}
		}
		return (word1.length() - count) == 1;
	}

	/**Determines if there is a word ladder from from to to going through
	 * at least one of the rung words. You may only move to the next word if
	 * there is only one letter different
	 * 
	 * 
	 * @param words - the list of possible rung words
	 * @param from - the initial starting from word
	 * @param to - the initial ending word in the ladder
	 * @return - "ladder" if a ladder exists, or "none" if there is no ladder
	 * */
	
	public String ladderExists(List<String> words, String from, String to) {
		if (words.isEmpty()){
			return "none";
		}
		for(String word : words){
			if(oneAway(word, from)){
				List<String> temp = new LinkedList<>(words);
				temp.remove(word);
				if(ladderExists(temp, word, to).equals("ladder") || oneAway(word,to));
					return "ladder";
			}
		}
		return "none";
	}
	
	
	public static void main(String[] args){
		WordLadder ladder = new WordLadder();
		
		List<String> words = new LinkedList<String>(Arrays.asList("hot", "dot", "dog")); 
		System.out.println(ladder.ladderExists(words, "hit", "cog")); //ladder	
		
		words = new LinkedList<String>(Arrays.asList("mist", "fist", "fish")); 
		System.out.println(ladder.ladderExists(words, "lost", "cost")); //none*/
		
		String[] w = {"pits", "fits", "sits", "bits", "hits", "pots", "pins", "pats", "puts", "pets", "pips", "site", "mite", "bite", "fite", "pine", "pate", "mile", "mole" };
		words = new LinkedList<String>(Arrays.asList(w));
		System.out.println(ladder.ladderExists(words, "pith", "vole")); // ladder
		
	}
}
