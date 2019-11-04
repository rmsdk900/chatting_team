package yyg.rere.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import yyg.rere.AppMain;
import yyg.rere.waiting.WaitController;

public class LoginController implements Initializable{
	
	@FXML private TextField txtId;
	@FXML private PasswordField txtPw;
	@FXML private Button btnLogin, btnSignup, btnExit;
	
	private Stage dialog;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// 로그인 버튼 눌렀을 때
		btnLogin.setOnAction((event)->{
			System.out.println("로그인");
			// 등록할 로그인 정보 콘솔에 출력
			String loginId = txtId.getText();
			String loginPw = txtPw.getText();
			System.out.println(loginId);
			System.out.println(loginPw);
			// 대기실 창 열기
			dialog = new Stage();
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(txtId.getScene().getWindow());
			dialog.setTitle("대기실");
			
			try {
				Parent signUp = FXMLLoader.load(WaitController.class.getResource("waitingroom.fxml"));
				
				Scene scene = new Scene(signUp);
				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
//			try {
//				Parent waitingRoom = FXMLLoader.load(WaitController.class.getResource("waitingroom.fxml"));
//				AppMain.stackPane.getChildren().add(waitingRoom);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		});
		// 회원가입 버튼 눌렀을 때;
		btnSignup.setOnAction((event)->{
			System.out.println("회원가입");
			// 회원가입창 열기
			dialog = new Stage();
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(txtId.getScene().getWindow());
			dialog.setTitle("회원가입");
			
			try {
				Parent login = FXMLLoader.load(getClass().getResource("signup.fxml"));
				
				//회원가입창 변수들 다 들고오기
				TextField txtNewId = (TextField) login.lookup("#txtNewId");
				Button confirmId = (Button) login.lookup("#confirmId");
				PasswordField txtNewPw = (PasswordField) login.lookup("#txtPw");
				PasswordField txtPwChk = (PasswordField) login.lookup("#txtPwCheck");
				TextField txtName = (TextField) login.lookup("#txtName");
				TextField txtNick = (TextField) login.lookup("#txtNick");
				Button btnReg = (Button) login.lookup("#btnReg");
				Button btnCancel = (Button) login.lookup("#btnCancel");
				
				// 제일 쉬운 닫기부터
				btnCancel.setOnAction(e->{
					dialog.close();
				});
				// 중복확인은 나중에
				confirmId.setOnAction(e->{
					System.out.println("중복확인");
					
				});
				
				// 등록 버튼 눌렀을 시
				btnReg.setOnAction(e->{
					String newId = txtNewId.getText();
					String newPw = txtNewPw.getText();
					String newPwC = txtPwChk.getText();
					String newName = txtName.getText();
					String newNick = txtNick.getText();
//					System.out.println(newId);
//					System.out.println(newPw);
//					System.out.println(newName);
//					System.out.println(newNick);
					
					txtId.setText(newId);
					txtPw.setText(newPw);
					
					dialog.close();
				});
				
				
				 
				
				
				
				
				
				Scene scene = new Scene(login);
				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		});
		
		btnExit.setOnAction((event)->{
			System.out.println("종료");
			Platform.exit();
		});
	}
	
}
