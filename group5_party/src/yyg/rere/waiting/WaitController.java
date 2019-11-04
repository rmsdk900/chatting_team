package yyg.rere.waiting;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import yyg.rere.waiting.roommodel.RoomModel;

public class WaitController implements Initializable{
	
	@FXML private TableView userTable;
	@FXML private TextField roomName;
	@FXML private Button btnCreateRoom;
	@FXML private FlowPane roomFlowPane;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		roomName.requestFocus();
		
		roomName.setOnKeyPressed(key->{
			if(key.getCode().equals(KeyCode.ENTER)) {
				btnCreateRoom.fire();
			}
		});
		
		
		
		btnCreateRoom.setOnAction(event->{
			CreateRoom();
		});
	}


	public void CreateRoom() {
		String roomTitle = roomName.getText();
		if(roomTitle.equals("")) {
			roomTitle = "이름없음";
		}
		String roomOwner = "root";
		// 새로운 방 아이콘
		Parent newRoom; 
		
		// 채팅 방
		Parent room;
		try {
			newRoom = FXMLLoader.load(RoomModel.class.getResource("roommodel.fxml"));
			Label lblRoomName = (Label) newRoom.lookup("#lblRoomName");
			Label lblRoomOwner = (Label) newRoom.lookup("#lblRoomOwner");
			Label lblUserCount = (Label) newRoom.lookup("#lblUserCount");
			lblRoomName.setText(roomTitle);
			lblRoomOwner.setText(roomOwner);
			roomFlowPane.getChildren().add(newRoom);
			
			
			
			room = FXMLLoader.load(getClass().getResource("../room/partyroom.fxml"));
			Stage stage = new Stage();
			stage.setTitle(roomTitle);
			Scene scene = new Scene(room);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
			// 있던 창 숨기기 (네가 원한다면)
//			txtId.getScene().getWindow().hide();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
}
