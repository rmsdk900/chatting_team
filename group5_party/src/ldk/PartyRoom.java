package ldk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PartyRoom implements Initializable{

	@FXML TextArea txtDisplay;
	@FXML TextField txtInput;
	@FXML ListView<PartyUser> uList;
	@FXML Button btnSend;
	
	private int num; // 방 번호
	private List<PartyUser> userList;
	private PartyUser roomOwner;
	private String roomName;
	
	ExecutorService threads;
	ServerSocket server;
	
	Socket socket;
	
	public PartyRoom(int roomNum) {
		this.num = roomNum;
		userList = new ArrayList<PartyUser>();
	}
	
	public PartyRoom(PartyUser user) {
		userList = new ArrayList<PartyUser>();
		user.enterRoom(this);
		userList.add(user);
		this.roomOwner = user;
	}
	
	public void enterUser(PartyUser user) {
		user.enterRoom(this);
		userList.add(user);
		receive();
	}
	
	public void exitUser(PartyUser user) {
		user.exitRoom(this);
		userList.remove(user);
		
		if(userList.size()<1) {
			RoomManager.removeRoom(this);
			return;
		}
		
		if(userList.size()<2) {
			this.roomOwner = userList.get(0);
			return;
		}
	}
	
	public void close() {
		for(PartyUser user : userList) {
			user.exitRoom(this);
		}
		this.userList.clear();
		this.userList = null;
	}
	
	
	public void setOwner(PartyUser partyUser) {
		this.roomOwner = partyUser;
	}
	
	public void setRoomName(String name) {
		this.roomName = name;
	}
	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public List<PartyUser> getUserList() {
		return userList;
	}

	public void setUserList(List<PartyUser> userList) {
		this.userList = userList;
	}

	public PartyUser getRoomOwner() {
		return roomOwner;
	}

	public void setRoomOwner(PartyUser roomOwner) {
		this.roomOwner = roomOwner;
	}

	public String getRoomName() {
		return roomName;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	
	public void receive() {
		new Thread(()->{
			while(true) {
				try {
					byte[] byteArr = new byte[512];
					InputStream is = socket.getInputStream();
					int readByte = is.read(byteArr);
					
					if(readByte == -1) throw new IOException();
					
					String data = new String(byteArr,0,readByte,"UTF-8");
					Platform.runLater(()->displayText(data));
				} catch (IOException e) {
					Platform.runLater(()->displayText("서버 통신 안됨"));
					close();
					break;
				}
			}
		}).start();
	}
	
	public void send(int code, String data) {
		try {
			data = code+"|"+data;
			byte[] byteArr = data.getBytes("UTF-8");
			OutputStream os = socket.getOutputStream();
			os.write(byteArr);
			os.flush();
			displayText(data);
			txtInput.clear();
		} catch (Exception e) {
			displayText("서버 통신 안됨");
			close();
		}
	}

	public void displayText(String data) {
		txtDisplay.appendText(data+"\n");
	}

}
