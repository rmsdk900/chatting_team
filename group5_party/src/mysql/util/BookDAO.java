package mysql.util;

import java.util.ArrayList;

import mysql.book.Book;

public interface BookDAO {

	public int insert(Book book);
	
	public int update(Book book);
	
	public ArrayList<Book> select();
	
	public Book selectBook(int num);
	
	public int delete(int num);
}
