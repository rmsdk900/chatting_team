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
import javafx.scene.input.KeyCode;
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
	Map<Integer, String> onRoomList = new Hashtable<>();
	
	// 그 방에 들어간 접속자 들 <방 번호 , <구성인원들>>
	Map<Integer, Vector<String>> onRoomMembers = new Hashtable<>();
	
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
							Platform.runLater(()->displayText(data)); 
							// tokenize data
							StringTokenizer st = new StringTokenizer(data, "|");
							int request = Integer.parseInt(st.nextToken());
							int option = Integer.parseInt(st.nextToken());
							
							String message = "";
							message = st.nextToken();
							// request에 따라 처리 다르게
							switch(request) {	// request|option|message
								case 0: // join
									
									if(option==-1) {
										String[] infos = message.split(",");
										for(String str : infos) {
											System.out.println(str);
											
										}
										DB.join(infos[0], infos[1], infos[2]);
									}else if(option==1) {
										boolean possible=DB.existInTable(message, "member_list");
										send(0,1,possible+"");
									}
									
									
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
										onUsers.get(connUserCount).setuId(login[0]);
										onUsers.get(connUserCount).setuName(DB.getterNn(login[0]));
										// onUsers에 접속할 수 있는 key값 보내자. 
										send(1, -1, "1,"+DB.getterNn(login[0]));
										connUserCount++;
									// 로그인 실패할 경우
									}else {
										send(1, -1, "0");
									}
									break;
								case 2: // make a room
//									// 중복확인
//									if(option==1) {
//										System.out.println("방제 중복 확인");
//										// 가져온 방제랑 비교.
//										if(!onRoomList.isEmpty()) {
//											for(Map.Entry<Integer, String> entry : onRoomList.entrySet()) {
//												System.out.println("야 도냐?"+entry.getValue());
//												// 같은게 있을 경우!
//												if(entry.getValue().equals(message)){
//													send(2,1,"false");
//													break;
//												}
//												
//											}
//										}
//										// 중복되지 않았을 경우
//										send(2,1,"true");
										
									// 방만들어질 때	
//									}
//									if(option==-1) {
										System.out.println("방 만들기");
										room_count++;
										// message = title
										System.out.println(message);
										// 방 리스트에 넣기
										onRoomList.put(room_count, message);
										// 방별 멤버 리스트에도 형성해놓아야 함.
										onRoomMembers.put(room_count, new Vector<String>());
//									}
									
									break;
								case 3: // enter the room			[option]
									// 메시지 2차 파싱
									String[] ss = message.split(",");
									// ss[0] = title, ss[1] = nickName
									// 방 이름으로 onRoomList 의 해당 방의 번호을 찾자.
									Integer inRNum = (Integer) getKey(onRoomList, ss[0]);
									
									// onRoomMember의 형성해놓은 vector에 닉네임 추가하자
									onRoomMembers.get(inRNum).add(ss[1]);
									// 자기가 들어와있는 방 번호는 옵션으로
									send(3,inRNum,ss[0]);
									break;
								case 4: // exit room
									// 방번호로 나갈 방 찾아서 이름 빼기
									onRoomMembers.get(option).remove(message);
									
									if(!onRoomMembers.get(option).isEmpty()) {
										// 나머지 사람들을 가져와서
										Vector<String> users = onRoomMembers.get(option);
										// 위의 유저들 소켓에만 신호를 보내자.
										for(int i=0;i<users.size();i++) {
											for(Map.Entry<Integer, PartyUser> entry : onUsers.entrySet()) {
												if(entry.getValue().getuName().equals(users.get(i))) {
													entry.getValue().send(5,option, message+" 님이 나가셨습니다.");
												}
											}
										}
									// 방에 남은 인원이 없다 = 폭파
									}else {
										onRoomList.remove(option);
									}
									send(4,option,"0");
									
									break;
								case 5: // message to the room		[option]
									// 방번호로 거기 있는 사람들 찾기
									Vector<String> group=onRoomMembers.get(option);
									for(int i=0;i<group.size();i++) {
										for(Map.Entry<Integer, PartyUser> entry : onUsers.entrySet()) {
											if(entry.getValue().getuName().equals(group.get(i))) {
												entry.getValue().send(5,option, message);
											}
										}
									}
									break;
								case 6: // logout
									for(Map.Entry<Integer, PartyUser> entry : onUsers.entrySet()) {
										if(entry.getValue().getuName().equals(message)) {
											onUsers.remove(entry.getKey());
											break;
										}
									}
									
									
									// @TODO CHECK
									System.out.println("CASE 6 : "+onUsers.size());
									
									// 서버의 로그아웃 했다는 신호
									send(6,-1,"1");
									// 다른 사람한테도 이 사람이 나갔다는 것을 알려주자.
									break;
								case 7: // updateUserList
									System.out.println("7번 받았니? : "+data);
									StringBuffer sb = new StringBuffer();
									for(Map.Entry<Integer, PartyUser> entry : onUsers.entrySet()) {
										String s = entry.getValue().getuName();
										sb.append(s);
										sb.append(",");
									}
//									
									String res = sb.toString();
									System.out.println();
									// 이미 들어왔던 사람들에게 뿌리기
									System.out.println("onUsers 사이즈 : "+onUsers.size());
									for(Map.Entry<Integer, PartyUser> entry : onUsers.entrySet()) {
										entry.getValue().send(7,-1,res);
									}
									break;
								case 8: // updateRoomList
									// @TODO 
									if(!onRoomList.isEmpty()) {
										StringBuffer sb1 = new StringBuffer();
										// onRoomList의 value값들 구하기
										for(Map.Entry<Integer, String> entry : onRoomList.entrySet()) {
											String s = entry.getValue();
											sb1.append(s);
											sb1.append(",");
										}
										
										String res1 = sb1.toString();
										// 이미 들어왔던 사람들에게 뿌리기
										for(Map.Entry<Integer, PartyUser> entry : onUsers.entrySet()) {
											entry.getValue().send(8,-1,res1);
										}
//										
									}else {
										// 방 아이콘들을 클리어하라는 명령을 보내자.
										for(Map.Entry<Integer, PartyUser> entry : onUsers.entrySet()) {
											entry.getValue().send(8,-1,"clear");
										}
									}
									break;
								case 9: // 방제 중복 확인
									
								default: // 다른 거 입력했을 때. 유효하지 않은 메시지 종류
									
									break;
							}
						} catch (Exception e) {
							System.out.println("[클라이언트 통신 오류 : "+client.getRemoteSocketAddress()+"]");
							try {
								client.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}							
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
			System.out.println("서버가 보냄"+data);
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
		txtDisplay.setEditable(false);
		txtDisplay.setOnKeyPressed(key->{
			if(key.getCode().equals(KeyCode.ENTER)) {
				btnStartStop.fire();
			}
		});
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
			// 저장되어있는 정보 다 날리기
			connSockets.clear();
			onUsers.clear();
			onRoomList.clear();
			onRoomMembers.clear();
			
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
	
	// Map에서 value 값으로 key값 찾기
	public Object getKey(Map<Integer, String> m, Object value) {
		for(Object o: m.keySet()) {
			if(m.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	}
	
}
