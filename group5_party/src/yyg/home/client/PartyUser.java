package yyg.home.client;

import java.net.Socket;

public class PartyUser {
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
//	private PartyRoom pRoom;
	
	
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
	
	// 방 입장 메소드
//	public void enterRoom(PartyRoom room) {
//		// 유저를 룸에 입장시키는 메소드(룸의 메소드)
//		room.enterUser(this);
//		// 유저가 속한 방을 자기 방으로 설정 
//		this.pRoom = room;
//	}
	
	// 방에서 퇴장 메소드
//	public void exitRoom(PartyRoom room) {
//		this.pRoom = null;
//		// 퇴장 메시지나 처리 항목 더 추가
//	}
	
	// 유저 간 비교를 위한 기존 메소드들 재정의 
	
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
//	public PartyRoom getpRoom() {
//		return pRoom;
//	}
	
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
//	public void setpRoom(PartyRoom pRoom) {
//		this.pRoom = pRoom;
//	}
	
	
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
	
	
	
	
}
