/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authordetector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author Kyle
 */
public class Statistic {
    private ArrayList<String> words, onceWord;
    private float numUniqueWords, numOnceUsedWords, totalWords, numChar, numSent, numPhr;
    private double typeToken, hapax, wordLength, wordPerScentence, phrasePerSentence, punctFreq;
    private ArrayList<Double> totalStats;


    public Statistic(ArrayList<Double> stat) {
        wordLength = stat.get(1);
        typeToken = stat.get(2);
        hapax = stat.get(3);
        wordPerScentence = stat.get(4);
        phrasePerSentence = stat.get(5);
        if(stat.size() > 6)
            punctFreq = stat.get(6);
            
            
        onceWord = new ArrayList<>();
        totalStats = new ArrayList<>();
        words = new ArrayList<>();
        
        for (int i = 0 ; i < 5 ; i++)
            totalStats.add(stat.get(i));

    }
    
    public Statistic() {
        onceWord = new ArrayList<>();
        totalStats = new ArrayList<>();
        words = new ArrayList<>();
        for (int i = 0 ; i < 7 ; i++)
            totalStats.add(0.0);
    }

    public void findStats(String ln) {
        typeToken = hapax = wordLength = wordPerScentence = punctFreq = phrasePerSentence = numUniqueWords = numOnceUsedWords = totalWords = numChar = numSent = numPhr= 0;
        setLines(ln);
        findNumWords(ln);
        findAveWordLength();
        typeTokenRatio();
        wordsPerSent();
        hapaxLegomanaRatio();
        sentComplexity(ln);
        punctFrequency(ln);
        
        totalStats.set(1, wordLength);
        totalStats.set(2, typeToken);
        totalStats.set(3, hapax);
        totalStats.set(4, wordPerScentence);
        totalStats.set(5, phrasePerSentence);
        totalStats.set(6, punctFreq);
    }
    
    public void setLines(String ln) {
        String[] st = ln.split("[?!.]+");
        numSent = st.length;
        System.out.println(numSent + " Sentences\n");
    }
        
    public void findNumWords(String ln) {
        String [] ts = ln.split("[\\W]+");
        totalWords = ts.length;
        System.out.println(totalWords + " Words \n");
        
        for (int i = 0 ; i < totalWords ; i++){
            words.add(ts[i]);
        }
        
        findUniqueWords(words);
        findNumChar(ln);
        
    }
    
    public void findUniqueWords(ArrayList<String> words){
        for (int i = 0 ; i < totalWords ; i++){
            words.set(i, words.get(i).toLowerCase());
        }
        //find out which words are only used once using two sets
        Set<String> uniques = new HashSet<>();
        Set<String> rest = new HashSet<>();
        
        for (String n: words){
            if (!uniques.add(n)) //If the word was already added to the set, it can't be added again
                rest.add(n);   //So if a word was already present in the first set, it's second recurrence is added to this set
        }                      //The rest of the occurences are ignored cause their dumb and pointless
        
        //Adds all the words and then 
        onceWord.addAll(uniques);
        onceWord.removeAll(rest);
        
        numOnceUsedWords = onceWord.size();
        System.out.println(numOnceUsedWords + " once \n");
        
        //Since the set "Uniques" already has each word in it, this is just the size of that. avoids creating a new set
        numUniqueWords = uniques.size();
        System.out.println(numUniqueWords + " uniques \n");
        
    }

    public void findNumChar(String ln) {
        //Remove all punctuation from initial string and take length
        String chars = ln.replaceAll("\\W", ""); //\\W searches for non-word chars and removes them
        numChar = chars.length();
        System.out.println(numChar + " chars\n");
    }

    public void punctFrequency(String ln){
        //Remove all characters except puctuation
        String puncts = ln.replaceAll("[^(\\p{Punct})]+", ""); //\\p{Punct} searches for all punctuation and the ^ makes it "Not" whatever is after it
        int numPuncts = puncts.length();                       //So it should remove anything that is not a punctuation mark
        punctFreq = numPuncts / numSent;
        System.out.println(numPuncts + " punctuatuions\n");
    }

    public void typeTokenRatio() {
        typeToken = numUniqueWords / totalWords;
    }
    
    public void wordsPerSent(){
        wordPerScentence = totalWords / numSent;
    }
    
    public void hapaxLegomanaRatio() {
        hapax = numOnceUsedWords / totalWords;
    }

    public void sentComplexity(String ln) {
        String[] st = ln.split("[,.:;?!]+");
        numPhr = st.length;
        System.out.println(numPhr + " phrases\n");
        
        phrasePerSentence = numPhr / numSent;
    }

    public void findAveWordLength() {
        wordLength = numChar/ totalWords;
    }

    public void printStat(){
        totalStats.forEach((n) -> {
            System.out.print(n.toString() + " ");
        });
        System.out.println("\n");
    }

    public double getPunctFreq() {
        return punctFreq;
    }
            
    public double getHapax() {
        return hapax;
    }
        
    public double getPhrasePerSentence() {
        return phrasePerSentence;
    }

    public double getTypeToken() {
        return typeToken;
    }


    public double getWordLength() {
        return wordLength;
    }

    public double getWordPerScentence() {
        return wordPerScentence;
    }
    
    public ArrayList<Double> getTotalStats() {
        return totalStats;
    }
}
