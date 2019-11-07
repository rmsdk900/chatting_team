package yyg.rere.waiting;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import yyg.rere.AppMain;
import yyg.rere.login.LoginController;
import yyg.rere.room.RoomController;
import yyg.rere.waiting.roommodel.RoomIcon;

public class WaitController {
	
	// gui 요소들
	TableView<UserListVO> userTable;
	TextField roomName;
	Button btnCreateRoom;
	// flowpane 때문에 불러와야 함.
	ScrollPane roomContainer;
	FlowPane roomFlowPane;
	
	// RoomIcon 관련
	RoomIcon roomIcon;
	AnchorPane roomPane;
	Label lblRoomName;
	
	// 이름없음 방 count
	int cnt = 0;
	
	// 중복 확인 boolean값
//	boolean canRoom;
	
	
	
	// stage 불러오기
	private Stage primaryStage;
	// loginController 불러오기
	private LoginController loginController;
	// RoomModel, RoomController 생성
	
	private RoomController roomController;
	
	public WaitController(LoginController loginController, String id) {
		this.loginController = loginController;
		// 새 화면 띄우자
		try {
			FXMLLoader loader = new FXMLLoader(AppMain.class.getResource("waiting/waitingroom.fxml"));
//			//fxml파일에 대한 정보를 가져옴
			Parent root = loader.load();
			
			roomContainer = (ScrollPane) root.lookup("#roomContainer");
			roomFlowPane = (FlowPane)roomContainer.getContent();
			userTable = (TableView<UserListVO>) root.lookup("#userTable");
			roomName = (TextField) root.lookup("#roomName");
			btnCreateRoom = (Button) root.lookup("#btnCreateRoom");
			
			
			
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle(id+"님의 대기실");
			stage.setResizable(false);
			stage.setOnCloseRequest(e->{
				// 서버에 로그아웃 요청 보내기
				loginController.send(6, -1, loginController.getMyNick());
				// 나말고 다른 사람한테만 리스트 요청 보내기
				loginController.send(7,1,"0");
			});
			stage.show();
		//	primaryStage.hide();
			
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
					
					
		roomName.requestFocus();
		roomName.setOnKeyPressed(key->{
			if(key.getCode().equals(KeyCode.ENTER)) {
				btnCreateRoom.fire();
			}
		});
			
		btnCreateRoom.setOnAction(event->{
			String roomTitle = roomName.getText();
			if(roomTitle.equals("")) {
				roomTitle = "이름없음"+cnt;
				cnt++;
			}
			// 방제 중복 확인 요청
//			loginController.send(2,1,roomTitle);
//			try {
//				Thread.sleep(500);
//				canRoom = Boolean.parseBoolean(loginController.getCanRoom());
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}			
//			System.out.println("불린 바뀌었니?"+canRoom);
//			if(canRoom) {
//				System.out.println("현재 방제는?"+roomTitle);
				// loginController의 메소드 실행하기
				loginController.createRoom(roomTitle);
				// 방에 들어가기	
				loginController.enterRoom(roomTitle, loginController.getMyNick());
				
//			}
			roomName.clear();
			
		});
	}
	
	
	
	public LoginController getLoginController() {
		return loginController;
	}



	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}


	public void setLoginController(LoginController loginController) {
		this.loginController = loginController;
		
	}

	// 리스트를 userTable에 띄우기
	public void updateUsers(HashMap<Integer, String> namesList) {
		System.out.println("유저리스트 업뎃");
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
//
//
	public void updateRooms(String[] rNames) {
		// 일단 지울까?
		if(!roomFlowPane.getChildren().isEmpty()) {
			Platform.runLater(()->{
				roomFlowPane.getChildren().clear();
			});
		}
		
		for(int i=0;i<rNames.length;i++) {
			roomIcon= new RoomIcon(this, rNames[i]);
		}
		return;
	}
	
	public FlowPane getRoomFlowPane() {
		return roomFlowPane;
	}

	
}
