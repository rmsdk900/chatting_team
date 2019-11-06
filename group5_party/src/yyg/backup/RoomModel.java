//package yyg.backup;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.ResourceBundle;
//
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.layout.AnchorPane;
//import javafx.stage.Stage;
//import yyg.rere.login.LoginController;
//import yyg.rere.room.RoomController;
//import yyg.rere.waiting.WaitController;
//
//
//public class RoomModel{
//
////	@FXML private Label lblRoomName, lblUserCount;
////	@FXML private AnchorPane roomIcon;
//	
//	WaitController waitController;
//	RoomController roomController;
//	
//	public RoomModel(WaitController waitController, String s) {
//		this.waitController = waitController;
//		Parent room;
//		
//		try {
//			room = FXMLLoader.load(RoomModel.class.getResource("roommodel.fxml"));
//			Label lblRoomName = (Label) room.lookup("#lblRoomName");
//			
////			Label lblUserCount = (Label) room.lookup("#lblUserCount");
//			lblRoomName.setText(s);
//			
//			Platform.runLater(()->{
//				waitController.
////				waitController.roomFlowPane.getChildren().add(room);
//			});
//		
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	public void setWaitController(WaitController waitController) {
//		this.waitController = waitController;
//	}
//	
//	
//	
////	@Override
////	public void initialize(URL location, ResourceBundle resources) {
////		roomIcon.setOnMouseClicked(event->{
////			
////			// 이게 waitController의 enterRoom인데...
////			try {
////				FXMLLoader chatRoom = new FXMLLoader(getClass().getResource("../../room/partyroom.fxml"));
////				Parent room = chatRoom.load();
////				roomController = chatRoom.getController();
////				roomController.setRoomModel(this);
////				Stage stage = new Stage();
////				// 들어갈 방의 이름을 어떻게 받지...
//////				stage.setTitle(roomTitle);
////				Scene scene = new Scene(room);
////				stage.setScene(scene);
////				stage.setResizable(false);
////				stage.show();
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
////		
////			
////		});
////	}
//
//
//	
//	
//	
//	
//}
