package mysql.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
	
	private final static String DRIVER = "com.mysql.jdbc.Driver";
	private final static String URL = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
	private final static String USER = "myjava";
	private final static String PASS = "12345";
	
	private static Connection conn = null;
	
	private DBHelper() {}
	
	public static Connection getConnection() {
		if(conn == null) {
			try {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection(URL,USER,PASS);
			} catch (ClassNotFoundException e) {
				System.out.println("DRIVER 를 찾을 수 없음 "+e.getMessage());
			} catch (SQLException e) {
				System.out.println("DB 정보 오류 "+e.getMessage());
			}
		}
		return conn;
	}
	
	public static void close(AutoCloseable closer) {
		try {
			if(closer != null) closer.close();
		} catch (Exception e) {}
	}
	/*
	public static void close(PreparedStatement pstmt) {
		try {
			if(pstmt != null)pstmt.close();
		} catch (SQLException e) {}
	}	
	public static void close(Statement stmt) {
		try {
			if(stmt != null)stmt.close();
		} catch (SQLException e) {}
	}	
	public static void close(ResultSet rs) {
		try {
			if(rs != null)rs.close();
		} catch (SQLException e) {}
	}
	public static void close(Connection conn) {
		try {
			if(conn != null)conn.close();
		} catch (SQLException e) {}
	}
	
	*/
	
	
	
	
}







