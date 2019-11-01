package mysql.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;

public class DatabaseExample {

	public static void main(String[] args) {
		
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
		// 데이터 베이스 연결 정보
		Connection conn = null;
		// 연결정보를 가지고 질의 전송을 도와주는 객체
		Statement stmt = null;
		// 질의에 대한 결과값이 있으면 결과값을 담는 객체
		ResultSet rs = null;
		
		try {
			
			Class.forName(driver);
			conn = DriverManager.getConnection(url,username,password);
			System.out.println("Database 연결 완료 : " + conn.toString());
			
			String sql ="";
			
			stmt = conn.createStatement();
			
			Scanner sc = new Scanner(System.in);
			System.out.println("이름을 입력해주세요 ");
			String name = sc.next();
			
			System.out.println("학과를 입력해주세요");
			String dept = sc.next();
			
			System.out.println("학년을 입력해주세요");
			int grade = sc.nextInt();
			
			sql = "INSERT INTO student(" + 
					"	stu_name," + 
					"	stu_dept," + 
					"	stu_grade," + 
					"	stu_class," + 
					"	stu_gender," + 
					"	stu_height," + 
					"	stu_weight" + 
					")VALUES('"+name+"','"+dept+"',"+grade+",'A','M',170.0,60.0);";
		
			// 실행 완료된 행의 갯수
			int result = stmt.executeUpdate(sql);
			System.out.println(result);
			
			// dept_copy;
			
			sql = "UPDATE dept_copy SET dept_name = 'Programming' WHERE dept_no='d005'";
			result = stmt.executeUpdate(sql);
			System.out.println("update dept_copy result : " + result);
			
			sql = "DELETE FROM dept_copy WHERE dept_no = 'd005'";
			result = stmt.executeUpdate(sql);
			System.out.println("delete dept_copy result : " + result);
			
			sql = "SELECT * FROM student";
			rs = stmt.executeQuery(sql);	// 결과값
			
			// rs.next : true(exist) <=> false(null)
			while(rs.next()) {
				int stu_no = rs.getInt("stu_no");
				String stu_name = rs.getString(2);
				String stu_dept = rs.getString(3);
				int stu_grade = rs.getInt(4);
				char stu_class = rs.getString(5).charAt(0);
				char stu_gender = rs.getString(6).charAt(0);
				double stu_height = rs.getDouble(7);
				double stu_weight = rs.getDouble(8);
				Date stu_date = rs.getTimestamp(9);
				StudentVO vo = new StudentVO(
									stu_no,
									stu_name,
									stu_dept,
									stu_grade,
									stu_class,
									stu_gender,
									stu_height,
									stu_weight,
									stu_date
								);
				System.out.println(vo);
			}
			
			
		} catch (ClassNotFoundException e) {
			System.out.println("Driver 를 찾을 수 없음");
		} catch (SQLException e) {
			System.out.println("Database 정보 오류");
			e.printStackTrace();
		}
		
		
	}
}
