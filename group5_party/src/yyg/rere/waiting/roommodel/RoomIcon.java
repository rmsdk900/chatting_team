package yyg.rere.waiting.roommodel;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import yyg.rere.waiting.WaitController;

public class RoomIcon {
	WaitController waitController;
	
	Label lblRoomName;
	

	public RoomIcon(WaitController waitController) {
			this.waitController = waitController;
	}
	
	
}
