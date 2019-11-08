package yyg.rere.DB;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;


public class RoomModel implements Initializable{

	@FXML private Label lblRoomName, lblUserCount;
	@FXML private AnchorPane roomIcon;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		roomIcon.setOnMouseClicked(event->{
			enterRoom();
		});
	}


	public void enterRoom() {
		System.out.println("입장하기");
		
	}
	
	
	
}
