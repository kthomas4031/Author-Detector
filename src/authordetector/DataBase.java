/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authordetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.abs;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 * @author Kyle
 */
public class DataBase {
    private Set<String> bookNames;
    private List<ArrayList<Double>> stats;
    private List<Book> books;
    private String allLines;
    private ArrayList<String> authors;
    
    public DataBase() {
        authors = new ArrayList<>();
        bookNames = new HashSet<>();
        stats = new ArrayList<>();
        books = new ArrayList<>();
    }
    
    
    void conjureSigs(File selectedFile){
        Path myPath = Paths.get(selectedFile.getAbsolutePath());
            try{
            allLines = new String(Files.readAllBytes(myPath));
            }catch(IOException e){
                    System.out.println("Could not find file");
            }
        //Parse into individual lines to separate each author's stats
        String delim = "\\r?\\n";
        String[] st = allLines.split(delim);
        int length = st.length;

        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < length ; i++){
            temp.add(st[i]);
            System.out.println(temp.get(i) + "\n");
        }
            
        ArrayList<Double> go;   
        for (int i = 0; i < length ; i++) {
            //Remove author from beginning of line and add to authors list
            String[] bob = temp.get(i).split("[ ]+|\t");
            authors.add(bob[0]);
            System.out.println(authors.get(i) + "\n");
            //Assign space for each of the 7 stats provided (including number of books)
            //so that even if the loaded signature file doesn't contain all 7, there is no out od bounds later on
            go = new ArrayList<>();
            for (int k = 0 ; k < 7 ; k++)
                go.add(0.0);
            
            //Replace 0.0 with proper stats at the same index as the inputted author
            for (int j = 1 ; j < bob.length ; j++)
                go.set(j-1, Double.parseDouble(bob[j]));           
            stats.add(go);
        }
    }

    public void saveFile(File sigfile){
         try {
      FileWriter fileWriter; 
      fileWriter = new FileWriter(sigfile); 
      // Output strings of the Signature File database to be written
             DecimalFormat f = new DecimalFormat(".00000");
             for (String n : authors){
                 fileWriter.write(n + " ");
                 for (Double num : stats.get(authors.indexOf(n)))
                     fileWriter.write(f.format(num) + " ");
                 fileWriter.write(System.getProperty("line.separator"));
             }
      fileWriter.close();
    } catch (IOException ex) {
       }
    }
    
    public void addStatToAuthor(String author, ArrayList<Double> currentStat){
        int index = authors.indexOf(author);
        //Increase book num by 1
        double booknum = stats.get(index).get(0);
        stats.get(index).set(0, (booknum+1));
        
        for (int i = 1; i < currentStat.size() ; i++){
            if (stats.get(index).get(i) == 0)
                stats.get(index).set(i, currentStat.get(i));
            else{
                double newVal = (booknum*(stats.get(index).get(i))+ currentStat.get(i))/(booknum + 1);
                stats.get(index).set(i, (newVal));
            }
        }
    }
    
    public String findAuthor(ArrayList<Double> currentStat){
        //Create a list of weights to use to do the summation math easier
        ArrayList<Double> weights = new ArrayList<>();
        weights.add(0.0);
        weights.add(19.0);
        weights.add(78.8);
        weights.add(97.6);
        weights.add(1.225);
        weights.add(2.7);
        weights.add(1.1);
                
        double sim = 0;
        
        //initialize the most likely author to be the first one so that there is something to compare the others to
        for (int j = 1 ; j < currentStat.size(); j++){
            sim += abs((currentStat.get(j) - stats.get(0).get(j))*weights.get(j));
            System.out.println(sim + " " + authors.get(0));
        }
        
        String bestMatch = authors.get(0);

        
        for (int i = 1 ; i < authors.size() ; i++){
            double temp = 0;
            //Run summation of differences to find stat similarity for each author
            for(int k = 1 ; k < currentStat.size() ; k++){
                temp += abs((currentStat.get(k) - stats.get(i).get(k))*weights.get(k));
                System.out.println(temp + " " + authors.get(i));
            }
            
            //if that author's stats are better than the previous best, make that one the best
            if (temp < sim){
                bestMatch = authors.get(i);
                sim = temp;
            }
        }
        return bestMatch;
    }
    
    public void addBook(Book addedBook){
        if(bookNames.contains(addedBook.getTitle()))
            System.out.println("Book already in Library!");
        else{
            books.add(addedBook);
            bookNames.add(addedBook.getTitle());
        }
    }
    
    public void addAuthor(String author, Statistic addedStat){
        authors.add(author);
        stats.add(addedStat.getTotalStats());
                        
        System.out.println("Author Added!");
    }
    
    public String getAllLines() {
        return allLines;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<ArrayList<Double>> getStats() {
        return stats;
    }

    public List<Book> getBooks() {
        return books;
    }
    
    public void setStats(List<ArrayList<Double>> stats) {
        this.stats = stats;
    }

    public Set<String> getBookNames() {
        return bookNames;
    }

}
