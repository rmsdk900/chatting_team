package yyg.Reference.external;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//class RoomManager{
//    private static List roomList; // 방의 리스트
//    private static AtomicInteger atomicInteger;
//    static {
//        roomList = new ArrayList();
//        atomicInteger = new AtomicInteger();
//    }
//    public static int createRoom() { // 룸을 새로 생성(빈 방)
//        int roomId = atomicInteger.incrementAndGet();// room id 채번
//        System.out.println("Room Created! : " + roomId);
//        return roomId;
//    }
//}

public class SomeTest {
    public static void main(String[] args) {
        System.out.println(RoomManager.createRoom());
        System.out.println(RoomManager.createRoom());

        Thread thread = new Thread(() -> System.out.println(RoomManager.createRoom()));
        Thread thread2 = new Thread(() -> System.out.println(RoomManager.createRoom()));
        Thread thread3 = new Thread(() -> System.out.println(RoomManager.createRoom()));
        thread.start();
        thread2.start();
        thread3.start();
    }
}
