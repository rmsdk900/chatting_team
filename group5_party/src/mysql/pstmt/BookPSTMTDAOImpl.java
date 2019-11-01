package mysql.pstmt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import mysql.book.Book;
import mysql.util.BookDAO;
import mysql.util.DBHelper;

public class BookPSTMTDAOImpl implements BookDAO{
	
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	
	

	@Override
	public int insert(Book book) {
		int result = 0;
		try {
			conn = DBHelper.getConnection();
			String sql = "INSERT INTO tbl_book(title,author) VALUES(?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, book.getTitle());
			pstmt.setString(2, book.getAuthor());
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("pstmt insert 오류 : " + e.getMessage());
		} finally {
			DBHelper.close(pstmt);
		}
		return result;
	}

	@Override
	public int update(Book book) {
		// TODO Auto-generated method stub
		int result = -1;
		try {
			conn = DBHelper.getConnection();
			String sql = "Select * from tbl_book";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			int n = book.getNum();
			while(rs.next()) {
				if (n == rs.getInt("Num")) {
					sql = "update tbl_book set Title=? where Num=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, book.getTitle());
					pstmt.setInt(2, book.getNum());
					result = pstmt.executeUpdate();
					
					sql = "update tbl_book set Author=? where Num=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, book.getAuthor());
					pstmt.setInt(2, book.getNum());
					result = pstmt.executeUpdate();
				}
				break;
			}
			
			
			
			sql = "INSERT INTO tbl_book(title,author) VALUES(?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, book.getTitle());
			pstmt.setString(2, book.getAuthor());
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("pstmt insert 오류 : " + e.getMessage());
		} finally {
			DBHelper.close(pstmt);
		}
		return result;
	}

	@Override
	public ArrayList<Book> select() {
		ArrayList<Book> books = new ArrayList<Book>();
		
		try {
			conn = DBHelper.getConnection();
			String sql = "select * from tbl_book";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			String t, a;
			int n;
			
			while(rs.next()) {
				n = rs.getInt("Num");
				a = rs.getString("Author");
				t = rs.getString("Title");
				Book B = new Book(n, t, a);
				books.add(B);
			}
		} catch (Exception e) {
			System.out.println("pstmt insert 오류 : " + e.getMessage());
		} finally {
			DBHelper.close(pstmt);
		}
				
		return books;
	}

	@Override
	public Book selectBook(int num) {
		int answer = 0;
		Book B = new Book ();
		try {
			conn = DBHelper.getConnection();
			String sql = "select * from tbl_book";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
						
			int n;
			while(rs.next()) {
				n = rs.getInt("Num");
				if (n == num) {
					B.setNum(rs.getInt("Num"));
					B.setTitle(rs.getString("Title"));
					B.setAuthor(rs.getString("Author"));
					// B.setRegdate(rs.getString);
					
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("pstmt insert 오류 : " + e.getMessage());
		} finally {
			DBHelper.close(pstmt);
		}
				
		return B;
	}

	@Override
	public int delete(int num) {
		int result = -1;
		try {
			conn = DBHelper.getConnection();
			String sql = "select * from tbl_book";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			int n;
			while(rs.next()) {
				n = rs.getInt("Num");
				if (n == num) {
					sql = "delete from tbl_book where num = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, n);
					
					result = pstmt.executeUpdate();
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("pstmt insert 오류 : " + e.getMessage());
		} finally {
			DBHelper.close(pstmt);
		}
				
		return result;
	}

}