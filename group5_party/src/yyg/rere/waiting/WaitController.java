package yyg.rere.waiting;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import yyg.rere.login.LoginController;
import yyg.rere.waiting.roommodel.RoomModel;

public class WaitController implements Initializable{
	
	@FXML private TableView<UserListVO> userTable;
	@FXML private TextField roomName;
	@FXML private Button btnCreateRoom;
	@FXML private FlowPane roomFlowPane;
	
	// stage 불러오기
	private Stage primaryStage;
	// loginController 불러오기
	private LoginController controller;
	// 생성자
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Thread updateWait = new Thread(()->{
			while (true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(()->update());
			}
		}); 
		updateWait.setDaemon(true);
		updateWait.start();
		
		
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
			
			Label lblUserCount = (Label) newRoom.lookup("#lblUserCount");
			lblRoomName.setText(roomTitle);
			
			roomFlowPane.getChildren().add(newRoom);
			
			// loginController의 메소드 실행하기
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("login/login.fxml"));
//			LoginController controller = loader.getController();
			controller.createRoom(roomTitle);
			
			
			
			
			
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
	// 새로고침
	public void update() {
		controller.updateRoomList();
		controller.updateUserList();
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}


	public void setLoginController(LoginController loginController) {
		this.controller = loginController;
		
	}

	// 리스트를 userTable에 띄우기
	public void updateUsers(HashMap<Integer, String> namesList) {
		// 리스트 내용 추가
		ObservableList<UserListVO> iul = FXCollections.observableArrayList();
		for ( Map.Entry<Integer, String> elem: namesList.entrySet()) {
			int key = elem.getKey();
			String value = elem.getValue();
			iul.add(new UserListVO(key, value));
		}
		//Class 정보 들고 오기
		Class<UserListVO> uClass = UserListVO.class;
		// 필드 정보 가져오기
		Field[] fields = uClass.getDeclaredFields();
		//컬럼 불러오기
		TableColumn<UserListVO, ?> numTc = userTable.getColumns().get(0);
		TableColumn<UserListVO, ?> nickTc = userTable.getColumns().get(1);
		// 컬럼 값 넣기
		numTc.setCellValueFactory(new PropertyValueFactory<>(fields[0].getName()));
		nickTc.setCellValueFactory(new PropertyValueFactory<>(fields[1].getName()));
		
		// 중앙 정렬하자
		numTc.setStyle("-fx-alignment:center");
		nickTc.setStyle("-fx-alignment:center");
		
		// 최종적으로 리스트 집어넣기
		userTable.setItems(iul);
	}


	public void updateRooms(String[] rNames) {
		// 일단 지울까?
		while(!roomFlowPane.getChildren().isEmpty()) {
			roomFlowPane.getChildren().remove(0);
		}
		
		// 방 이름 만큼 형성해야 함.
		for(int i=0; i<rNames.length;i++) {
			Parent room;
			
			try {
				room = FXMLLoader.load(RoomModel.class.getResource("roommodel.fxml"));
				Label lblRoomName = (Label) room.lookup("#lblRoomName");
				
//				Label lblUserCount = (Label) room.lookup("#lblUserCount");
				lblRoomName.setText(rNames[i]);
				
				Platform.runLater(()->{
					roomFlowPane.getChildren().add(room);
				});
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return;
	}
	
}
