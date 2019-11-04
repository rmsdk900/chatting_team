package yyg.rere.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

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
	num int,
    rName char(50) not null,
    
    primary key (num)
);

alter table member_list add index (id);
alter table member_list add index (num);
 */

public class ChatServer {

	private static int u_count = 0;
	private static int r_count = 0;
	private static boolean running = true;
	static ServerSocket serverSocket;
	static HashMap<Integer, Socket> online = new HashMap<>();				// online user
	static HashMap<Integer, ArrayList<Integer>> rooms = new HashMap<>();	// room control
	
	public static void main(String [] args) {
		try {
			serverSocket = new ServerSocket();
/*////////*/serverSocket.bind(new InetSocketAddress("192.168.1.31", 5001));
			System.out.println("[Server open]");
			
			Thread t = new Thread(() -> {
				while (running) {addClient();}
			});
			t.start();
		} catch (IOException e) {
			System.out.println("[Server open fail] : " + e.getMessage());
			stopServer();
			return;
		}
	}
	
	private static void stopServer() {
		System.out.println("Server Boom!");
		try {
			for (int a : online.keySet()) {
				// what order?
				online.get(a).close();		
				online.remove(a);
			}
			if(serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException e) {}
	}
	
	private static synchronized void addClient() {
		System.out.println("(Come on client...)");
		try {
			Socket socket = null;
			socket = serverSocket.accept();
			if (socket != null) {
				int s_num = u_count;
				online.put(u_count++, socket);
				System.out.println("new client" + s_num + " accepted");
				receive(s_num, socket);
			}
			else {
				return;
			}
		} catch (IOException e) {
			System.out.println("'addClient' STOP : " + e.getMessage());
		}
	}
	
	private static synchronized void delClient(int num) {
		System.out.println("client" + num + " is in 'delClient'");
		
		// what order?
		whole:
		for (int a : rooms.keySet()) {
			for (int b = 0; b < rooms.get(a).size(); b++) {
				if (rooms.get(a).get(b) == num) {rooms.get(a).remove(b);}
				if (rooms.get(a).size() == 0)	{rooms.remove(a);}
				break whole;
			}
		}
	
		try {
			online.get(num).close();
		} catch (IOException e) {
			System.out.println("fail 'delClient' in removing socket");
			e.printStackTrace();
		}
		
		online.remove(num);
	}
	
	private static void receive(int s_num, Socket socket) {
		System.out.println("client" + s_num + " in 'receive' function");
		Thread t = new Thread(() -> {	// what is this lambda expression about?
			while(true) {
				try {
					byte[] bytes = new byte[100];
					InputStream is = socket.getInputStream();					
					
					int readByte = is.read(bytes);
					
					String sender = ((InetSocketAddress)socket.getRemoteSocketAddress()).getHostName();
					String data = new String(bytes, 0, readByte, "UTF-8");
					
					// tokenize data
					StringTokenizer st = new StringTokenizer(data, "|");
					int request = Integer.parseInt(st.nextToken());
					int option = Integer.parseInt(st.nextToken());
					
					String message = "";
					int count = 0;
					while(st.hasMoreTokens()) {
						if (count != 0) message += "|";
						message = st.nextToken();
					}
					
					
					switch(request) {	// request|option|message
						case 1: // make a room and enter
							int temp = r_count++;
							rooms.put(temp, new ArrayList<Integer>());
							setRoom(temp, message);
							rooms.get(temp).add(s_num);
							break;
						case 2: // enter the room			[option]
							rooms.get(option).add(s_num);
							break;
						case 3: // room out					[option]
							rooms.get(option).remove(s_num);
							if (rooms.get(option).size() == 0)	{
								rooms.remove(option);
								delRoom(option);
							}
							break;
						case 4: // message to the room		[option]
							send(option, message);
							break;
						case 5: // logout
							delClient(s_num);
							break;
						default:
							break;
					}
				} catch (Exception e) {
					delClient(s_num);
					System.out.println("[Connection Fail or Over] : " + socket.getRemoteSocketAddress());
					break;
				}
			}				
		});
		t.start();
	}
	
	private static void send(int r_num, String message) {
		System.out.println("from room" + r_num + ", got some message");
		try {
			OutputStream os = null;
			Socket socket = null;
			for (int a = 0; a < rooms.get(r_num).size(); a++) {
				socket = online.get(rooms.get(r_num).get(a));
				os = socket.getOutputStream();
				byte[] bytes = message.getBytes("UTF-8");
				os.write(bytes);
				os.flush();		
			}
		} catch (Exception e) {
			System.out.println("Error in 'send'");
		}
	}
	
	public static void setRoom(int num, String rName) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
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
			
			sql = "INSERT INTO room_list (num, rName) values (" + num + ", '" + rName + "');";
			rs = stmt.executeQuery(sql);			// 결과값
			if (rs.next())	return;
			
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
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
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
			
			sql = "delete from room_list where num =" + num;
			rs = stmt.executeQuery(sql);			// 결과값
			if (rs.next())	return;
			
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
		
		return;
	}
	
	public static int getterNum(String id) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
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
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
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
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
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
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
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

	public static void join(String name, String pw, String nick_name) {	// need arguments
// assume that "nick_name" is not empty or blank
		String sql;
				
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
		Connection conn = null;	// 데이터 베이스 연결 정보
		Statement stmt = null;	// 연결정보를 가지고 질의 전송을 도와주는 객체
		ResultSet rs = null;	// 질의에 대한 결과값이 있으면 결과값을 담는 객체
		
		try {
//
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
				int result = stmt.executeUpdate(sql);	// 실행 완료된 행의 갯수
				
				sql = "select id from member_list;";
				rs = stmt.executeQuery(sql);			// 결과값

				unique_name = true;
				while(rs.next()) {						// rs.next : true(exist) <=> false(null)
					String name_list = rs.getString(1);
					if (name_list.equals(name)) {
						System.out.println("Already exist same name.");	// 동명이인 가능성 배제
						unique_name = false;
						break;
					} 
				}

				if (unique_name) {
					 sql = "INSERT INTO member_list (id, pw, nName) values ('" + name + "', '" + pw + "', '" + nick_name + "');";
					System.out.println(sql);
					result = stmt.executeUpdate(sql);
				}
			}
		} catch (ClassNotFoundException e1) {
			System.out.println("class error");
			e1.printStackTrace();
		} catch (SQLException e1) {
			System.out.println("sql error");
			e1.printStackTrace();
		}
	}
	
	public static boolean login(String name, String pw) {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/myjava?useSSL=false";
		String username = "myjava";
		String password = "12345";
		
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

			sql = "select * from member_list where id='" + name + "';";
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
}
