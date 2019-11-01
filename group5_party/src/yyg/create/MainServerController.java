package yyg.create;

import java.io.IOException;
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

public class MainServerController implements Initializable {

	// 서버 포트 설정
	public static final int SERVER_PORT = 5001;
	
	@FXML private TextArea txtDisplay;
	@FXML private Button btnStartStop;
	
	// 스레드 풀 관련
	ExecutorService serverPool;
	// 서버 소켓
	ServerSocket server;
		
	// 클라이언트들을 담아둘 벡터
	List<ConnClient> connectedClients = new Vector<>();
	
	// 클라이언트의 소켓 저장해야지
	class ConnClient {
		
		// 연결된 클라이언트 정보 담기
		Socket socket;
		// 닉
		String nickName;
		
		// 생성자
		ConnClient(Socket socket){
			this.socket = socket;
			// 계속 정보 받기
			receiveData();
		}
		
		// 정보 받기 메소드
		public void receiveData() {
			
		}
		// 데이터 전송
		public void send(String data) {
			
		}
		
		
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnStartStop.setOnAction(event->handleBtnStartStop(event));
		
		
	}

	private void handleBtnStartStop(ActionEvent event) {
		// 무슨 상태로 돌입할지 알려주기
		System.out.println(btnStartStop.getText());
		// 서버 시작과 종료 & 버튼에 상태 표시해주기.
		if(btnStartStop.getText().equals("Start")) {
			startServer();
		}else {
			stopServer();
		}	
	}
	// 서버 시작
	public void startServer() {
		// 여러 작업들 담아줄 쓰레드 풀 생성
		serverPool = Executors.newFixedThreadPool(30);
		try {
			// 서버 소켓 생성
			server = new ServerSocket(SERVER_PORT);
			// 쓰레드가 ui를 바꾸는 작업은 runLater로 처리
			Platform.runLater(()->{
				btnStartStop.setText("Stop");
				displayText("[ 서버 시작 ]");
			});
			// 콘솔에 서버 시작 표시
			System.out.println("[ Start Server ]");
		} catch (IOException e) {
			// 문제 생기면 서버 종료하고 이 메소드에서 나감
			stopServer();
			return;
		}
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				//클라이언트가 날라올 때마다 해야하기 때문에 반복문 사용
				while (true) {
					try {
						// 연결 받기 상태
						Socket socket = server.accept();
						// 연결되면 소켓 주소 보여주자.
						String connSuccess = "[ 연결 수락 : ]"+socket.getRemoteSocketAddress();
						System.out.println(connSuccess);
						
						// gui 에서 이거 보이게 하자
						Platform.runLater(()->displayText(connSuccess));
						// 받은 socket 을 바탕으로 Client 생성
						ConnClient client = new ConnClient(socket);
						// 연결된 소켓 vector 에 추가
						connectedClients.add(client);
						
						// 연결된 소켓 수 표시
						displayText("[ 연결 Client 수: "+ connectedClients.size()+" ]");
					} catch (IOException e) {
//						stopServer();
						break;
					}
				}
			}
		};
		// 풀에다가 넣자
		serverPool.submit(runnable);
	}
	// 서버 종료
	public void stopServer() {
		try {
			// 인덱스 안꼬이고 처리하기 위해 Iterator 사용
			Iterator<ConnClient> itr = connectedClients.iterator();
			while(itr.hasNext()) {
				// 하나씩 불러와
				ConnClient client = itr.next();
				// 소켓 닫고 vector에서 삭제
				if(client.socket != null && !client.socket.isClosed()) {
					client.socket.close();
					itr.remove();
				}
			}
			// 서버 닫자
			if (server != null && !server.isClosed()) {
				server.close();
			}
			// server thread pool도 닫자
			if (serverPool != null && !serverPool.isShutdown()) {
				serverPool.shutdown();
			}
			// gui에도 표시
			Platform.runLater(()->{
				btnStartStop.setText("Start");
				displayText("[ 서버 종료 ]");
			});
			System.out.println("MainServer closed!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	// 날라온거 textarea에 적기
	void displayText(String text) {
		txtDisplay.appendText(text+"\n");
	}
	
}
