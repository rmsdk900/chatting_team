package yyg.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;



import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ClientController implements Initializable{

	@FXML private TextArea txtDisplay;
	@FXML private TextField txtInput, txtIP, txtNick;
	@FXML private Button btnConn, btnSend;
	
	// server 정보
	Socket socket;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 처음에 바로 칠 수 있게 ip에 포커스를 줘야징
		txtIP.requestFocus();
		
		// 처음 연결 안되었을 때 작성할 수 없게
		txtInput.setEditable(false);
		btnSend.setDisable(true);
		btnConn.setOnAction(event->handleBtnConn(event));
		btnSend.setOnAction((event)->{
			// 메시지를 전달할 때는 code 2
			send(2,txtInput.getText());
		});
		
	
		// 밑의 거 람다식 말고 정식으로 칠 때.
//		txtIP.setOnKeyPressed(new EventHandler<KeyEvent>() {
//			@Override
//			public void handle(KeyEvent event) {
//				
//			}
//			
//		});
		// 키 눌렀을 때 이벤트 핸들러 작동
		txtIP.setOnKeyPressed(key -> {
			if(key.getCode().equals(KeyCode.ENTER)) {
				txtNick.requestFocus();
			}
		});
		
		txtNick.setOnKeyPressed(key -> {
			if(key.getCode().equals(KeyCode.ENTER)) {
				// 버튼 실행하게 하는 메소드
				btnConn.fire();
				txtInput.requestFocus();
				System.out.println("btnConn 실행");
			}
		});
		
		// send도 엔터로 실행되게 하는 것.
		txtInput.setOnKeyPressed(key->{
			if(key.getCode().equals(KeyCode.ENTER)) {
				btnSend.fire();
			}
		});
	}
	
	public void handleBtnConn(ActionEvent event) {
		// 버튼 텍스트 바꾸면서 서버랑 연결 시작/끊기 메소드 활성화
		if(btnConn.getText().equals("Start")) {
			startClient();
		}else {
			stopClient();
		}
	}
	
	public void startClient() {
		try {
			System.out.println("서버 연결");
			String txt = txtIP.getText().trim();
			System.out.println(txt);
			// ip 주소를 가지고 ip 정보를 가져옴
			InetAddress ip = InetAddress.getByName(txt);
			System.out.println(ip);
			// 서버에 연결 요청보냄. 
			socket = new Socket(ip, 5001);
			System.out.println("[연결완료 : "+socket.getRemoteSocketAddress()+"]");
			// 완료되고 나서 버튼과 텍스트들 기능 활성화/비활성화
			btnConn.setText("Stop");
			txtIP.setEditable(false);
			txtNick.setEditable(false);
			txtInput.setEditable(true);
			btnSend.setDisable(false);
			
			// 처음 연결되었을 때니까. 닉네임은 code 1과 함께 보냈다.
			String message = txtNick.getText().trim();
			send(1,message);
			
			// 서버 값 전달 받을 준비하기
			receive();
		} catch (Exception e) {
			Platform.runLater(()->displayText("[서버 통신 안됨]"));
			stopClient();
			return;
		}
	}
	
	public void stopClient() {
		try {
			System.out.println("연결 종료");
			// 이친구도 스레드 내부에서 호출될 수 있으니까.
			Platform.runLater(()->{
				displayText("[서버와 연결 끊음]");
				// 되어야 하는 기능 살리고 안되야 하는 기능 죽이고.
				btnConn.setDisable(false);
				btnConn.setText("Start");
				txtIP.setEditable(true);
				txtNick.setEditable(true);
				txtInput.setEditable(false);
				btnSend.setDisable(true);
				
			});
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 메시지 받고
	public void receive() {
		// 생성과 동시에 시작
		new Thread(()->{
			// 이 쓰레드에서 할 내용 정의
			while (true) {
				try {
					byte[] byteArr = new byte[100];
					InputStream is = socket.getInputStream();
					int readByte = is.read(byteArr);
					// 안넘어오면 오류 발생
					if(readByte == -1) throw new IOException();
					
					String data = new String(byteArr, 0, readByte, "UTF-8");
					Platform.runLater(()->displayText(data));
					
					
				} catch (IOException e) {
					Platform.runLater(()->displayText("[서버 통신 안됨]"));
					stopClient();
					break;
				}
				
				
			}
		}).start();
	}
	// 메시지 주고
	public void send(int code, String data) {
		try {
			// code랑 구분자로 분리해서 표시하기.
			data = code+"|"+data;
			
			byte[] byteArr = data.getBytes("UTF-8");
			OutputStream os = socket.getOutputStream();
			os.write(byteArr);
			os.flush();
			displayText(data);
			// 보냈으니 다음 거 쓰게 지워주기
			txtInput.clear();
		} catch (Exception e) {
			displayText("[서버 통신 안됨]");
			stopClient();
		}
	}
	
	public void displayText(String data) {
		txtDisplay.appendText(data+"\n");
	}
	
	
	
}
