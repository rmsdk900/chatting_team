package yyg.rere.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

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

// 학교에서는 이거 쓰자
// 이전 드라이버 : "com.mysql.jdbc.Driver"
// 이전 url : jdbc:mysql://localhost:3306/myjava?useSSL=false

// 집에서는 이거
//static String driver = "com.mysql.cj.jdbc.Driver";
//static String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

public class DB {
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
	static String username = "myjava";
	static String password = "12345";
	
	
	public static int getterNum(String id) {
		
		
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

	public static void join(String id, String pw, String nick_name) {	// need arguments
		String sql;
		
		Connection conn = null;	// 데이터 베이스 연결 정보
		Statement stmt = null;	// 연결정보를 가지고 질의 전송을 도와주는 객체
		ResultSet rs = null;	// 질의에 대한 결과값이 있으면 결과값을 담는 객체
		
		int result;
		
		try {
//////////////////////////////////////////////////////////
			Scanner sc = new Scanner(System.in);
//////////////////////////////////////////////////////////
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
				result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수
				
				sql = "select id from member_list;";
				rs = stmt.executeQuery(sql);			// 결과값

				unique_name = true;
				while(rs.next()) {						// rs.next : true(exist) <=> false(null)
					String name_list = rs.getString(1);
					if (name_list.equals(id)) {
						System.out.println("Already exist same name.");	// 동명이인 가능성 배제
						unique_name = false;
						break;
					} 
				}

				if (unique_name) {
					 sql = "INSERT INTO member_list (id, pw, nName) values ('" + id + "', '" + pw + "', '" + nick_name + "');";
					System.out.println(sql);
					result = stmt.executeUpdate(sql);
				}
			}
			
			sc.close();
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
	}
	
	public static boolean login(String id, String pw) {
	
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

			sql = "select * from member_list where id='" + id + "';";
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
	
	public static void setRoom(int num, String rName) {
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
			stmt = conn.createStatement();			// 연결정보를 가지고 질의 전송을 도와주는 객체
			
			sql = "use project;";
			result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수

			sql = "INSERT INTO room_list (num, rName) values ("+num+", '"+rName+"');";
			rs = stmt.executeQuery(sql);			// 결과값 
			
			if (rs.next()) return;
			
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
		
		return;
	}
	
	public static void delRoom(int num) {
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
			stmt = conn.createStatement();			// 연결정보를 가지고 질의 전송을 도와주는 객체
			
			sql = "use project;";
			result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수

			sql = "delete from room_list where num = " + num;
			rs = stmt.executeQuery(sql);			// 결과값 
			
			if (rs.next()) return;
			
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
		
		return;
	}
	
	
	public static void main(String[] args) {
		// boolean exist_table(int, String), void join(), boolean login(String, String)
		/*if (existInTable("a", "member_list")) {
			System.out.println(1);
		} else {
			System.out.println(0);
		}*/
//		DB db = new DB();
//		if (db.getterNum("a") == 1) {
//			System.out.println("getterNum success");
//		} else {
//			System.out.println("getterNum fail");
//		}
//		
//		if (db.getterPw("a").equals("b")) {
//			System.out.println("getterPw success");
//		} else {
//			System.out.println("getterPw fail");
//		}
//		
//		if (db.getterNn("a").equals("c")) {
//			System.out.println("getterNum success");
//		} else {
//			System.out.println("getterNum fail");
//		}
		
		return;
	}
}