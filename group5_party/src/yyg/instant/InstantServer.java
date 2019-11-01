package yyg.instant;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author yyg
 * Desc gui 입히기 전 MainServer
 *
 */

public class InstantServer {
	// 서버 IP랑 사용 포트 지정
	public static final String IP = "192.168.1.31";
	public static final int PORT = 5001;
	
	// ServerSocket, 클라이언트 Socket 지정.
	ServerSocket server;
	Socket client;
	
//	HashMap<String, ObjectOutputStream> hm;
	
	//server의 thread pool 지정
	ExecutorService serverPool;
	
	public InstantServer() {
		try {
			// thread 풀 생성
			serverPool = Executors.newFixedThreadPool(
					Runtime.getRuntime().availableProcessors()
			);
			// 서버 소켓 생성
			server = new ServerSocket();
			// 서버 바인드
			server.bind(new InetSocketAddress(IP, PORT));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("***** 채팅 서버 오픈 *****");
//		hm = new HashMap<>();
		startServer();
	}
	
	public void startServer() {
		while (true) {
			try {
				System.out.println("[ CLIENT 연결 대기중 ... ]");
				client = server.accept();
//				InstantServerThread chatThread = new InstantServerThread(client, hm);
//				serverPool.submit(chatThread);
				
			} catch (IOException e) {
				System.out.println("서버 오류");
				serverPool.shutdownNow();
				try {
					if(server != null && !server.isClosed()) {
						server.close();
					}
				} catch (IOException e1) {}
				break;
			}
		}
		System.out.println("서버 종료");
		
	}
	
	public static void main(String[] args) {
		new InstantServer();
	}
}
