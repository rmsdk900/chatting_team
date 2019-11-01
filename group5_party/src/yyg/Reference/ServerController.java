package yyg.Reference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
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
	ExecutorService threads;
	// 서버 소켓
	ServerSocket server;
	
	// 클라이언트들을 담아둘 벡터
	List<Client> connections = new Vector<>();
	
	// 가져올 클라이언트 
	class Client {
		
		// 연결된 클라이언트 정보를 담을 수 있는 소켓
		Socket socket;
		// 연결된 사람 닉
		String nickName;
		// 생성자 소켓 
		Client(Socket socket){
			this.socket = socket;
			receive();
		}
		
		// message 받기
		public void receive() {
			// 계속 받아야 하니까.
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							byte[] byteArr = new byte[100];
							// 소켓 인풋 스트림 받아오고
							InputStream is = socket.getInputStream();
							int readByteCount = is.read(byteArr);
							// 받아올 게 없으면 에러 발생
							if(readByteCount == -1)throw new IOException();
							String message = "[요청처리"+socket.getRemoteSocketAddress()+"]";
							Platform.runLater(()->displayText(message));
							String data = new String(byteArr,0, readByteCount, "UTF-8");
							System.out.println(data);
							
							// 구분자를 기준으로 잘라 넣는다.
							String[] strs = data.split("\\|");
							String msg ="";
							System.out.println(strs[0]);
							System.out.println(strs[1]);
							// 1이면 닉네임!
							if(strs[0].equals("1")) {
								Client.this.nickName = strs[1];
								msg = nickName+" : 님이 입장하셨습니다.";
							}else if(strs[0].equals("2")) {
								msg = nickName +" : "+strs[1];
							}
							
							Platform.runLater(()->displayText(data));
							// 모든 접속된 클라이언트들에게 던져주기 얘는 서버니까
							for (Client client : connections) {
								// 나한테는 안보내게
								if(client != Client.this) {
									client.send(msg);
								}
							}
						} catch (Exception e) {
							connections.remove(Client.this);
							String message = "[클라이언트 통신 안됨 : "+socket.getRemoteSocketAddress()+"]";
							Platform.runLater(()->displayText(message));
							try {
								socket.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							break;
						}
						
					}
					
				}
				
			};
			// runnable 풀에 등록 얘도 계속 돌아야 하니까.
			threads.submit(runnable);
			
		}
		// message 전송
		public void send(String data) {
			
			try {
				byte[] byteArr = data.getBytes("UTF-8");
				// 써야하니까.
				OutputStream os = socket.getOutputStream();
				os.write(byteArr);
				os.flush();
				
				
			} catch (Exception e) {
				String message = "[클라이언트 통신 안됨 : "+socket.getRemoteSocketAddress()+"]";
				Platform.runLater(()->displayText(message));
				connections.remove(Client.this);
				try {
					socket.close();
				} catch (IOException e1) {}
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
		threads = Executors.newFixedThreadPool(30);
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
						
						//Client 객체 생성
						Client client = new Client(socket);
						// 리스트에 추가
						connections.add(client);
						
						displayText("[연결 Client 수: " + connections.size()+"]");
					} catch (IOException e) {
						stopServer();
						break;
					}
					
				}
				
			}
			
		};
		// 풀에다가 활동을 등록시켜놓음.
		threads.submit(runnable);
		
		
		
	}
	
	public void stopServer() {
		try {
			// 안정적으로 인덱스 안꼬이고 처리하기 위해 이걸 사용
			Iterator<Client> iterator = connections.iterator();
			while(iterator.hasNext()) {
				Client client = iterator.next();
				if(client.socket !=null&& !client.socket.isClosed()) {
					client.socket.close();
					iterator.remove();
				}
			}
			
			if(server !=null && !server.isClosed()) {
				server.close();
			}
			if(threads !=null && !threads.isShutdown()) {
				threads.shutdown();
			}
			// 스레드가 ui를 직접 변경하면 오류가 발생하기 때문에 runlater 써주자. 
			Platform.runLater(()->{
				btnStartStop.setText("Start");
				displayText("[ 서버 종료 ]");
			});
		} catch (IOException e) {}
		
	}
	// 날라온거 textarea에 적기
	void displayText(String text) {
		txtDisplay.appendText(text+"\n");
	}
	
	
}
