package yyg.rere.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
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
// 로그인 관련 처리하는 녀석이자 socket 관련 처리하는 녀석
public class LoginController implements Initializable{
	
	// 생성자 ? 
	public LoginController() {
		
	}
	
	// fxml 변수
	@FXML private TextField txtId;
	@FXML private PasswordField txtPw;
	@FXML private Button btnLogin, btnSignup, btnExit;
	
	// 커스텀 다이얼로그 
	private Stage dialog;
	//통신 관련 변수들
	private Socket socket;
	
	// 유저 정보 담고 있기
	private PartyUser partyUser;
	
	// 로그인 여부 확인
	boolean isOk;
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
			String data = loginId+","+loginPw;
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
				try {
					Parent signUp = FXMLLoader.load(WaitController.class.getResource("waitingroom.fxml"));
					Stage stage = new Stage();
					stage.setTitle(loginId+"님의 대기실");
					Scene scene = new Scene(signUp);
					stage.setScene(scene);
					stage.setResizable(false);
					stage.show();
					// 있던 창 숨기기 (네가 원한다면)
//					txtId.getScene().getWindow().hide();
					
					
					

				} catch (IOException e) {
					e.printStackTrace();
				}
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
				TextField txtName = (TextField) login.lookup("#txtName");
				TextField txtNick = (TextField) login.lookup("#txtNick");
				Button btnReg = (Button) login.lookup("#btnReg");
				Button btnCancel = (Button) login.lookup("#btnCancel");
				
				btnReg.setDisable(true);
				
				confirmId.setOnAction(e->{
					System.out.println("중복확인");
					String newId = txtNewId.getText();
					if (DB.existInTable(newId, "member_list")) {
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
					String newName = txtName.getText();
					String newNick = txtNick.getText();
					
					if (!newPw.equals(newPwC)) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("Password and PasswordCheck are different");
						alert.setContentText("Please check your Password");
						alert.showAndWait();
						
						return;
					}
					// 서버 DB에 저장하도록 보내주기
					String newUser = newId+","+newPw+","+newName;
					send(0, -1, newUser);
					
//					DB.join(newId, newPw, newNick);
					// 얘도 서버를 통해서 DB에 저장해주기.
					partyUser = new PartyUser(DB.getterNum(newId), newId, newNick, newPw);
					// 서버로 보내줘야 DB에 저장할 것 같군.
					
					System.out.println(partyUser);

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
	// 서버랑 연결 작업하기
	public void startClient() {
		
		System.out.println("서버에 연결 시도");
		try {
			InetAddress ip = InetAddress.getByName("127.0.0.1");
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
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(int req, int opt, String data) {
		data = req+"|"+opt+"|"+data;
		System.out.println(data);
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
	public void receive() {
		new Thread(()->{
			// 계속
			while (true) {
				try {
					byte[] bytes = new byte[100];
					InputStream is = socket.getInputStream();
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
						message = st.nextToken();
					}
					// req에 따라서 분류 처리
						switch (request) {
						case 0: //join
							
							break;
						case 1: // login
							if(message.equals("true")) {
								isOk = true;
							}else {
								isOk = false;
							}
							break;
						default:
							break;
					}
					
					
					
					System.out.println(data);
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
	//해야 함
	public void createRoom() {
		
		
	}
	
	
	
	

	
	
}
