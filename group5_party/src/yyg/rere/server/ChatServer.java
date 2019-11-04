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

public class ChatServer {

	private static int u_count = 0;
	private static int r_count = 0;
	private static boolean running = true;
	static ServerSocket serverSocket;
	static HashMap<Integer, Socket> online = new HashMap<>();				// online user
	static HashMap<Integer, ArrayList<Integer>> rooms = new HashMap<>();	// room control
//	Socket socket;
	
//	// 가져올 클라이언트들
//	class PartyUser {
//		
//		int uNumber;
//		String uId;
//		String uPassword;
//		String uName;
//		Socket socket;
//		
//		PartyUser(Socket socket){
//			this.socket = socket;
//			
//		}
//		
//	}
	
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
							System.out.println(isOk);
							String bool=""+isOk;
							sendNotice(1,0,bool);
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
							send(option, message);
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
	
	private static void sendNotice(int req, int rNum, String message) {
		try {
			OutputStream os = online.get(0).getOutputStream();
//			OutputStream os = online.get(u_count).getOutputStream();
			message = req+"|"+rNum+"|"+message;
			byte[] bytes = message.getBytes("UTF-8");
			os.write(bytes);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	
	
}
