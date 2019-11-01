package yyg.rere.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import yyg.rere.AppMain;
import yyg.rere.waiting.WaitController;

public class LoginController implements Initializable{
	
	@FXML private TextField txtId;
	@FXML private PasswordField txtPw;
	@FXML private Button btnLogin, btnSignup, btnExit;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		btnLogin.setOnAction((event)->{
			System.out.println("로그인");
			try {
				Parent waitingRoom = FXMLLoader.load(WaitController.class.getResource("waitingroom.fxml"));
				AppMain.stackPane.getChildren().add(waitingRoom);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		btnSignup.setOnAction((event)->{
			System.out.println("회원가입");
		});
		
		btnExit.setOnAction((event)->{
			System.out.println("종료");
			Platform.exit();
		});
	}
	
}
