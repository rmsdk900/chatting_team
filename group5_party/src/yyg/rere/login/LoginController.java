package yyg.rere.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.StringTokenizer;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import yyg.rere.room.RoomController;

import yyg.rere.waiting.WaitController;
// 로그인 관련 처리하는 녀석이자 socket 관련 처리하는 녀석 main!!
public class LoginController implements Initializable{
	// stage 불러오기
	private Stage primaryStage;
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	// 생성자 ? 
	public LoginController() {
	}
	
	// fxml 변수
	@FXML private TextField txtId;
	@FXML private PasswordField txtPw;
	@FXML private Button btnLogin, btnSignup, btnExit;
	
	// 다른 controller
	WaitController controller;
	
	// 커스텀 다이얼로그 
	private Stage dialog;
	//통신 관련 변수들
	private Socket socket;
	
	// 회원가입 여부 확인
	boolean possible;
	
	// 로그인 여부 확인
	boolean isOk;
	
	// 방제 중복 여부 확인
//	String canRoom;
	
	

	// 접속한 내 loginId
	String loginId;
	// 접속한 내 닉네임
	String myNick;
	
	
	
	//로그인한 녀석의 닉네임 불러오기.
	// 로그인한 녀석의 아이디 불러오기
	public String getLoginId() {
		return loginId;
	}
	
	

	public String getMyNick() {
		return myNick;
	}

	//자기가 들어가 있는 방들 정보
	Map<Integer, RoomController> onRooms = new Hashtable<>();
	
	// 서버에 있는 소켓 리스트에서 클라이언트의 소켓이 들어가있는 key값.
	private int serverKey = 0;
	// 
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// 서버와 연결 먼저
		startClient();
		// id에게 먼저 포커스 주기
		txtId.requestFocus();
		
		// 키보드로도 버튼 클릭과 같은 효과 주기
		txtId.setOnKeyPressed(key->{
			if(key.getCode().equals(KeyCode.ENTER)) {
				txtPw.requestFocus();
			}
		});
		
		txtPw.setOnKeyPressed(key->{
			if(key.getCode().equals(KeyCode.ENTER)) {
				btnLogin.fire();
			}
		});
		
		
		
