package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{
    Set<String> Dictionary_ = new HashSet<String>();
    Map<String, Set<String>> groups = new HashMap<> ();
    SortedSet<Character> guesses = new TreeSet<>();
    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        Dictionary_.clear();
        guesses.clear();
        groups.clear();
        File txtfile = new File(String.valueOf(dictionary));
        Scanner read = new Scanner(txtfile);


        if(read.hasNextLine() == false || wordLength ==0){ //
            throw new EmptyDictionaryException();
        }

        int lengthMatch = 0;
        while (read.hasNextLine()) {// add words with wordlength to dictionary
            String word = read.nextLine();
            if(word.length() == wordLength){ //checking wordlength
                lengthMatch ++;
                Dictionary_.add(word);
            }
        }

        if(lengthMatch == 0){ //no words with the wordlength
            throw new EmptyDictionaryException();
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {

        groups.clear();

        guess = Character.toLowerCase(guess);

        Set guessedLetters = getGuessedLetters();
        if(guessedLetters.contains(guess) == true){
            throw new GuessAlreadyMadeException();
        }

        guessedLetters.add(guess);

        Iterator<String> it = Dictionary_.iterator();
        Set<String> groupKeys = new HashSet<>();


        while (it.hasNext()) {
            String word = it.next();
            String pattern = "";
            for(int i = 0; i < word.length(); i++){
                if(word.charAt(i) == guess){
                    pattern += guess;
                }else{
                    pattern += "-";
                }
            }

            if(groups.containsKey(pattern)){
                groups.get(pattern).add(word);
            }else{
                Set<String> words = new HashSet<String>();
                words.add(word);
                groups.put(pattern, words);
                groupKeys.add(pattern);
            }
        }

        //if there is only one pattern
        if(groups.size() == 1){
            String key = (String) groupKeys.toArray()[0];
            Dictionary_ = groups.get(key);
            return Dictionary_;
        }

        //select the largest set
        int maxSize = 0;
        Map<String, Set<String>> biggest = new HashMap<>();
        Set<String> biggestKeys = new HashSet<>();
        for (String key_pattern : groups.keySet())
        {
            if(maxSize < groups.get(key_pattern).size()){
                biggest.clear();
                biggestKeys.clear();
                maxSize = groups.get(key_pattern).size();
                biggest.put(key_pattern, groups.get(key_pattern));
                biggestKeys.add(key_pattern);
            }else if(maxSize == groups.get(key_pattern).size()) {
                biggest.put(key_pattern, groups.get(key_pattern));
                biggestKeys.add(key_pattern);
            }
        }

        if(biggest.size() == 1){
            String key = (String) biggestKeys.toArray()[0];
            Dictionary_ = biggest.get(key);
            return Dictionary_; // new dictionary of words

        }

        //tie breaker, comparing KEYS
        Map<String, Set<String>> x = new HashMap<>(); //no guess
        Map<String, Set<String>> y = new HashMap<>();//fewest letter occurence
        Map<String, Integer> z = new HashMap<>(); //right most

        // pattern with no guess char
        Set<String> xKeys = new HashSet<>();
        for(String k: biggest.keySet()){
            if(!biggest.keySet().contains(guess)){
               x.put(k, biggest.get(k));
               xKeys.add(k);
            }
        }
        if(x.size() == 1){
            String key = (String) xKeys.toArray()[0];
            Dictionary_ = x.get(key);
            return Dictionary_;
        }

        //fewest letter occurence
        Set<String> yKeys = new HashSet<>();
        int maxOccurence = 1000;
        for(String k : x.keySet()){
            if(maxLetter(guess, k) < maxOccurence){
                y.clear();
                yKeys.clear();
                maxOccurence = maxLetter(guess,k);
                y.put(k, x.get(k));
                yKeys.add(k);
            }else if(maxLetter(guess, k) == maxOccurence){
                y.put(k, x.get(k));
                yKeys.add(k);
            }
        }

        if(y.size() == 1){
            String key = (String) yKeys.toArray()[0];
            Dictionary_ = y.get(key);
            return Dictionary_;
        }

        // STILL TIE
        List pattern_= new ArrayList(y.keySet());
        String right = (String) pattern_.get(0);
        for(int i = 1; i < pattern_.size(); i++){
            right = rightMost(right, (String)pattern_.get(i), guess);
        }
        Dictionary_ = y.get(right);
        return Dictionary_;
    }

    public String rightMost(String currP, String newP, char guess){
        for(int i = currP.length()-1; i >=0; i--){
            if(currP.charAt(i) == guess && newP.charAt(i) != guess){
                return currP;
            }else if(currP.charAt(i) != guess && newP.charAt(i) == guess){
                return newP;
            }
        }
        return null;
    }

    public int maxLetter(char guess, String pattern){
        int max = 0;
        for(int i = 0; i < pattern.length(); i++){
            if(pattern.charAt(i) == guess){
                max++;
            }
        }
        return max;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guesses;
    }
}
