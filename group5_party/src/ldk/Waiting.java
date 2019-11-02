package ldk;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

	
class RoomManager{
	
	private static List<PartyRoom> roomList; // 생성된 방의 리스트
	private static AtomicInteger atomicInteger;
	
	static {
		roomList = new ArrayList<PartyRoom>();
		atomicInteger = new AtomicInteger();
	}
	
	public RoomManager(){
		
	}
	
	public static PartyRoom createRoom() {
		int roomId = atomicInteger.incrementAndGet();
		PartyRoom room = new PartyRoom(roomId);
		roomList.add(room);
		return room;
	}
	
	public static PartyRoom createRoom(PartyUser owner) {
		int roomId = atomicInteger.incrementAndGet();
		
		PartyRoom room = new PartyRoom(roomId);
		room.enterUser(owner);
		room.setOwner(owner);
		
		roomList.add(room);
		return room;
	}
	
	public static PartyRoom getRoom(PartyRoom partyroom) {
		int idx = roomList.indexOf(partyroom);
		
		if(idx>0) {
			return roomList.get(idx);
		}else {
			return null;
		}
	}
	
	public static void removeRoom(PartyRoom room) {
		room.close();
		roomList.remove(room);
	}
	
	public static int roomCount() {
		return roomList.size();
	}
}

public class Waiting implements Initializable{
	
	@FXML TableView<PartyRoom> tableView;
	@FXML ListView<PartyUser> userList;
	@FXML Button btnCreate, btnJoin, btnExit;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

}
