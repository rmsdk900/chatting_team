package yyg.instant;

/*
 * package yyg;
 * 
 * import java.net.Socket;
 * 
 *//**
	 * 
	 * @author yyg Desc : 유저들의 정보를 담고 있는 class
	 *
	 */
/*
 * 
 * 
 * 
 * 
 * public class PartyUser { // 각 유저의 auto-increment private int autoIn;
 * 
 * // 유저의 아이디 private String uId;
 * 
 * // 유저의 이름 혹은 닉네임 private String uName;
 * 
 * // 유저의 비밀번호 private String uPassword;
 * 
 * // 유저의 소켓 private Socket client;
 * 
 * // 유저가 속한 방 private PartyRoom pRoom;
 * 
 * // 생성자들 // id로 유저 만들기 public PartyUser(String uId) { this.uId = uId; }
 * 
 *//**
	 * 방에 입장하는 메소드
	 * 
	 */
/*
 * 
 * 
 * 
 * public void enterRoom(PartyRoom room) { // 유저를 룸으로 입장시키는 메소드(룸의 메소드 이용)
 * room.enterUser(this); // 유저가 속한 방을 룸으로 변경 this.pRoom = room; }
 * 
 *//**
	 * 방에서 퇴장하는 메소드
	 * 
	 *//*
		 * public void exitRoom(PartyRoom room) { this.pRoom = null; // 퇴장 처리 메시지(선택)
		 * 
		 * }
		 * 
		 * // 유저 간 비교할 수 있도록 equals 메소드 재정의
		 * 
		 * @Override public boolean equals(Object obj) { // 객체 자체가 똑같을 때 if(this==obj)
		 * return true; // 객체가 비어있거나 클래스가 갖지 않을 때 if(obj==null || getClass() !=
		 * obj.getClass()) return false;
		 * 
		 * PartyUser partyUser = (PartyUser) obj; return uId == partyUser.uId; }
		 * 
		 * // 이거 int인데 데이터 베이스에서 auto-increment도 불러와야하나?
		 * 
		 * @Override public int hashCode() {
		 * 
		 * return autoIn; }
		 * 
		 * 
		 * // Getter만 public String getuId() { return uId; }
		 * 
		 * 
		 * public String getuName() { return uName; } public String getuPassword() {
		 * return uPassword; } public Socket getClient() { return client; } public
		 * PartyRoom getpRoom() { return pRoom; }
		 * 
		 * public int getAutoIn() { return autoIn; }
		 * 
		 * 
		 * 
		 * // setter 필요 없으면 나중에 지움. public void setuId(String uId) { this.uId = uId; }
		 * public void setuName(String uName) { this.uName = uName; } public void
		 * setuPassword(String uPassword) { this.uPassword = uPassword; } public void
		 * setClient(Socket client) { this.client = client; } public void
		 * setpRoom(PartyRoom pRoom) { this.pRoom = pRoom; } public void setAutoIn(int
		 * i) { this.autoIn = i; }
		 * 
		 * 
		 * 
		 * }
		 */