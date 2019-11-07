package yyg.rere.room;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import yyg.rere.login.LoginController;
import yyg.rere.waiting.WaitController;


public class RoomController {
	
	WaitController waitController;
	LoginController loginController;
	
	TextArea txtArea;
	TextField txtInput;
	Button btnSend;
	
	// 방 번호
	int rNumber;
	
	public RoomController(LoginController loginController, int rNumber, String rName) {
		this.loginController = loginController;
		this.rNumber = rNumber;
		enterRoom(rName);
	}

	private void enterRoom(String rName) {
		try {
			Parent room = FXMLLoader.load(getClass().getResource("partyroom.fxml"));
			
			// 변수 생성해놓자.
			txtArea = (TextArea) room.lookup("#txtArea");
			txtInput = (TextField) room.lookup("#txtInput");
			btnSend = (Button) room.lookup("#btnSend");
			
			
			
			Platform.runLater(()->{
				Stage stage = new Stage();
				Scene scene = new Scene(room);
				stage.setTitle(rName);
				stage.setScene(scene);
				stage.setResizable(false);
				stage.show();
			});
			txtArea.setEditable(false);
			btnSend.setOnMouseClicked(e->{
				String msg = txtInput.getText();
				sendMessage(msg);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

	public void setWaitController(WaitController waitController) {
		this.waitController = waitController;
	}


	public void sendMessage(String msg) {
		System.out.println("보낼 메시지 : "+msg);
		loginController.send(5, rNumber, msg);
	}

	
	
}
