package yyg.rere.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import yyg.rere.DB.DB;

public class ServerController implements Initializable{
	
	// 서버 포트 설정
	public static final int SERVER_PORT = 5001;
	
	@FXML private TextArea txtDisplay;
	@FXML private Button btnStartStop;
	
	// 스레드 풀 관련
	ExecutorService serverPool;
	// 서버 소켓
	ServerSocket server;
	// 서버 접속자 카운트
	private static int connUserCount = 0;
	private static int room_count = 0;
	// 플래그
	private static boolean running = true;
	// 처음 client 연결 시 정보를 담고 있을 녀석
	List<Socket> connSockets = new ArrayList<>();
	// 접속자들 정보를 담고 있는 Map
	Map<Integer, PartyUser> onUsers = new Hashtable<>();
	
	// 방 리스트
	List<String> onRoomList = new ArrayList<>();
//	Map<Integer, String> onRoomList = new HashMap<>();
	
	// 그 방에 들어간 접속자 들
	Map<Integer, String> onRoomMembers = new HashMap<>();
	
	// 가져올 클라이언트 정보를 담고 있는 녀석
	class PartyUser{
		// 각 유저의 auto-increment
		private int uNumber;
		//유저 아이디
		private String uId;
		// 유저의 이름 or 닉네임
		private String uName;
		
		// 유저의 소켓
		private Socket client;
		// 유저가 속한 방
//		private PartyRoom pRoom;
		
		//생성자
		// id로 유저 객체 만들기
		public PartyUser(String uId) {
			this.uId = uId;
		}
		
		public PartyUser(Socket client) {
			this.client = client;
			receive();
		}
		
		public PartyUser (String uId, Socket client) {
			this.client = client;
			this.uId = uId;
		}
		
		// Getter
		public int getuNumber() {
			return uNumber;
		}
		public String getuId() {
			return uId;
		}
		public String getuName() {
			return uName;
		}
		
		public Socket getClient() {
			return client;
		}
//		public PartyRoom getpRoom() {
//			return pRoom;
//		}
		
		// Setter
		public void setuNumber(int uNumber) {
			this.uNumber = uNumber;
		}
		public void setuId(String uId) {
			this.uId = uId;
		}
		public void setuName(String uName) {
			this.uName = uName;
		}
		
		public void setClient(Socket client) {
			this.client = client;
		}
//		public void setpRoom(PartyRoom pRoom) {
//			this.pRoom = pRoom;
//		}
		
		
		// 기존 메소드들 재정의
		@Override
		public boolean equals(Object obj) {
			// 객체 자체가 똑같을 때
			if(this==obj)return true;
			// 객체가 비어있거나 클래스가 서로 같지 않을 때
			if(obj==null || getClass() != obj.getClass()) return false;
			PartyUser partyUser = (PartyUser) obj;
			return uId == partyUser.uId;
		}
		// autoIncrement로 해시코드 설정하기
		@Override
		public int hashCode() {
			return uNumber;
		}
		@Override
		public String toString() {
			return "PartyUser [uNumber=" + uNumber + ", uId=" + uId + ", uName=" + uName + ", client=" + client + "]";
		}
		
