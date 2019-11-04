package yyg.rere;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import yyg.rere.Client.PartyUser;
import yyg.rere.login.LoginController;
import yyg.rere.room.RoomController;
import yyg.rere.waiting.WaitController;
import yyg.rere.waiting.roommodel.RoomModel;

public class MainController implements Initializable {
	// VO
	PartyUser partyUser;
	
	public MainController() {
		
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
		
		
	}






	public void init() {
		// 창부터 열기
		try {
			Parent root = FXMLLoader.load(getClass().getResource("login/login.fxml"));
			stage = new Stage();
			stage.setTitle("로그인");
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		
	}




	// 스테이지
	Stage stage;

	// 각 컨트롤러 
	LoginController loginController;
	
	WaitController waitController;
	
	RoomModel roomModel;
	
	RoomController roomController;
		
	// 통신관련 변수들
	Socket server;
	
	
	
	
	
	
	// DB
	static class DB {
		/*
		create database project;
		use project;

		create table member_list(
			num int auto_increment,
		    id char(20) not null,
		    pw char(20) not null,
		    nName char(20),
		    primary key (num)
		);

		create table room_list(
			num int auto_increment,
		    rName char(50) not null,
		    
		    primary key (num)
		);

		alter table member_list add index (id);
		alter table member_list add index (num);
		show index from member_list;
		*/
		
		// DB 관련 메소드
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
}
