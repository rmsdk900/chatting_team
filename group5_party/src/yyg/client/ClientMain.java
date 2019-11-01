package yyg.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {

	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
			Parent root = loader.load();
			//클라이언트 컨트롤러 가져오기.
			ClientController client = loader.getController();
			
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			// 닫기 버튼 눌러도 정상적으로 종료되게 하기
			stage.setOnCloseRequest(event->{
				client.stopClient();
			});
			stage.setTitle("CHAT CLIENT");
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
