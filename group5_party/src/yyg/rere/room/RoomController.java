package yyg.rere.room;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class RoomController implements Initializable {
	@FXML private TextArea txtArea;
	@FXML private TextField txtInput;
	@FXML private Button btnSend;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		txtArea.setEditable(false);
		
		txtInput.requestFocus();
		
		txtInput.setOnKeyPressed(key->{
			if(key.getCode().equals(KeyCode.ENTER)) {
				btnSend.fire();
			}
		});
		
		btnSend.setOnAction(event->{
			send();
		});
		
	}

	public void send() {
		System.out.println("보내기 버튼 클릭");
		
	}
	
}
