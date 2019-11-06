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
import java.util.Map;
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
import yyg.rere.Client.PartyUser;
import yyg.rere.DB.DB;
import yyg.rere.waiting.WaitController;
// 로그인 관련 처리하는 녀석이자 socket 관련 처리하는 녀석 main!!
public class LoginController implements Initializable{
	// stage 불러오기
	private Stage primaryStage;
	
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
			String loginId = txtId.getText();
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
//				//서버에 목록들 업데이트 갱신 요청
				updateUserList();
				updateRoomList();
				// id, pw 칸 비우기
				txtId.clear();
				txtPw.clear();
			// 로그인 실패
			}else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("로그인 실패!");
				alert.setHeaderText("로그인에 실패하였습니다.");
				alert.setContentText("아이디나 패스워드를 다시 확인해주세요!");
				alert.showAndWait();
			}
			
		});
		
//		primaryStage.setOnCloseRequest(e->{
//			try {
//				FXMLLoader reloader = new FXMLLoader(getClass().getResource("login.fxml"));
//				Parent login = reloader.load();
//				Stage stage = new Stage();
//				Scene s = new Scene(login);
//				stage.setScene(s);
//				stage.show();
//				
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		});
		// 이 화면이 닫기 요청을 받았을 때
		// 이건 하면 열리고 꺼짐.
//		primaryStage.setOnCloseRequest(e->{
//			send(6,-1,"0");
//			Scene scene = txtId.getScene();
//			primaryStage.setScene(scene);
//			primaryStage.show();
//		});

		
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
					// 서버로 신호 보내자. 
					send(0,1,newId);
					
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
//			InetAddress hm = InetAddress.getByName("192.168.1.41");
			// 서버에 연결 요청 보내기
			socket = new Socket(ip, 5001);
//			socket = new Socket(hm, 5001);
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
		logout();
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logout() {
		System.out.println("로그아웃");
		send(6,-1,serverKey+"");
		
	}





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
			////////
			int c=0;
			////////
			while (true) {
				try {
					byte[] bytes = new byte[512];
					InputStream is = socket.getInputStream();
					/////////////////////////////////////
					c++;
					if(c==3) {
						int rB = is.read(bytes);
						String d = new String(bytes, 0, rB, "UTF-8");
						c++;
					}
					
					/////////////////////////////////
					int readByte = is.read(bytes);
					// 안넘어오면 오류 발생
					if(readByte == -1) throw new IOException();
					
					String data = new String(bytes, 0, readByte, "UTF-8");
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
							if(message.equals("1")) {
								isOk = true;
							}else {
								isOk = false;
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
							// 마지막 , 지우기
							message = message.substring(0, message.length()-1);
							// 방 이름 array
							String[] rNames=message.split(",");
							
							// 이게 안되는 거 같은데
							controller.updateRooms(rNames);
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
	
	public void enterRoom(String title) {
		send(3,-1,title);
	}
	public void delRoom(String title) {
		
	}
	
	
	// 유저리스트
	public void updateUserList(){
		//유저리스트 요청 보내기
		send(7,-1,"0");
		System.out.println("유저 목록 업뎃");
		
		
	}
	// 방 목록
	public void updateRoomList(){
		// 방 목록 요청 보내기
		send(8,-1,"0");
		System.out.println("방 목록 업뎃");
		
	}
	
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setOnCloseRequest(e->{
			try {
				FXMLLoader reloader = new FXMLLoader(getClass().getResource("login.fxml"));
				Parent login = reloader.load();
				Scene s = new Scene(login);
				primaryStage.setScene(s);
				primaryStage.show();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
	

	
	
}
