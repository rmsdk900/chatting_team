package ldk;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController implements Initializable{

	@FXML Label lblId, lblPw;
	@FXML Button btnLogin, btnSignup, btnCancel;
	
	//private ObservableList<PartyUser> list;
	private Stage dialog;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnLogin.setOnAction(event->handleBtnLogin(event));
		btnSignup.setOnAction(event->handleBtnSignup(event));
		btnCancel.setOnAction(event->handleBtnCancel(event));
	}

	public void handleBtnLogin(ActionEvent event) {
		dialog = new Stage(StageStyle.DECORATED);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(btnLogin.getScene().getWindow());
		dialog.setTitle("대기실");
		Parent parent = null;
		
		try {
			parent = FXMLLoader.load(getClass().getResource("waiting.fxml"));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			stop();
		}
		
		Scene scene = new Scene(parent);
		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.show();
	}
	
	public void handleBtnSignup(ActionEvent event) {
		dialog = new Stage(StageStyle.DECORATED);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(btnSignup.getScene().getWindow());
		dialog.setTitle("회원가입");
		Parent parent = null;
		
		try {
			parent =  FXMLLoader.load(getClass().getResource("signup.fxml"));
		} catch (IOException event1) {
			System.out.println(event1.getMessage());
			stop();
		}
		
		Scene scene = new Scene(parent);
		dialog.setScene(scene);
		dialog.setResizable(false);
		dialog.show();
		
//		TextField txtId = (TextField)parent.lookup("txtId");
//		TextField txtName = (TextField)parent.lookup("txtName");
//		TextField txtNick = (TextField)parent.lookup("txtNick");
//		PasswordField pfPw = (PasswordField)parent.lookup("pfPw");
//		PasswordField pfPCheck = (PasswordField)parent.lookup("pfPCheck");
//		
//		((Button)parent.lookup("btnSign")).setOnAction(e->{
//			PartyUser pUser = new PartyUser();
//			
//			String uId = txtId.getText();
//			String uPw = pfPw.getText();
//			String uName = txtName.getText();
//			String uNick = txtNick.getText();
//			
//			if(uPw != pfPCheck.getText()) {
//				System.out.println("비밀번호가 일치하지 않습니다.");
//			}
//			
//			pUser.setuId(uId);
//			pUser.setuPw(uPw);
//			pUser.setuName(uName);
//			pUser.setNickName(uNick);
//			
//			list.add(pUser);
//		});
//		
//		((Button)parent.lookup("btnCancel")).setOnAction(e->dialog.close());
	}
	
	public void handleBtnCancel(ActionEvent event) {
		Platform.exit();
	}
	
	public void stop() {
		System.out.println("연결 종료");
		
	}

}
