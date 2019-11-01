//package yyg;
///**
// * 
// * @author yyg
// * Desc : 방을 생성하고 삭제하는 역할. 
// *
// */
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class RoomManager {
//	
//	// 방의 리스트
//	private static List roomList;
//	// synchronized 대신 이거 써볼까?
//	private static AtomicInteger atomicInteger;
//	
//	static {
//		roomList = new ArrayList();
//		atomicInteger = new AtomicInteger();
//	}
//	
//	public RoomManager() {}
//	
//	// 빈 룸 생성
//	public static PartyRoom createRoom() {
//		// roomId 생성하고 채번
//		int roomId = atomicInteger.incrementAndGet();
//		PartyRoom room = new PartyRoom(roomId);
//		// 방 만들었음.
//		roomList.add(room);
//		
//		System.out.println("빈방 만들어짐");
//		return room;
//	}
//	
//	// 유저 리스트로 방 생성
//	public static PartyRoom createRoom(List users) {
//        int roomId = atomicInteger.incrementAndGet();// room id 채번
//
//        PartyRoom room = new PartyRoom(roomId);
//        room.enterUser(users);
//
//        roomList.add(room);
//        System.out.println("Room Created!");
//        return room;
//    }
//	
//	// 방 제거
//	public static void removeRoom(PartyRoom room) {
//		room.close();
//		roomList.remove(room);
//		System.out.println("방 제거 완료");
//	}
//	
//	public static int roomCount() {
//		return roomList.size();
//	}
//	
//}
