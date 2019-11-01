package yyg.instant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class InstantServerThread implements Runnable {
	Socket socket;
	HashMap<String, ObjectOutputStream> hm;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	String userid;

	public InstantServerThread(Socket client, HashMap<String, ObjectOutputStream> hm) {
		this.socket=client;
		this.hm = hm;
		
		try {
			System.out.println(socket.getInetAddress()+"로부터 연결 요청 받음");
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			ois = new ObjectInputStream(this.socket.getInputStream());
			
			userid = (String)ois.readObject();
			
			if(hm.containsKey(userid)) {
				oos.writeObject("사용할 수 없는 아이디입니다. 다시 요청해주세요.");
				oos.flush();
				throw new NullPointerException("사용할 수 없는 아이디로 요청");
			}
			broadcast(userid+"님이 입장하셨습니다.");
			
			synchronized (hm) {
				hm.put(userid, oos);
			}
			
			System.out.println("접속한 클라이언트의 아이디는: "+userid+"입니다.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void run() {
		// Client 에서 전달된 메시지
		// /quit =>> 연결 종료
		// /to id message =>> 귓속말
		// 나머지는 전체 채팅
		
		while(true) {
			try {
				String receiveData = (String)ois.readObject();
				if(receiveData.trim().equals("/quit")) {
					break;
				}else if(receiveData.indexOf("/to") > -1) {
					// 귓속말
					sendMsg(receiveData);
				}else {
					// 전쳇말
					broadcast(userid+" : "+receiveData);
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			
		}
		synchronized (hm) {
			hm.remove(userid);
		}
		System.out.println(userid+"님이 나가셨습니다.");
	}
	//전체 메시지 전달
	public void broadcast(String message) {
		try {
			for(ObjectOutputStream oos : hm.values()) {
				oos.writeObject(message);
				oos.flush();
			}
		} catch (IOException e) {}
	}
	// /to id 메시지 => 귓속말
	public void sendMsg(String message) {
		// /to 최기근 바보야!
		int begin = message.indexOf(" ")+1;
		
		int end = message.indexOf(" ", begin);
		
		if(end != 1) {
			String id = message.substring(begin,end);
			String msg = message.substring(end+1);
			
			ObjectOutputStream oos = hm.get(id);
			
			try {
				if(oos != null) {
					oos.writeObject(userid+"님이 귓속말을 보냈어요 : "+msg);
				}else {
					oos = hm.get(userid);
					oos.writeObject(id+"님이 존재하지 않습니다.");
				}
				oos.flush();
			} catch (IOException e) {}
		}
		
		
	}
}
