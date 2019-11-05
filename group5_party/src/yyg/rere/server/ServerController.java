package yyg.rere.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

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
	Map<Integer, PartyUser> onUsers = new HashMap<>();
	
	// 방 리스트
	Map<Integer, String> onRoomList = new HashMap<>();
	
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
			// 유저의 비밀번호
			private String uPassword;
			// 유저의 소켓
			private Socket client;
			// 유저가 속한 방
//			private PartyRoom pRoom;
			
			
			//생성자
			// id로 유저 객체 만들기
			public PartyUser(String uId) {
				this.uId = uId;
			}
			// id와 password로 만들기
			public PartyUser(String uId, String uPassword) {
				this.uId = uId;
				this.uPassword = uPassword;
			}
			
			public PartyUser(Socket client) {
				this.client = client;
				receive();
			}
			
			public PartyUser (String uId, Socket client) {
				this.client = client;
				this.uId = uId;
			}
			
			public PartyUser(int uNumber, String uId, String uName, String uPassword) {
				this.uNumber = uNumber;
				this.uId = uId;
				this.uName = uName;
				this.uPassword = uPassword;
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
			public String getuPassword() {
				return uPassword;
			}
			public Socket getClient() {
				return client;
			}
//			public PartyRoom getpRoom() {
//				return pRoom;
//			}
			
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
			public void setuPassword(String uPassword) {
				this.uPassword = uPassword;
			}
			public void setClient(Socket client) {
				this.client = client;
			}
//			public void setpRoom(PartyRoom pRoom) {
//				this.pRoom = pRoom;
//			}
			
			
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
				return "PartyUser [uNumber=" + uNumber + ", uId=" + uId + ", uName=" + uName + ", uPassword=" + uPassword
						+ ", client=" + client + "]";
			}
			
			// 계속 정보를 받는 녀석 불러오기
			public void receive() {
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						while(true) {
							byte[] bytes = new byte[100];
//							InputStream is = client
						}
						
						
					}
				};
				serverPool.submit(runnable);
			}
			
			public void send(int req, int opt, String data) {
				data = req+"|"+opt+"|"+data;
				// 확인용
				System.out.println(data);
				try {
					byte[] bytes = data.getBytes("UTF-8");
					OutputStream os = client.getOutputStream();
					os.write(bytes);
					os.flush();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}
	
	
	
//	// 접속자들 담아둘 vector
//	List<Client> connClients = new Vector<>();
//	// 방마다 접속자 수를 담아둘 map
//	Map<String, Vector<Client>> onUserList = new HashMap<>();
//	// 방 리스트 담아둘 vector
//	List<String> roomList = new Vector<>();
//	
	
	
//	// 가져올 클라이언트 
//	class Client {
//		
//		// 연결된 클라이언트 정보를 담을 수 있는 소켓
//		Socket socket;
//		// 연결된 사람 닉
//		String uName;
//		// 생성자 소켓 
//		Client(Socket socket){
//			this.socket = socket;
//			receive();
//		}
//		
//		// message 받기
//		public void receive() {
//			// 계속 받아야 하니까.
//			Runnable runnable = new Runnable() {
//
//				@Override
//				public void run() {
//					while (true) {
//						try {
//							byte[] byteArr = new byte[100];
//							// 소켓 인풋 스트림 받아오고
//							InputStream is = socket.getInputStream();
//							int readByteCount = is.read(byteArr);
//							// 받아올 게 없으면 에러 발생
//							if(readByteCount == -1)throw new IOException();
//							String message = "[요청처리"+socket.getRemoteSocketAddress()+"]";
//							Platform.runLater(()->displayText(message));
//							String data = new String(byteArr,0, readByteCount, "UTF-8");
//							System.out.println(data);
//							
//							// 구분자를 기준으로 잘라 넣는다.
//							String[] strs = data.split("\\|");
//							String msg ="";
//							System.out.println(strs[0]);
//							System.out.println(strs[1]);
//							// 1이면 닉네임!
//							if(strs[0].equals("1")) {
//								Client.this.uName = strs[1];
//								msg = uName+" : 님이 입장하셨습니다.";
//							}else if(strs[0].equals("2")) {
//								msg = uName +" : "+strs[1];
//							}
//							
//							Platform.runLater(()->displayText(data));
//							// 모든 접속된 클라이언트들에게 던져주기 얘는 서버니까
//							for (Client client : connClients) {
//								// 나한테는 안보내게
//								if(client != Client.this) {
//									client.send(msg);
//								}
//							}
//						} catch (Exception e) {
//							connClients.remove(Client.this);
//							String message = "[클라이언트 통신 안됨 : "+socket.getRemoteSocketAddress()+"]";
//							Platform.runLater(()->displayText(message));
//							try {
//								socket.close();
//							} catch (IOException e1) {
//								e1.printStackTrace();
//							}
//							break;
//						}
//						
//					}
//					
//				}
//				
//			};
//			// runnable 풀에 등록 얘도 계속 돌아야 하니까.
//			serverPool.submit(runnable);
//			
//		}
//		// message 전송
//		public void send(String data) {
//			
//			try {
//				byte[] byteArr = data.getBytes("UTF-8");
//				// 써야하니까.
//				OutputStream os = socket.getOutputStream();
//				os.write(byteArr);
//				os.flush();
//				
//				
//			} catch (Exception e) {
//				String message = "[클라이언트 통신 안됨 : "+socket.getRemoteSocketAddress()+"]";
//				Platform.runLater(()->displayText(message));
//				connClients.remove(Client.this);
//				try {
//					socket.close();
//				} catch (IOException e1) {}
//			}
//			
//			
//		}
//	}
	
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
						
						
//						//Client 객체 생성
//						Client client = new Client(socket);
//						// 리스트에 추가
//						connClients.add(client);
//						
//						displayText("[연결 Client 수: " + connClients.size()+"]");
					} catch (IOException e) {
//						stopServer();
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
//			Iterator<Client> iterator = connClients.iterator();
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
