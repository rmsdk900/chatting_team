//package yyg.backup;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.ResourceBundle;
//
//import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextField;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.input.KeyCode;
//import javafx.scene.layout.FlowPane;
//import javafx.stage.Stage;
//import yyg.rere.login.LoginController;
//import yyg.rere.room.RoomController;
//import yyg.rere.waiting.roommodel.RoomModel;
//
//public class WaitController implements Initializable{
//	
//	@FXML private TableView<UserListVO> userTable;
//	@FXML private TextField roomName;
//	@FXML private Button btnCreateRoom;
//	@FXML private FlowPane roomFlowPane;
//	
////	TableView<UserListVO> userTable;
////	TextField roomName;
////	Button btnCreateRoom;
////	FlowPane roomFlowPane;
//	
//	// stage 불러오기
//	private Stage primaryStage;
//	// loginController 불러오기
//	private LoginController loginController;
//	// RoomModel, RoomController 생성
//	private RoomModel roomModel;
//	private RoomController roomController;
//	
////	public WaitController(LoginController loginController) {
////		this.loginController = loginController;
////		// 새 화면 띄우자
////		try {
////			FXMLLoader loader = new FXMLLoader(getClass().getResource("../waiting/waitingroom.fxml"));
//////			//fxml파일에 대한 정보를 가져옴
////			Parent root = loader.load();
////			this.setPrimaryStage(primaryStage);
////			
////			userTable = (TableView) root.lookup("#userTable");
////			roomName = (TextField) root.lookup("#roomName");
////			btnCreateRoom = (Button) root.lookup("#btnCreateRoom");
////			roomFlowPane = (FlowPane) root.lookup("#roomFlowPane");
////			
////			
////			Scene scene = new Scene(root);
////			primaryStage.setScene(scene);
////			primaryStage.setTitle("대기실");
////			primaryStage.setResizable(false);
////			primaryStage.show();
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
////
////		
////		
////	}
//	
//	
//	@Override
//	public void initialize(URL location, ResourceBundle resources) {
//		
//
//		
//		
//		
//		
//		
//		roomName.requestFocus();
//		
//		roomName.setOnKeyPressed(key->{
//			if(key.getCode().equals(KeyCode.ENTER)) {
//				btnCreateRoom.fire();
//			}
//		});
//		
//		
//		
//		btnCreateRoom.setOnAction(event->{
//			String roomTitle = roomName.getText();
//			if(roomTitle.equals("")) {
//				roomTitle = "이름없음";
//			}
//			// loginController의 메소드 실행하기
//			loginController.createRoom(roomTitle);
//			// 방 목록 업데이트
//			loginController.updateRoomList();
////			createRoom();
//			enterRoom(roomTitle);
//		});
//		
//		
//	}
//
//
//
//	// 방 들어가기
//	public void enterRoom(String rName) {
//		System.out.println("들어간다?");
//		// roomModel에서 title 가져오기
//		
//		RoomController partyRoom = new RoomController(this, rName);
//		
//		
//
//		
//	}
//	// 새로고침
//	public void update() {
//		loginController.updateRoomList();
//	}
//	
//	public void setPrimaryStage(Stage primaryStage) {
//		this.primaryStage = primaryStage;
//	}
//
//
//	public void setLoginController(LoginController loginController) {
//		this.loginController = loginController;
//		
//	}
//
//	// 리스트를 userTable에 띄우기
//	public void updateUsers(HashMap<Integer, String> namesList) {
//		// 리스트 내용 추가
//		ObservableList<UserListVO> iul = FXCollections.observableArrayList();
//		for ( Map.Entry<Integer, String> elem: namesList.entrySet()) {
//			int key = elem.getKey();
//			String value = elem.getValue();
//			iul.add(new UserListVO(key, value));
//		}
//		//Class 정보 들고 오기
//		Class<UserListVO> uClass = UserListVO.class;
//		// 필드 정보 가져오기
//		Field[] fields = uClass.getDeclaredFields();
//		//컬럼 불러오기
//		TableColumn<UserListVO, ?> numTc = userTable.getColumns().get(0);
//		TableColumn<UserListVO, ?> nickTc = userTable.getColumns().get(1);
//		// 컬럼 값 넣기
//		numTc.setCellValueFactory(new PropertyValueFactory<>(fields[0].getName()));
//		nickTc.setCellValueFactory(new PropertyValueFactory<>(fields[1].getName()));
//		
//		// 중앙 정렬하자
//		numTc.setStyle("-fx-alignment:center");
//		nickTc.setStyle("-fx-alignment:center");
//		
//		// 최종적으로 리스트 집어넣기
//		userTable.setItems(iul);
//	}
////
////
//	public void updateRooms(String[] rNames) {
//		// 일단 지울까?
//		Platform.runLater(()->{
//			while(!roomFlowPane.getChildren().isEmpty()) {
//				roomFlowPane.getChildren().clear();
//			}
//		});
//		
//		
//		
//		// 방 이름 만큼 형성해야 함.
//		for(int i=0; i<rNames.length;i++) {
//			Parent room;
//			
//			try {
//				room = FXMLLoader.load(RoomModel.class.getResource("roommodel.fxml"));
//				Label lblRoomName = (Label) room.lookup("#lblRoomName");
//				
////				Label lblUserCount = (Label) room.lookup("#lblUserCount");
//				lblRoomName.setText(rNames[i]);
//				
//				Platform.runLater(()->{
//					roomFlowPane.getChildren().add(room);
//				});
//			
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//		}
//		return;
//	}
//
//	
//}