		// 로그인 버튼 눌렀을 때
		btnLogin.setOnAction((event)->{
			System.out.println("로그인");
			// 등록할 로그인 정보 콘솔에 출력
			loginId = txtId.getText();
			String loginPw = txtPw.getText();
			// 로그인 확인
			// data = "id,pw,serverKey";
			String data = loginId+","+loginPw+","+serverKey;
			//로그인 허가 신호 보내기
			send(1, -1, data);
			//받아올 시간이 필요한가 봐
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			// 이것도 서버에서 받아와야 함.
			System.out.println(isOk);
			// 로그인 성공 시 대기실 창 열기
			if(isOk) {
				controller = new WaitController(this, loginId);
				primaryStage.hide();
				try {
					Thread.sleep(1000);
					//서버에 목록들 업데이트 갱신 요청
					updateUserList();
					updateRoomList();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// id, pw 칸 비우기
				txtId.clear();
				txtPw.clear();
				// id에 다시 포커스 주기.
				txtId.requestFocus();
			// 로그인 실패
			}else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("로그인 실패!");
				alert.setHeaderText("로그인에 실패하였습니다.");
				alert.setContentText("아이디나 패스워드를 다시 확인해주세요!");
				alert.showAndWait();
			}
			
		});
		

		
		// 회원가입 버튼 눌렀을 때;
		btnSignup.setOnAction((event)->{
			System.out.println("회원가입");
			// 회원가입창 열기
			dialog = new Stage();
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(txtId.getScene().getWindow());
			dialog.setTitle("회원가입");
			
			try {
				Parent login = FXMLLoader.load(getClass().getResource("signup.fxml"));
				
				//회원가입창 변수들 다 들고오기
				TextField txtNewId = (TextField) login.lookup("#txtNewId");
				Button confirmId = (Button) login.lookup("#confirmId");
				PasswordField txtNewPw = (PasswordField) login.lookup("#txtPw");
				PasswordField txtPwChk = (PasswordField) login.lookup("#txtPwCheck");
				
				Label pwChkMsg = (Label) login.lookup("#pwChkMsg");
				TextField txtNick = (TextField) login.lookup("#txtNick");
				Button btnReg = (Button) login.lookup("#btnReg");
				Button btnCancel = (Button) login.lookup("#btnCancel");
				
				btnReg.setDisable(true);
				
				confirmId.setOnAction(e->{
					System.out.println("중복확인");
					String newId = txtNewId.getText();
					
					try {
						Thread.sleep(500);
						// 서버로 신호 보내자. 
						send(0,1,newId);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					if (possible) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("Same ID exist");
						alert.setContentText("Try another ID");
						alert.showAndWait();
						
						btnReg.setDisable(true);
						return;
					}
					else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Success");
						alert.setHeaderText("You can use this ID");
						alert.setContentText("Move on to next step");
						alert.showAndWait();
						
						btnReg.setDisable(false);
					}
				});
				
				Thread autoCheckPw = new Thread(()->{
					while(true) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						String nPw =txtNewPw.getText();
						String nPwChk = txtPwChk.getText();
						if(nPw.equals(nPwChk)) {
							Platform.runLater(()->{
								pwChkMsg.setStyle("-fx-text-fill: blue;");
								pwChkMsg.setText("패스워드가 일치함");
							});
													
						}else {
							Platform.runLater(()->{
								pwChkMsg.setStyle("-fx-text-fill: red;");
								pwChkMsg.setText("패스워드가 일치하지 않음");
							});
							
						}
					}
				});
				autoCheckPw.setDaemon(true);
				autoCheckPw.start();	
				
				// 제일 쉬운 닫기부터
				btnCancel.setOnAction(e->{
					dialog.close();
				});
				
				
				// 등록 버튼 눌렀을 시
				btnReg.setOnAction(e->{
					// 텍스트 얻어오기
					String newId = txtNewId.getText();
					String newPw = txtNewPw.getText();
					String newPwC = txtPwChk.getText();
					String newNick = txtNick.getText();
					
					
					
					// 닉이 없을 경우 이름 입력하게
					if(newNick.equals("")) {
						newNick = newId;
					}
					
					if (!newPw.equals(newPwC)) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("Password and PasswordCheck are different");
						alert.setContentText("Please check your Password");
						alert.showAndWait();
						
						return;
					}
					// 서버 DB에 저장하도록 보내주기
					String newUser = newId+","+newPw+","+newNick;
					send(0, -1, newUser);
			
					
					dialog.close();
				});

