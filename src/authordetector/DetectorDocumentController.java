package authordetector;

import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class DetectorDocumentController {

    Book currentBook;
    DataBase library = new DataBase();
    Statistic currentStat;
    String author;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea dbShower;

    @FXML
    private TextArea sigDisplay;

    @FXML
    private TextArea book;

    @FXML
    private TextField writer;

    @FXML
    private Button authorSetter;

    @FXML
    private Button dbLoader;

    @FXML
    private Button bookAnalyzer;

    @FXML
    private Button mystery;

    @FXML
    private Button sigSaver;

    @FXML
    private Button sigDisplayer;

    @FXML
    private Label avgWordLength;

    @FXML
    private Label hapaxRatio;

    @FXML
    private Label typeToken;

    @FXML
    private Label wordsPerSentence;

    @FXML
    private Label sentenceComplexity;
    
    @FXML
    private Label punctFreq;

    @FXML
    void loadSigs(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("Text Files", "*.txt"),
             new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(null);
        library.conjureSigs(selectedFile);
        dbShower.appendText(library.getAllLines());
    }

    @FXML
    void readBook(ActionEvent event) {
        author = writer.getText();
        String allLines;
        currentBook = new Book();
        FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
             new FileChooser.ExtensionFilter("Text Files", "*.txt"),
             new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(null);
        allLines = currentBook.readBook(selectedFile);
        book.setText(currentBook.getTitle() + "\n" + allLines);
        
        library.addBook(currentBook);
       
        currentStat = new Statistic();
        
        currentStat.findStats(allLines);
        displayStat();
        
        if(!"".equals(author)) {
            library.addStatToAuthor(author, currentStat.getTotalStats());
        }
    }

    void displayStat(){
        currentStat.printStat();
        avgWordLength.setText(Double.toString(currentStat.getWordLength()));
        typeToken.setText(Double.toString(currentStat.getTypeToken()));
        hapaxRatio.setText(Double.toString(currentStat.getHapax()));
        wordsPerSentence.setText(Double.toString(currentStat.getWordPerScentence()));
        sentenceComplexity.setText(Double.toString(currentStat.getPhrasePerSentence()));
        punctFreq.setText(Double.toString(currentStat.getPunctFreq()));
        sigDisplay.setText(library.getBookNames() + "\n");
    }
    
    @FXML
    void saveSigs(ActionEvent event) {
        FileChooser writeFC = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        writeFC.getExtensionFilters().add(extFilter);
        //Show save file dialog
        writeFC.setInitialDirectory(new File(System.getProperty("user.home")));
        File writeFile = writeFC.showSaveDialog(null);
        if (writeFile != null) {
            library.saveFile(writeFile);   //NOTE HERE - sigFile is my signature database file that I loaded
        }
    }

    @FXML
    void setAuthor(ActionEvent event) {
        author = writer.getText();
        if (library.getAuthors().contains(author)){
                currentStat = new Statistic(library.getStats().get(library.getAuthors().indexOf(author)));
                System.out.println("Author Set!\n");
            }
        else{
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Author not in Database!");
            alert.setContentText("Do you want to add them?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                // ... user chose OK
                currentStat = new Statistic();
                library.addAuthor(author, currentStat);

            }
        }
    }

    @FXML
    void showSigs(ActionEvent event) {
        dbShower.clear();
        for (String current : library.getAuthors()) {
            System.out.println(current + ": ");
            dbShower.appendText(current + ": ");
            for (Double get : library.getStats().get(library.getAuthors().indexOf(current))) {
                System.out.println(get);
                dbShower.appendText(Double.toString(get) + " ");
            }
            dbShower.appendText("\n");
        }
    }

    @FXML
    void solveMyth(ActionEvent event) {
        String bestMatch;
        if(book.getText() == null || book.getText().equals("")){     
            
        }else{
            bestMatch = library.findAuthor(currentStat.getTotalStats());
        
            System.out.println("The author is " + bestMatch);
            
            //Once the author is determined, add this book's stats to the authors stats.
            library.addStatToAuthor(bestMatch, currentStat.getTotalStats());
            
            //Pop up menu for aesthetic
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Author Determined!");
            alert.setHeaderText("The author is " + bestMatch);
            alert.setContentText("At least... I'm pretty certain it is");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                // ... user chose OK
            }
        }
    }

    @FXML
    void initialize() {
        
        assert dbShower != null : "fx:id=\"dbShower\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert sigDisplay != null : "fx:id=\"sigDisplay\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert book != null : "fx:id=\"book\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert writer != null : "fx:id=\"writer\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert authorSetter != null : "fx:id=\"authorSetter\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert dbLoader != null : "fx:id=\"dbLoader\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert bookAnalyzer != null : "fx:id=\"bookAnalyzer\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert mystery != null : "fx:id=\"mystery\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert sigSaver != null : "fx:id=\"sigSaver\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert sigDisplayer != null : "fx:id=\"sigDisplayer\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert avgWordLength != null : "fx:id=\"avgWordLength\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert hapaxRatio != null : "fx:id=\"hapaxRatio\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert typeToken != null : "fx:id=\"typeToken\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert wordsPerSentence != null : "fx:id=\"wordsPerSentence\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert sentenceComplexity != null : "fx:id=\"sentenceComplexity\" was not injected: check your FXML file 'DetectorDocument.fxml'.";
        assert punctFreq != null : "fx:id=\"punctFreq\" was not injected: check your FXML file 'DetectorDocument.fxml'.";

    }
}
