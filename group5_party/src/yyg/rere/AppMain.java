package yyg.rere;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import yyg.rere.login.LoginController;


public class AppMain extends Application {
	
	
	@Override
	public void start(Stage primaryStage) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("login/login.fxml"));
//			//fxml파일에 대한 정보를 가져옴
			Parent root = loader.load();
			LoginController controller = loader.getController();
			controller.setPrimaryStage(primaryStage);

			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("로그인");
			primaryStage.setResizable(false);
			primaryStage.setOnCloseRequest(e->controller.stopClient());
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