				Scene scene = new Scene(login);
				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.show();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		});
		
		btnExit.setOnAction((event)->{
			System.out.println("종료");
			Platform.exit();
		});
	}
	// 로그아웃할 때(waitingroom 창을 닫았을 때)
	
	
	
	
	// 서버랑 연결 작업하기
	public void startClient() {
		
		System.out.println("서버에 연결 시도");
		try {
			InetAddress ip = InetAddress.getByName("192.168.1.31");
			// 서버에 연결 요청 보내기
			socket = new Socket(ip, 5001);
			System.out.println("[ 연결 완료 : "+socket.getRemoteSocketAddress()+"]");
			// 계속 받기
			receive();
		} catch (UnknownHostException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopClient() {
		System.out.println("연결 종료");
		// 모든 방에서 나가기
		// 키들 뽑아서 방을 다 나가기
		for(Entry<Integer, RoomController> entry :onRooms.entrySet()) {
			exitRoom(entry.getKey());
		}
		// 로그아웃 하기
		logout();
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	




	// 정보보내기
	public void send(int req, int opt, String data) {
		data = req+"|"+opt+"|"+data;
		System.out.println("클라가 보냄"+data);
		try {
			byte[] bytes = data.getBytes("UTF-8");
			OutputStream os = socket.getOutputStream();
			os.write(bytes);
			os.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	// 계속 서버로부터 받기
	// requset|option|message
	/**
	 * request:
	 * 	0: addClient -> server에 저장된 자기 소켓을 이용할 수 있는 key값 
	 * 	1: login permission 
	 * 	2: communication
	 * option:
	 * 	request 상관없이 -1: default
	 * 	request ==2 일때만 : room_number
	 * message:
	 * 	request ==0: server에 있는 자기 소켓을 value로 가지고 있는 key값
	 * 			==1: permission boolean값;
	 * 			==2: message 
	 */
	public void receive() {
		new Thread(()->{
			// 계속
			while (true) {
				try {
					byte[] bytes = new byte[512];
					InputStream is = socket.getInputStream();
					
					int readByte = is.read(bytes);
					// 안넘어오면 오류 발생
					if(readByte == -1) throw new IOException();
					
					String data = new String(bytes, 0, readByte, "UTF-8");
					// 오는 지 확인
					System.out.println("서버로부터 온 것: "+data);
					// tokenize data;
					StringTokenizer st = new StringTokenizer(data, "|");
					int request = Integer.parseInt(st.nextToken());
					
					int option = Integer.parseInt(st.nextToken());
					
					String message = "";
					int count = 0;
					while(st.hasMoreTokens()) {
						if (count != 0) message += "|";
						count++;
						message += st.nextToken();
					}
					// req에 따라서 분류 처리
						switch (request) {
						case 0: //socket 키값 받기.
							if(option==1) {
								possible=Boolean.parseBoolean(message);
								System.out.println(possible);
							} else if(option==-1){
								serverKey = Integer.parseInt(message);
							}
							
							break;
						case 1: // login
							if(message.equals("0")) {
								isOk = false;
							}else {
								isOk = true;
								String[] findNick=message.split(",");
								myNick = findNick[1];
							}
							break;
//						case 2: // 방제 중복 확인
//							canRoom = message;
						case 3: //enterRoom
							// 자기가 들어간 방의 정보를 넣기
							onRooms.put(option, new RoomController(this, option, message));
							break;
						case 4: //exitRoom
							// 자기가 접속한 방 목록에서 해당 녀석 지우기
							onRooms.remove(option);
							
						case 5: // msg
							// 받은 메시지를 그 방번호에 해당하는 방에만 뿌려야 함. 
							if(onRooms.get(option)!=null) {
								onRooms.get(option).displayMessage(message);
							}
							
							break;
						case 6:
							//자기가 나갔을 때
							if(option==-1) {
								// 소켓 닫았다가 다시 열기
								// 로그인 창 다시 열기
								Platform.runLater(()->primaryStage.show());
							// 다른 사람이 나갔을 때
							}else if(option==1) {
								updateUserList();
							}
							
							
							break;
						case 7: // userList
							// 마지막 , 지우기
							message = message.substring(0, message.length()-1);
							// 닉네임 array 만들기.
							String[] names=message.split(",");
							int userCount = 0;
							HashMap<Integer, String> namesList = new HashMap<>();
							for(String s: names) {
								namesList.put(userCount, s);
								userCount++;
							}
							// 이게 실행이 안됨.
							controller.updateUsers(namesList);
							break;
						case 8: // roomList
							if(message.equals("clear")) {
								Platform.runLater(()->controller.getRoomFlowPane().getChildren().clear());
							}else {
								// 마지막 , 지우기
								message = message.substring(0, message.length()-1);
								// 방 이름 array
								String[] rNames=message.split(",");
								
								// 이게 안되는 거 같은데
								controller.updateRooms(rNames);
							}
							break;
						default:
							break;
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					break;
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}).start();
	}
	//서버 데이터베이스에 넣는 것은 됨.
	public void createRoom(String title) {
		send(2, -1, title);
		updateRoomList();
		
		
	}
	
	public void enterRoom(String title, String nick) {
		try {
			Thread.sleep(300);
			send(3,-1,title+","+nick);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	// 나만 방을 나갈 때
		public void exitRoom(int rNumber) {
			// 방 나감 요청
			send(4,rNumber, myNick);
			try {
				Thread.sleep(300);
				// 목록 갱신도 해라..
				send(8,-1,"0");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	
	
	
	
	// 유저리스트
	public void updateUserList(){
		try {
			Thread.sleep(300);
			//유저리스트 요청 보내기
			send(7,-1,"0");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	// 방 목록
	public void updateRoomList(){
		try {
			Thread.sleep(300);
			// 방 목록 요청 보내기
			send(8,-1,"0");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
//	public String getCanRoom() {
//		return canRoom;
//	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;	
	}
	
	//로그아웃하기
		public void logout() {
			System.out.println("로그아웃");
			send(6,-1, myNick);
		}




}
