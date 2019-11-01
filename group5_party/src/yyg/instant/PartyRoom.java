package yyg.instant;
/*
 * package yyg;
 * 
 * import java.util.ArrayList; import java.util.List;
 * 
 * public class PartyRoom { // partyRoom의 id private int id; // 방에 들어가있는 유저 리스트
 * private List userList; // 방 만든 방장 private PartyUser roomOwner; // 방 이름
 * private String roomName;
 * 
 * // 빈 방? public PartyRoom(int roomId) { this.id = roomId; userList = new
 * ArrayList(); }
 * 
 * // 유저가 만든 방 public PartyRoom(PartyUser user) { userList = new ArrayList();
 * user.enterRoom(this); userList.add(user); this.roomOwner = user; }
 * 
 * // 리스트로 방 생성 - 이거 없어도 될 것 같긴 함 public PartyRoom(List users) { // 리스트 복사
 * this.userList = users;
 * 
 * // 룸 입장 for(PartyUser user: users) { user.enterRoom(this); }
 * 
 * //첫번째 유저를 방장으로 설정 this.roomOwner = (PartyUser) userList.get(0); }
 * 
 * // 유저 입장시키기 public void enterUser(PartyUser user) { user.enterRoom(this);
 * userList.add(user); }
 * 
 * public void enterUser(List users) { for(PartyUser partyUser: users) {
 * partyUser.enterRoom(this); }
 * 
 * userList.addAll(users); }
 * 
 * // 유저 퇴장시키기 public void exitUser(PartyUser user) { user.exitRoom(this);
 * userList.remove(user);
 * 
 * // 아무도 없으면 if(userList.size()<1) { // 이 방 제거 RoomManager.removeRoom(this);
 * return; } // 방에 남은 인원이 1명 이하 if(userList.size() < 2) { this.roomOwner =
 * (PartyUser) userList.get(0); return; } }
 * 
 * // 방 닫기 ( 모든 유저 내보내고 리스트에서 삭제) public void close() { for(PartyUser user :
 * userList) { user.exitRoom(this); } this.userList.clear(); this.userList =
 * null; }
 * 
 * // 채팅 방 로직 구성
 * 
 * 
 * public void broadcast() {
 * 
 * } // 방장 바꾸기 public void setOwner(PartyUser partyUser) { this.roomOwner =
 * partyUser; } // 방 이름 바꾸기 public void setRoomName(String name) { this.roomName
 * = name; }
 * 
 * // 닉네임으로 사람 가져오기 public PartyUser getUserByuName(String uName) {
 * for(PartyUser user : userList) { //찾는 유저가 있다면
 * if(user.getuName().equals(uName)) { return user; } } // 찾는 유저가 없을 경우 return
 * null; }
 * 
 * // 유저 객체 가져오기 public PartyUser getUser(PartyUser partyUser) { int idx =
 * userList.indexOf(partyUser);
 * 
 * // 유저가 있으면? if(idx > 0) { return (PartyUser) userList.get(idx); // 유저가 없으면?
 * 
 * }else { return null; } } // 방 이름을 가져옴 public String getRoomName() { return
 * roomName; } // 유저의 수를 리턴 public int getUserSize() { return userList.size(); }
 * // 방장 리턴 public PartyUser getOwner() { return roomOwner; } // 방 아이디 얻어오기
 * public int getId() { return id; }
 * 
 * public void setId(int id) { this.id = id; }
 * 
 * public List getUserList() { return userList; }
 * 
 * public void setUserList(List userList) { this.userList = userList; }
 * 
 * public PartyUser getRoomOwner() { return roomOwner; }
 * 
 * public void setRoomOwner(PartyUser roomOwner) { this.roomOwner = roomOwner; }
 * 
 * @Override public boolean equals(Object obj) { if(this == obj) return true;
 * if(obj == null || getClass() != obj.getClass()) return false;
 * 
 * PartyRoom partyRoom = (PartyRoom) obj;
 * 
 * return id == partyRoom.id; }
 * 
 * @Override public int hashCode() { return id; }
 * 
 * 
 * 
 * 
 * 
 * 
 * }
 */