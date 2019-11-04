package yyg.rere.login;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

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
import yyg.rere.waiting.WaitController;
// 로그인 관련 처리하는 녀석이자 socket 관련 처리하는 녀석
public class LoginController implements Initializable{
	
	// fxml 변수
	@FXML private TextField txtId;
	@FXML private PasswordField txtPw;
	@FXML private Button btnLogin, btnSignup, btnExit;
	
	// 커스텀 다이얼로그 
	private Stage dialog;
	//통신 관련 변수들
	private Socket server;
	private Socket socket;
	
	// 유저 정보 담고 있기
	private PartyUser partyUser;
	
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
			
			boolean isOk =login(loginId, loginPw);
			// 로그인 성공 시 대기실 창 열기
			if(isOk) {
				try {
					
					Parent signUp = FXMLLoader.load(WaitController.class.getResource("waitingroom.fxml"));
					Stage stage = new Stage();
					stage.setTitle("대기실");
					Scene scene = new Scene(signUp);
					stage.setScene(scene);
					stage.setResizable(false);
					stage.show();
					// 있던 창 숨기기 (네가 원한다면)
					txtId.getScene().getWindow().hide();
					
					

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
					if (existInTable(newId, "member_list")) {
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
					
					join(newId, newPw, newNick);
					// 얘도 서버를 통해서 DB에 저장해주기.
					partyUser = new PartyUser(getterNum(newId), newId, newNick, newPw);
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
			InetAddress ip = InetAddress.getByName("192.168.1.31");
			// 서버에 연결 요청 보내기
			socket = new Socket(ip, 5001);
			System.out.println("[ 연결 완료 : "+socket.getRemoteSocketAddress()+"]");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	public static int getterNum(String id) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
		Connection conn = null;	// 데이터 베이스 연결 정보
		Statement stmt = null;	// 연결정보를 가지고 질의 전송을 도와주는 객체
		ResultSet rs = null;	// 질의에 대한 결과값이 있으면 결과값을 담는 객체
		
		String sql;
		int result;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("Database 연결 완료 : " + conn.toString());
			
			sql = "";
			stmt = conn.createStatement();
			
			sql = "use project;";
			result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수
			
			sql = "select num from member_list where id = '" + id + "';";
			rs = stmt.executeQuery(sql);			// 결과값
			if (rs.next())	return rs.getInt(1);
			
			System.out.println("no matching ID.");
			
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
		
		return -1;
	}

	public static String getterPw(String id) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
		Connection conn = null;	// 데이터 베이스 연결 정보
		Statement stmt = null;	// 연결정보를 가지고 질의 전송을 도와주는 객체
		ResultSet rs = null;	// 질의에 대한 결과값이 있으면 결과값을 담는 객체
		
		String sql;
		int result;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("Database 연결 완료 : " + conn.toString());
			
			sql = "";
			stmt = conn.createStatement();
			
			sql = "use project;";
			result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수
			
			sql = "select pw from member_list where id = '" + id + "';";
			rs = stmt.executeQuery(sql);			// 결과값
			if (rs.next())	return rs.getString(1);
			
			System.out.println("no matching ID.");
			
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
		
		return "";
	}	
	
	public static String getterNn(String id) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
		Connection conn = null;	// 데이터 베이스 연결 정보
		Statement stmt = null;	// 연결정보를 가지고 질의 전송을 도와주는 객체
		ResultSet rs = null;	// 질의에 대한 결과값이 있으면 결과값을 담는 객체
		
		String sql;
		int result;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("Database 연결 완료 : " + conn.toString());
			
			sql = "";
			stmt = conn.createStatement();
			
			sql = "use project;";
			result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수
			
			sql = "select nName from member_list where id = '" + id + "';";
			rs = stmt.executeQuery(sql);			// 결과값
			if (rs.next())	return rs.getString(1);
			
			System.out.println("no matching ID.");
			
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
		
		return "";
	}

	public static boolean existInTable(String id, String tName) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
		Connection conn = null;	// 데이터 베이스 연결 정보
		Statement stmt = null;	// 연결정보를 가지고 질의 전송을 도와주는 객체
		ResultSet rs = null;	// 질의에 대한 결과값이 있으면 결과값을 담는 객체
		
		String sql;
		int result;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("Database 연결 완료 : " + conn.toString());
			
			sql = "";
			stmt = conn.createStatement();
			
			sql = "use project;";
			result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수
			
			sql = "select * from " + tName + " where id = '" + id + "';";
			rs = stmt.executeQuery(sql);			// 결과값
				
			while(rs.next()) {						// rs.next : true(exist) <=> false(null)
				String memName = rs.getString(2);
				if (memName.equals(id)) {
					return true;
				} 
			}
			
			return false;
			
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
		
		return false;
	}

	public static void join(String name, String pw, String nick_name) {	// need arguments
// assume that "nick_name" is not empty or blank
		String sql;
				
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
		Connection conn = null;	// 데이터 베이스 연결 정보
		Statement stmt = null;	// 연결정보를 가지고 질의 전송을 도와주는 객체
		ResultSet rs = null;	// 질의에 대한 결과값이 있으면 결과값을 담는 객체
		
		try {
//
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("Database 연결 완료 : " + conn.toString());
// 
			sql = "";
			stmt = conn.createStatement();
// 
			boolean unique_name = false;
			while (!unique_name) {
/////////////////////////////////////////////////////////////////
				System.out.println("ID");
				System.out.println("PW");
				System.out.println("nick_name");
////////////////////////////////////////////////////////////////
				sql = "use project;";
				int result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수
				
				sql = "select id from member_list;";
				rs = stmt.executeQuery(sql);			// 결과값

				unique_name = true;
				while(rs.next()) {						// rs.next : true(exist) <=> false(null)
					String name_list = rs.getString(1);
					if (name_list.equals(name)) {
						System.out.println("Already exist same name.");	// 동명이인 가능성 배제
						unique_name = false;
						break;
					} 
				}

				if (unique_name) {
					 sql = "INSERT INTO member_list (id, pw, nName) values ('" + name + "', '" + pw + "', '" + nick_name + "');";
					System.out.println(sql);
					result = stmt.executeUpdate(sql);
				}
			}
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
	}
	
	public static boolean login(String name, String pw) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
		Connection conn = null;	// 데이터 베이스 연결 정보
		Statement stmt = null;	// 연결정보를 가지고 질의 전송을 도와주는 객체
		ResultSet rs = null;	// 질의에 대한 결과값이 있으면 결과값을 담는 객체
		
		String sql;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("Database 연결 완료 : " + conn.toString());
			
			sql = "";
			stmt = conn.createStatement();			// 연결정보를 가지고 질의 전송을 도와주는 객체
			
			sql = "use project;";
			int result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수

			sql = "select * from member_list where id='" + name + "';";
			rs = stmt.executeQuery(sql);			// 결과값 // rs.next : true(exist) <=> false(null)
			
			if (rs.next()) {
				String memPw = rs.getString(3);
				if (memPw.equals(pw)) {
					System.out.println("login success");
					return true;
				} 
			}
			
			System.out.println("Wrong ID or PW.");
			
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
		
		return false;
	}
	
}