		// 각 클라이언트가 계속 정보를 받는 녀석 불러오기
		public void receive() {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					while(true) {
						try {
							byte[] bytes = new byte[512];
							InputStream is = client.getInputStream();
							int readByte = is.read(bytes);
//						if(readByte == -1)throw new IOException();
							String data = new String(bytes, 0, readByte, "UTF-8");
							
							// tokenize data
							StringTokenizer st = new StringTokenizer(data, "|");
							int request = Integer.parseInt(st.nextToken());
							int option = Integer.parseInt(st.nextToken());
							
							String message = "";
							message = st.nextToken();
							// request에 따라 처리 다르게
							switch(request) {	// request|option|message
								case 0: // join
									String[] infos = message.split(",");
									for(String str : infos) {
										System.out.println(str);
									}
									DB.join(infos[0], infos[1], infos[2]);
									break;
								case 1:	// login
									// login[0] = loginId / login[1] = pw / login[2] = serverKey;
									String[] login = message.split(",");
									boolean isOk = DB.login(login[0], login[1]);
									// 로그인할 경우
									if(isOk) {
										int key = Integer.parseInt(login[2]);
										// 로그인한 유저의 정보를 담는 맵에 넣기
										onUsers.put(connUserCount, new PartyUser(connSockets.get(key)));
										onUsers.get(connUserCount).setuName(DB.getterNn(login[0]));
										connUserCount++;
										send(1, -1, "1");
									// 로그인 실패할 경우
									}else {
										send(1, -1, "0");
									}
									break;
								case 2: // make a room and enter
									// message = title
									System.out.println(message);
									//서버 데이터베이스에 방 이름이랑 번호 추가
									// 방 리스트가 필요 있나?
//									DB.setRoom(room_count, message);
									// 방 리스트에 넣기
									onRoomList.add(message);
									room_count++;
									break;
								case 3: // enter the room			[option]
//									rooms.get(option).add(s_num);
									break;
								case 4: // exit room
//									rooms.get(option).remove(s_num);
//									if (rooms.get(option).size() == 0)	{
//										rooms.remove(option);
//										DB.delRoom(option);
//									}
									break;
								case 5: // message to the room		[option]
//									Socket temp_socket;
//									for (int a = 0; a < rooms.get(option).size(); a++) {
//										temp_socket = online.get(rooms.get(option).get(a)).getSocket();
//										send(temp_socket, 2, option, message);		
//									}
									break;
								case 6: // logout
//									delClient(s_num);
									break;
								case 7: // updateUserList
									List<String> temps = new ArrayList<>();
									for(int i=0;i<onUsers.size();i++) {
										temps.add(onUsers.get(i).getuName());
									}
									StringBuffer sb = new StringBuffer();
									for(String s : temps) {
										sb.append(s);
										sb.append(",");
									}
									String res = sb.toString();
//									System.out.println(res);
									send(7,-1,res);
									break;
								case 8: // updateRoomList
									if(!onRoomList.isEmpty()) {
										StringBuffer sb1 = new StringBuffer();
										for(String s: onRoomList) {
											sb1.append(s);
											sb1.append(",");
											
										}
										String res1 = sb1.toString();
										send(8,-1,res1);
									}
									break;
								case 9: // del Room
									break;
								default: // 다른 거 입력했을 때.
									break;
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
					
					
				}
			};
			serverPool.submit(runnable);
		}
		// 보내기
		public void send(int req, int opt, String data) {
			data = req+"|"+opt+"|"+data;
			// 확인용
//			System.out.println(data);
			try {
				byte[] bytes = data.getBytes("UTF-8");
				
				OutputStream os = client.getOutputStream();
				os.write(bytes);
				os.flush();
			} catch (UnsupportedEncodingException e) {
				// 통신 안될 때 메시지 정하기
				// 클라이언트에 표시
				// map에서 삭제
				// socket 닫기
				e.printStackTrace();
			} catch (IOException e) {
				// 통신 안될 때 메시지 정하기
				// 클라이언트에 표시
				// map에서 삭제
				// socket 닫기
				e.printStackTrace();
			}
				
			}
			
		}
			
			
			
			
		
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnStartStop.setOnAction(event->handleBtnStartStop(event));
	}
	
	public void handleBtnStartStop(ActionEvent e) {
		System.out.println("click");
		System.out.println(btnStartStop.getText());
		// 버튼 시작 멈춤 이름 바뀌게 및 서버 시작/ 종료 메소드 실행
		if(btnStartStop.getText().equals("Start")) {
			startServer();
		}else {
			stopServer();
		}
	}
	
	public void startServer() {
		// 풀 생성
		serverPool = Executors.newFixedThreadPool(30);
		try {
			// 서버 소켓 생성
			server = new ServerSocket(SERVER_PORT);
			Platform.runLater(()->{
				btnStartStop.setText("Stop");
				displayText("[ 서버 시작 ]");
			});
		} catch (IOException e) {
			// 문제 생길 시 종료
			stopServer();
			return;
		}
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// 여러개 클라이언트를 받기 위해 반복문이 필요 할일 한 후 다시 클라이언트 연결 될 때까지 blocked
				while (true) {
					try {
						// 연결 받을 때까지 blocked 상태가 됨
						Socket socket = server.accept();
						// 연결 완료되면 소켓 주소 보여주고
						String message = "[ 연결 수락 : ]" + socket.getRemoteSocketAddress();
						System.out.println(message);
						//txtDisplay.appendText(message+"\n");
						// 텍스트에어리어에 보이게 하는 메소드 (스레드니까 런레이터로 감싸주자)
						Platform.runLater(()->displayText(message));
						
						//PartyUser 객체 생성
						PartyUser partyUser = new PartyUser(socket);
						// 일단 연결했을 socket 넣기. 
						connSockets.add(socket);
						// socket 불러올 key값.
						int key = connSockets.indexOf(socket);
						String socketKey = key+"";
						
						partyUser.send(0,-1, socketKey);
						
						
						
						// 연결 소켓 갯수 
						displayText("[연결 Client 수: "+connSockets.size()+"]");
						
						
//						
					} catch (IOException e) {
						
						break;
					}
					
				}
				
			}
			
		};
		// 풀에다가 활동을 등록시켜놓음.
		serverPool.submit(runnable);
		
		
		
	}
	
	

	public void stopServer() {
		try {
//			// 안정적으로 인덱스 안꼬이고 처리하기 위해 이걸 사용
//			collection 다 빼야함. 
//			Iterator<PartyUser> iterator = .iterator();
//			while(iterator.hasNext()) {
//				Client client = iterator.next();
//				if(client.socket !=null&& !client.socket.isClosed()) {
//					client.socket.close();
//					iterator.remove();
//				}
//			}
			
			if(server !=null && !server.isClosed()) {
				server.close();
			}
			if(serverPool !=null && !serverPool.isShutdown()) {
				serverPool.shutdown();
			}
			// 스레드가 ui를 직접 변경하면 오류가 발생하기 때문에 runlater 써주자. 
			Platform.runLater(()->{
				btnStartStop.setText("Start");
				displayText("[ 서버 종료 ]");
			});
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	// 날라온거 textarea에 적기
	void displayText(String text) {
		txtDisplay.appendText(text+"\n");
	}
	
	
}
