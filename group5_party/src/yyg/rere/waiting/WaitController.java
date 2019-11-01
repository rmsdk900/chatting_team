package yyg.rere.waiting;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class WaitController implements Initializable{
	
	@FXML private TableView userTable;
	@FXML private ListView roomList;
	@FXML private TextField roomName;
	@FXML private Button btnCreateRoom;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
}
