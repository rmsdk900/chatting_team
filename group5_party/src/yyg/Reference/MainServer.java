package yyg.Reference;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainServer extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			//컨트롤러 정보를 가져올 일이 있을 거 같아 loader로 가져옴.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
			Parent root = loader.load();
			
			ServerController controller = loader.getController();
			
			Scene scene = new Scene(root);
			primaryStage.setTitle("Chat Server");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// 컨트롤러에서 종료 요청을 받으면 서버를 종료하자.
			primaryStage.setOnCloseRequest(event->controller.stopServer());
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
