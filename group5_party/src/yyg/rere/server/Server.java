package yyg.rere.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import yyg.rere.DB.DB;

class Pair {
	private Socket socket;
	private int num;
	
	public Pair(Socket socket) {
		this.socket = socket;
		this.num = 0;
	}
	public Pair(Socket socket, int num) {
		this.socket = socket;
		this.num = num;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setS(Socket socket) {
		this.socket = socket;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
//	public void send() {
//		
//	}
}

public class Server {

	private static int r_count = 0;
	private static int u_count = 0;
	private static boolean running = true;
	static ServerSocket serverSocket;
	static HashMap<Integer, Pair> online = new HashMap<>();				// online user
	// online 의 Integer
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
				online.get(a).getSocket().close();		
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
				int s_num = u_count;	//////////////////////////////////////
				online.put(u_count++, new Pair(socket));		//////////////////////////
				System.out.println("new client" + s_num + " accepted");
				send(socket, 0, -1, Integer.toString(s_num));
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
			online.get(num).getSocket().close();
		} catch (IOException e) {
			System.out.println("fail 'delClient' in removing socket");
			e.printStackTrace();
		}
		
		online.remove(num);
	}
	// s_num = socket number ; 위에 online의 키 값.
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
						case 0: // join
							String[] infos = message.split(",");
							for(String str : infos) {
								System.out.println(str);
							}
							DB.join(infos[0], infos[1], infos[2]);
							break;
						case 1:	// login
							String[] login = message.split(",");
							boolean isOk = DB.login(login[0], login[1]);
							System.out.println("client" + s_num + " login permission : " + isOk);
							if(isOk) {
								send(socket, 1, -1, "1");
								online.get(s_num).setNum(DB.getterNum(login[0]));
							}
							else	  send(socket, 1, -1, "0");
							break;
						case 2: // make a room and enter
							int temp = r_count++;
							rooms.put(temp, new ArrayList<Integer>());
							DB.setRoom(temp, message);
							rooms.get(temp).add(s_num);
							break;
						case 3: // enter the room			[option]
							rooms.get(option).add(s_num);
							break;
						case 4: // room out					[option]
							rooms.get(option).remove(s_num);
							if (rooms.get(option).size() == 0)	{
								rooms.remove(option);
								DB.delRoom(option);
							}
							break;
						case 5: // message to the room		[option]
							Socket temp_socket;
							for (int a = 0; a < rooms.get(option).size(); a++) {
								temp_socket = online.get(rooms.get(option).get(a)).getSocket();
								send(temp_socket, 2, option, message);		
							}
							break;
						case 6: // logout
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
	
	private static void send(Socket socket, int request, int option, String message) {
		System.out.println("'send'" + socket);
		try {
			OutputStream os = null;
			os = socket.getOutputStream();
			String refined = request + "|" + request + "|" + message; 
			byte[] bytes = refined.getBytes("UTF-8");
			os.write(bytes);
			os.flush();		
		} catch (Exception e) {
			System.out.println("Error in 'send'");
		}
	}
	
}