package yyg.rere.waiting.roommodel;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import yyg.rere.waiting.WaitController;

public class RoomIcon {
	WaitController waitController;
	
	Label lblRoomName;
	String rName;
	
	AnchorPane roomPane;
	

	public RoomIcon(WaitController waitController, String rName) {
			this.waitController = waitController;
			this.rName = rName;
			
		try {
			Parent roomBox = FXMLLoader.load(getClass().getResource("roommodel.fxml"));
			lblRoomName = (Label) roomBox.lookup("#lblRoomName");
			roomPane = (AnchorPane) roomBox.lookup("#roomIcon");
			lblRoomName.setText(rName);
			
			Platform.runLater(()->waitController.getRoomFlowPane().getChildren().add(roomBox));
			
			roomPane.setOnMouseClicked(e->{
				System.out.println("클릭했다");
				waitController.getLoginController().enterRoom(lblRoomName.getText(), waitController.getLoginController().getMyNick());
			});
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			
			
			
	}
	
	
}
