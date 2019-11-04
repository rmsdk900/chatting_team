package ldk;


import java.net.Socket;


public class PartyUser {

	private int num;
	private String uId;
	private PartyRoom room;
	private String uPw;
	private String uName;
	private String nickName;
	
	Socket socket;
	
	public PartyUser() {}
	
	public PartyUser(String nickName) {
		this.nickName = nickName;
	}

	public PartyUser(int num, String uId, PartyRoom room, String uPw, String uName, String nickName, Socket socket) {
		this.num = num;
		this.uId = uId;
		this.room = room;
		this.uPw = uPw;
		this.uName = uName;
		this.nickName = nickName;
		this.socket = socket;
	}

	public void enterRoom(PartyRoom room) {
		room.enterUser(this);
		this.room = room;
	}
	
	public void exitRoom(PartyRoom room) {
		this.room = null;
	}
	

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getuPw() {
		return uPw;
	}

	public void setuPw(String uPw) {
		this.uPw = uPw;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public PartyRoom getRoom() {
		return room;
	}

	public void setRoom(PartyRoom room) {
		this.room = room;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
}
