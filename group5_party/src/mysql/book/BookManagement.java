package mysql.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mysql.pstmt.BookPSTMTDAOImpl;
import mysql.util.BookDAO;

public class BookManagement {
	// 문자열 입력
	Scanner scanLine = new Scanner(System.in);
	// 선택번호 입력
	Scanner scanSelectnum = new Scanner(System.in);
	
	// database 연결 객체
	BookDAO dao;
	
	// 도서목록
	List<Book> books = new ArrayList<>();
	// 프로그램 실행 flag
	boolean isRun = true;
	// 메인 메뉴 선택 번호
	int selectNo = 0;

	public BookManagement(BookDAO dao){
		this.dao = dao;
		run();
	}
	
	public void run() {
		while(isRun) {
			System.out.println("================================================");
			System.out.println("1.도서등록 | 2. 도서목록 | 3.도서수정 | 4.도서삭제 | 5. 종료");
			System.out.println("================================================");
			selectNo = getSelectNum("번호를 선택하세요>");
			
			switch(selectNo) {
				case 1 :
					registerBook();
					break;
				case 2 :
					selectBook();
					break;
				case 3 :
					updateBook();
					break;
				case 4 : 
					deleteBook();
					break;
				case 5 : 
					terminate();
					break;	
				default : 
					System.out.println("등록된 메뉴가 아닙니다.");
			
			}
		}
		
	}
	
	// 프로그램 종료
	public void terminate() {
		System.out.println("프로그램 종료");
		isRun =false;
	}

	// 도서 등록
	public void  registerBook() {
		System.out.println("1. 도서등록");
		String title = getData("등록할 도서의 제목을 입력해주세요 >");
		String author = getData("등록할 도서의 저자를 입력해주세요 >");
		Book book = new Book();
		book.setTitle(title);
		book.setAuthor(author);
		dao.insert(book);
		System.out.println("등록 완료");
	}
	
	// 도서 목록 출력
	public void selectBook() {
		System.out.println("2. 도서목록");
		books = dao.select();
		if(books != null) {
			for(Book b : books) {
				printBookInfo(b);
			}
		}
	}

	// 도서 정보 수정
	public void updateBook() {
		System.out.println("3. 도서수정");
		selectBook();
		int num = getSelectNum("수정할 책의 관리 번호를 입력하세요 > ");
		Book b = dao.selectBook(num);
		if(b == null) {
			System.out.println("등록된 번호의 책이 존재하지 않습니다.");
			return;
		}
		
		
		update : while(true) {
			System.out.println("========================");
			System.out.println("1.제목수정|2.저자수정|3.수정완료");
			System.out.println("========================");
			selectNo = getSelectNum("번호 입력 > ");
			switch(selectNo) {
				case 1 :
					System.out.println("제목수정");
					String title = getData("제목입력 > ");
					b.setTitle(title);
					break;
				case 2 :
					System.out.println("저자수정");
					String author = getData("저자입력 > ");
					b.setAuthor(author);
					break;
				case 3 : 
					dao.update(b);
					System.out.println("수정완료");
					break update;
			}
			
		}
		
	}
	
	// 도서 목록에서 책 정보 삭제
	public void deleteBook() {
		System.out.println("4. 도서삭제");
		int num = getSelectNum("삭제할 도서의 관리번호를 입력해 주세요>");
		Book b = dao.selectBook(num);
		if(b != null) {
			dao.delete(num);
			System.out.println("삭제완료");
			return;
		}
		System.out.println("해당되는 도서번호의 책이 존재하지 않습니다.");
	}
	
	// 책 정보 출력
	public void printBookInfo(Book b) {
		System.out.println(b.toString());
	}
	
	// 도서관리번호로 책 정보 찾기
	public Book findBook(int num) {
		return null;
	}
	
	// 사용자에게 메시지를 전달 받아 출력하고 문자열 값 받아 반환
	String getData(String message) {
		System.out.println(message);
		return scanLine.nextLine();
	}
	
	// 번호 선택 받기
	int getSelectNum(String message) {
		System.out.println(message);
		return scanSelectnum.nextInt();
	}
		
	public static void main(String[] args) {
		//new BookManagement().run();
//		new BookManagement(new BookSTMTDAOImpl());
		new BookManagement(new BookPSTMTDAOImpl());
	}

}
