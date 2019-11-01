package mysql.stmt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import mysql.book.Book;
import mysql.util.BookDAO;
import mysql.util.DBHelper;

public class BookSTMTDAOImpl implements BookDAO{
	
	// DB 연결정보
	private Connection conn;
	// DB 질의 전송
	private Statement stmt;
	// DB 질의 결과값 
	private ResultSet rs;
	

	@Override
	public int insert(Book book) {
		int result = 0;
		
		try {
			conn = DBHelper.getConnection();
			stmt = conn.createStatement();
			String sql ="INSERT INTO tbl_book(title,author)"
					 + " VALUES('"+book.getTitle()+"','"+book.getAuthor()+"')";
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Insert 오류  : " + e.getMessage());
		} finally {
			try {
				if(stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {}
		}
		return result;
	}

	@Override
	public int update(Book book) {
		System.out.println(book);
		int result = 0;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.createStatement();
			String sql = "UPDATE tbl_book SET "
					   + " title = '"+ book.getTitle()+"', "
				   	   + " author = '" + book.getAuthor()+"' "
			   		   + " WHERE num = "+book.getNum();
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("UPDATE 오류 : " + e.getMessage());
		}finally {
			try {
				if(stmt != null) stmt.close();
			} catch (SQLException e) {}
		}
		return result;
	}

	@Override
	public ArrayList<Book> select() {
		ArrayList<Book> books = new ArrayList<>();
		
		try {
			conn = DBHelper.getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM tbl_book ORDER BY num DESC";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				int num = rs.getInt(1);
				String title = rs.getString(2);
				String author = rs.getString(3);
				Date regdate = rs.getTimestamp(4);
				Book book = new Book(num,title,author,regdate);
				books.add(book);
				
//				books.add(
//					new Book(
//							rs.getInt(1),
//							rs.getString(2),
//							rs.getString(3),
//							rs.getTimestamp(4)
//					)
//				);
			}
		} catch (SQLException e) {
			System.out.println("select 오류 : " + e.getMessage());
		} finally {
			try {
				if(rs != null)rs.close();
				if(stmt != null) stmt.close();
			} catch (SQLException e) {}
		}
		return books;
	}

	@Override
	public Book selectBook(int num) {
		Book book = null;
		
		try {
			conn = DBHelper.getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM tbl_book WHERE num ="+num;
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				int number = rs.getInt(1);
				String title = rs.getString(2);
				String author = rs.getString(3);
				Date regdate = rs.getDate(4);
				book = new Book(number,title,author,regdate);
			}
		} catch (SQLException e) {
			System.out.println("selectBoo 오류 : " + e.getMessage());
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
			} catch (SQLException e) {}
		}
		return book;
	}

	@Override
	public int delete(int num) {
		int result =0;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.createStatement();
			String sql = "DELETE FROM tbl_book WHERE num="+num;
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("DELETE 오류 : " + e.getMessage());
		} finally {
			try {
				if(stmt != null)stmt.close();
			} catch (SQLException e) {}
		}
		return result;
	}

}
