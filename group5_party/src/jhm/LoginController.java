package jhm;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController implements Initializable{
	
	@FXML private TextField txtId;
	@FXML private PasswordField txtPw;
	@FXML private Button btnLogin, btnSignup, btnExit;
	
	private Stage dialog;
	
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
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// 로그인 버튼 눌렀을 때
		btnLogin.setOnAction((event)->{
			System.out.println("로그인");
			// 등록할 로그인 정보 콘솔에 출력
			String loginId = txtId.getText();
			String loginPw = txtPw.getText();
			System.out.println(loginId);
			System.out.println(loginPw);
			
// login(loginId, loginPw);

			
			// 대기실 창 열기
			dialog = new Stage();
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(txtId.getScene().getWindow());
			dialog.setTitle("대기실");
		});
		// 회원가입 버튼 눌렀을 때;
		btnSignup.setOnAction((event)->{
			System.out.println("회원가입");
			dialog = new Stage();
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(txtId.getScene().getWindow());
			dialog.setTitle("회원가입");
			
			try {
				Parent login = FXMLLoader.load(getClass().getResource("signup.fxml"));
				
				TextField txtNewId = (TextField) login.lookup("#txtNewId");
				Button confirmId = (Button) login.lookup("#confirmId");
				PasswordField txtNewPw = (PasswordField) login.lookup("#txtPw");
				PasswordField txtPwChk = (PasswordField) login.lookup("#txtPwCheck");
				TextField txtName = (TextField) login.lookup("#txtName");
				TextField txtNick = (TextField) login.lookup("#txtNick");
				Button btnReg = (Button) login.lookup("#btnReg");
				Button btnCancel = (Button) login.lookup("#btnCancel");
				
				btnReg.setDisable(true);
				
				btnCancel.setOnAction(e->{
					dialog.close();
				});

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
				
				// 등록 버튼 눌렀을 시
				btnReg.setOnAction(e->{
					String newId = txtNewId.getText();
					String newPw = txtNewPw.getText();
					String newPwC = txtPwChk.getText();
					// String newName = txtName.getText();
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
					
					dialog.close();
				});
				
				Scene scene = new Scene(login);
				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		});
		
		btnExit.setOnAction((event)->{
			System.out.println("종료");
			Platform.exit();
		});
	}
	
}
