package yyg.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AppMain extends Application {

	// 자원 설정 
	public static StackPane stackPane;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			// 공유자원으로 가져왔으니까. 
			stackPane = FXMLLoader.load(AppMain.class.getResource("login/login.fxml"));
			// 클라이언트 컨트롤러 가져오기
			
			// static이니까 
			Scene scene = new Scene(AppMain.stackPane);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}