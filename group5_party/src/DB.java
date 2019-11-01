import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/*
create database project;
use project;

create table member_list(
	num int auto_increment,
    m_name char(20) not null,
    pw char(20) not null,
    primary key (num)
);

create table room_list(
	num int auto_increment,
    r_name char(50) not null,
    
    primary key (num)
); 
 */
public class DB {
	String driver = "com.mysql.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
	String username = "myjava";
	String password = "12345";
	
	Connection conn = null;	// 데이터 베이스 연결 정보
	Statement stmt = null;	// 연결정보를 가지고 질의 전송을 도와주는 객체
	ResultSet rs = null;	// 질의에 대한 결과값이 있으면 결과값을 담는 객체
	

	void join() {
		String sql;
		String name;
		String pw;
		
		int result;
		Scanner sc = new Scanner(System.in);
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("Database 연결 완료 : " + conn.toString());
			
			sql = "";
			stmt = conn.createStatement();
			
			boolean unique_name = true;
			while (unique_name) {
/////////////////////////////////////////////////////////////////
				System.out.println("ID");
				name = sc.next();
				
				System.out.println("PW");
				pw = sc.next();
////////////////////////////////////////////////////////////////
				sql = "use project;";
				result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수
				
				sql = "select m_name from member_list;";
				rs = stmt.executeQuery(sql);			// 결과값
				
				while(rs.next()) {						// rs.next : true(exist) <=> false(null)
					String name_list = rs.getString(1);
					if (name_list.equals(name)) {
						System.out.println("Already exist same name.");	// 동명이인 가능성 배제
						unique_name = false;
					}
				}
			}
			
			
//			sql = "INSERT INTO member_list(" + name + ", " + pw + ");";
			result = stmt.executeUpdate(sql);
			
			sc.close();
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
	}
	
	boolean login(String name, String pw) {
		String sql;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("Database 연결 완료 : " + conn.toString());
			
			sql = "";
			stmt = conn.createStatement();			// 연결정보를 가지고 질의 전송을 도와주는 객체
			
//////////////////////////////////////////////////////////////////
			
			sql = "use project;";
			int result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수
			
			sql = "select * from member_list where m_name=" + name + ");";
			
			rs = stmt.executeQuery(sql);			// 결과값
			if (!rs.next()) {System.out.println("You write wrong NAME.");}
			else {
				String real_pw = rs.getString(3);
				if(real_pw.equals(pw)) {return true;}
			}
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