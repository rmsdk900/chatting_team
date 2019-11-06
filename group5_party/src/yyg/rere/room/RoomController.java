package yyg.rere.room;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import yyg.rere.waiting.WaitController;


public class RoomController {
//	@FXML private TextArea txtArea;
//	@FXML private TextField txtInput;
//	@FXML private Button btnSend;
	
	WaitController waitController;
	
	TextArea txtArea;
	TextField txtInput;
	Button btnSend;
	
	public RoomController(WaitController waitController, String rName) {
		this.waitController = waitController;
		enterRoom(rName);
	}

	private void enterRoom(String rName) {
		try {
			Parent room = FXMLLoader.load(getClass().getResource("partyroom.fxml"));
			
			// 변수 생성해놓자.
			txtArea = (TextArea) room.lookup("#txtArea");
			txtInput = (TextField) room.lookup("#txtInput");
			btnSend = (Button) room.lookup("#btnSend");
			
			
			
			Stage stage = new Stage();
			Scene scene = new Scene(room);
			stage.setTitle(rName);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

	public void setWaitController(WaitController waitController) {
		this.waitController = waitController;
	}


	public void sendMessage() {
		System.out.println("보내기 버튼 클릭");
		
	}

	
	
}
