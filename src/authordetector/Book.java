/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authordetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Kyle
 */
public class Book {
    private String title, allLines;
    
    public Book() {
        title = allLines = "";
    }
    
    public String readBook(File selectedFile){
        Path myPath = Paths.get(selectedFile.getAbsolutePath());
        try{
            allLines = new String(Files.readAllBytes(myPath));
        }catch(IOException e){
            System.out.println("Could not find file");
        }
        
        title = myPath.getFileName().toString();
        
        return allLines;
    }

    public String getTitle() {
        return title;
    }

    public String getAllLines() {
        return allLines;
    }
    
    
}
