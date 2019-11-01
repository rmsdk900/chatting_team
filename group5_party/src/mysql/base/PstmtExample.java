package mysql.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class PstmtExample {

	public static void main(String[] args) {
		
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("DB 연결 성공");
			
			String query = "INSERT INTO dept_copy VALUES(?,?)";
			
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, "d030");
			pstmt.setString(2, "Developement");
			int result = pstmt.executeUpdate();
			System.out.println("삽입 결과 :"+result);
			
			
			
			Scanner sc = new Scanner(System.in);
			System.out.println("검색할 부서의 부서번호를 입력하세요 > ");
			String no = sc.next();
			
			String sql = "SELECT * FROM dept_copy WHERE dept_no = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, no);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				System.out.println(rs.getString("dept_no"));
				System.out.println(rs.getString("dept_name"));
			}
			
			
		} catch (ClassNotFoundException e) {
			System.out.println("Driver 연결 실패 : " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("db 연결 실패 or 실행 오류 : " + e.getMessage());
		}finally {
			
			try {
				rs.close();
				pstmt.close();
				conn.close();
				System.out.println("연결 해제 완료");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			
		}
		
		
		

	}

}
