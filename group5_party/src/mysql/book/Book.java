package mysql.book;

import java.util.Date;

public class Book {
	
	// 도서관리 번호
	private int num;
	// 도서 제목
    private String title;
	// 도서 저자
	private String author;
	//도서 등록일
	private Date regdate;
	
	public Book() {}

	public Book(int num, String title, String author) {
		this.num = num;
		this.title = title;
		this.author = author;
	}
	
	public Book(int num, String title, String author, Date regdate) {
		super();
		this.num = num;
		this.title = title;
		this.author = author;
		this.regdate = regdate;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	@Override
	public String toString() {
		return "Book [num=" + num + ", title=" + title + ", author=" + author + ", regdate=" + regdate + "]";
	}

}