/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Arco;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnConnessioneMassima"
    private Button btnConnessioneMassima; // Value injected by FXMLLoader

    @FXML // fx:id="btnCollegamento"
    private Button btnCollegamento; // Value injected by FXMLLoader

    @FXML // fx:id="txtMinuti"
    private TextField txtMinuti; // Value injected by FXMLLoader

    @FXML // fx:id="cmbMese"
    private ComboBox<String> cmbMese; // Value injected by FXMLLoader

    @FXML // fx:id="cmbM1"
    private ComboBox<Match> cmbM1; // Value injected by FXMLLoader

    @FXML // fx:id="cmbM2"
    private ComboBox<Match> cmbM2; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doConnessioneMassima(ActionEvent event) {
    	if(model.getGraph()==null)
    		txtResult.setText("Devi prima creare il grafo");
    	txtResult.appendText("\nLe coppie di match con peso MAX sono:\n");
    	txtResult.appendText(model.getCoppieTOP());
    
    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	String meseString;
    	Integer mese=0;
    	try {
    		meseString=cmbMese.getValue();
    		if(meseString==null) {
    			txtResult.setText("Scegli un mese");
    			return;
    		}
    	}catch(NullPointerException npe) {
    		txtResult.appendText("Scegli un mese");
    		return;
    	}
    	
    	switch(meseString) {
    		case "Gennaio":
    			mese=1;
    			break;
    		case "Febbraio":
    			mese=2;
    			break;
    		case "Marzo":
    			mese=3;
    			break;
    		case "Aprile":
    			mese=4;
    			break;
    		case "Maggio":
    			mese=5;
    			break;
    		case "Giugno":
    			mese=6;
    			break;
    		case "Luglio":
    			mese=7;
    			break;
    		case "Agosto":
    			mese=8;
    			break;
    		case "Settembre":
    			mese=9;
    			break;
    		case "Ottobre":
    			mese=10;
    			break;
    		case "Novembre":
    			mese=11;
    			break;
    		case "Dicembre":
    			mese=12;
    			break;
    		default:
    			mese=0;
    			break;
    	}
    	
    	Integer MIN;
    	try {
    		MIN=Integer.parseInt(txtMinuti.getText()) ;
    	}catch(NumberFormatException nfe) {
    		txtResult.appendText("Inserisci un numero intero di minuti");
    		return;
    	}
    	catch(NullPointerException npe) {
    		txtResult.appendText("Inserisci un numero intero di minuti");
    		return;
    	}
    	
    	this.model.creaGrafo(mese,MIN);
    	
    	txtResult.appendText("Caratteristiche del GRAFO:\n#VERTICI = "+this.model.getNVertici()+"\n#ARCHI = "+this.model.getNArchi()+"\n");
    	
    	cmbM1.getItems().addAll(model.getVertici());
    	cmbM2.getItems().addAll(model.getVertici());
    	
    }

    @FXML
    void doCollegamento(ActionEvent event) {
    	if(model.getGraph()==null)
    		txtResult.setText("Devi prima creare il grafo");
    	
    	Match partenza;
    	Match arrivo;
    	try {
    		partenza= cmbM1.getValue();
    		arrivo=cmbM2.getValue();
    	}
    	catch(NullPointerException npe) {
    		txtResult.appendText("Scegli un match di partenza e uno di arrivo");
    		return;
    	}
    	
    	List <Match> percorsoTOP = model.trovaPercorso(partenza, arrivo);
    	txtResult.appendText("\nIl percorso di peso MAX è:\n");
    	for(Match m: percorsoTOP)
    		txtResult.appendText(m+"\n");
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnConnessioneMassima != null : "fx:id=\"btnConnessioneMassima\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCollegamento != null : "fx:id=\"btnCollegamento\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtMinuti != null : "fx:id=\"txtMinuti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbMese != null : "fx:id=\"cmbMese\" was not injected: check your FXML file 'Scene.fxml'.";        assert cmbM1 != null : "fx:id=\"cmbM1\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbM2 != null : "fx:id=\"cmbM2\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	String[] mesi = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
    	
    	cmbMese.getItems().addAll(mesi);
  
    }
    
    
}
