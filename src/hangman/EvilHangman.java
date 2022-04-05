package hangman;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class EvilHangman {

    public static void main(String[] args) {
        File dictionaryFile  = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);
        int num = Integer.parseInt(args[2]); //guessNumber
        String updatedWord = "";
        for(int w = 0;  w < wordLength; w++){
            updatedWord += "-";
        }
        EvilHangmanGame newGame = new EvilHangmanGame();
        Set<String> d = null;

        try {
            newGame.startGame(dictionaryFile, wordLength);
        } catch(EmptyDictionaryException e){
            System.out.println("ERROR: Dictionary is empty.");
            System.exit(0);
        }catch(IOException e){ //can't find file
            System.out.println("ERROR: Cannot find dictionary.");
            System.exit(0);

        }


        while(num > 0){
            System.out.print("You have " + num + " guesses left" + "\n");
            System.out.print("Used letter: " + newGame.guesses.toString() + "\n");
            System.out.print("Word: " + updatedWord + "\n");
            Scanner scanner = new Scanner (System.in);
            System.out.print("Enter guess: ");
            String input = scanner.next();

            if(input == null || !input.matches("[a-zA-Z]+") || input.length() > 1 ){
                System.out.println("Invalid input!");
            }else if(input.length() >= 1){
                for(int i = 0; i < input.length(); i++){
                    if(Character.isLetter(input.charAt(i))){
                        char guess = input.charAt(i);
                        try{
                            d = newGame.makeGuess(guess);
                            if(!d.iterator().next().contains(String.valueOf(guess))){ //bad guess
                                System.out.println("Sorry, there are no " + guess + "\n");
                                num --;
                            }else if(d.iterator().next().contains(String.valueOf(guess))){ //good guess
                                String[] words = d.toArray(new String[d.size()]);
                                String str = words[0];
                                int count = 0;
                                StringBuilder string = new StringBuilder(updatedWord);
                                for(int j = 0; j < str.length(); j++) {
                                    if(str.charAt(j) == guess){
                                        string.setCharAt(j, guess);
                                        updatedWord = string.toString();
                                        count++;
                                   }
                                }
                                System.out.println("Yes there is " +  count + " " + guess + "\n");
                                if(!updatedWord.contains("-")){
                                    System.out.println("You win! You guessed the word: " + updatedWord);
                                    System.exit(0);
                                }
                            }
                        }catch(GuessAlreadyMadeException e){
                            System.out.println("Guess already made! ");
                            System.out.print("Enter guess: ");
                        }
                    }
                }
            }
        }

        if(updatedWord.contains("-")){
            //first item in the set
            List<String> list_ = new ArrayList<>(d);
            System.out.println("Sorry, you lost! The word was: " + list_.get(0));

        }
    }


}
