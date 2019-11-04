package yyg.home.server.divide;

/*콘솔 멀티채팅 클라이언트 프로그램*/
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;
 

public class MultiClient {
	
	
	static boolean chatmode = false;
	static int chatState = 0; 
	//0 : 로그인 x, 1 : 로그인된 상태, 2: 방 입장 완료 (대화 가능),
	//3 : 상대방이 1:1대화 요청한 상태 , 
	//5 : req_fileSend (상대방이 현재 사용자에게 파일 전송을 요청수락한 상태)
	//메인 메소드
    public static void main(String[] args) throws UnknownHostException, IOException {
       
       
        try{
            String ServerIP = "localhost";
            //소켓 객체 생성
            Socket socket = new Socket(ServerIP, 5001); 
            System.out.println("[##] 서버와 연결되었습니다......");
            //사용자로부터 얻은 문자열을 서버로 전송해주는 역할을 하는 메소드
            
            
            /////////////////////////////////////////////////////////////
            
            Thread sender = new Sender(socket);  
            Thread receiver = new Receiver(socket);        
            //쓰레드들 시작
            sender.start(); 
            receiver.start(); 
           
        }catch(Exception e){
            System.out.println("예외[MultiClient class]:"+e);
        }
        
    }//main()-------
}//End class MultiClient





/////////////////////////////////////////////////////////////////////
 
//서버로부터 메시지를 읽는 클래스
class Receiver extends Thread{
   
    Socket socket;
    DataInputStream in;
   
    //Socket을 매개변수로 받는 생성자
    public Receiver(Socket socket){
        this.socket = socket;
        
        try{
            in = new DataInputStream(this.socket.getInputStream());
        }catch(Exception e){
            System.out.println("����:"+e);
        }
    }//생성자 --------------------
   
    
    /**메시지 파서*/     
   public String[] getMsgParse(String msg){
    	//System.out.println("msgParse()=>msg?"+ msg);
    	
    	String[] tmpArr = msg.split("[|]");
    	
    	return tmpArr;
    }
    
    
   	//run()메소드 재정의
    @Override
    public void run(){ 
    	//입력스트림이 null이 아니면 반복
        while(in!=null){ 
            try{     	
            
            	//입력스트림을 통해 읽어온 문자열을 msg에 할당
            	String msg = in.readUTF(); 
            	
                String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
                
                //메시지 처리 ----------------------------------------------
                // 로그온 시도 (대화명)
                if(msg.startsWith("logon#yes")){ 
                	// 채팅 상태를 로그온된 상태로 변경
                	MultiClient.chatState = 1; 
                	//logon#yes|그룹리스트         
                	System.out.println(msgArr[0]);
                	System.out.println("▶지역을 입력해주세요:");
                // 로그온 실패(대화명)
                }else  if(msg.startsWith("logon#no")){ 
                	
                	MultiClient.chatState = 0;                	
                	System.out.println("[##] 중복된 이름이 존재합니다\n▶이름을 다시 입력해주세요:");
                	//1. 이름이 중복될 경우(서버전체 or 그룹) logon#no 패킷이 서버로부터 전달됨
                	//2. 이름이 중복될 경우 서버에서 자체적으로 name(1), name(2) 이런 식으로 중뵉되지 않게 변경하는 방법.
                // 그룹 입장
                }else if(msg.startsWith("enterRoom#yes")){ 
                	
                	//enterRoom#yes|지역
                    System.out.println("[##] 채팅방 ("+msgArr[0]+") 에 입장하였습니다.");
                    MultiClient.chatState = 2; //채팅 상태 변경( 채팅방 입장 완료 대화가능상태)
                	 
                }else if(msg.startsWith("enterRoom#no")){
                	//enterRoom#no|지역
                	 System.out.println("[##] 입력하신 ["+msgArr[0]+ "]는 존재하지 않는 지역입니다.");
                	 System.out.println("▶지역을 다시 입력해주세요:");
                //서버에서 전달하고자 하는 메시지
                }else if(msg.startsWith("show")){ 
                
                	//show|메시지 내용
                	System.out.println(msgArr[0]);
                //대화내용	
                }else if(msg.startsWith("say")){ 
                	//say|아이디|대화내용         		
            		System.out.println("["+msgArr[0]+"] "+msgArr[1] );	
            	//귓속말	
                }else if(msg.startsWith("whisper")){ 
                	//whisper|아이디|대화내용
                	System.out.println("[귓]["+msgArr[0]+"] "+msgArr[1] );	
            	//해당 사용자에게 1:1대화 요청	
                }else if(msg.startsWith("req_PvPchat")){ 
                	//req_PvPchat|출력내용
                	//채팅 상태 변경 (상대방이 1:1대화 신청을 했을 경우)
                	MultiClient.chatState = 3; 
                	//MultiClient.chatmode=true; //Sender에게 1:1대화 요청이 들어왔다는 것을 알려주기 위함
        			System.out.println(msgArr[0]); //메시지만 추출
            		System.out.print("▶선택:");   
            	// 상대방이 현재 사용자에게 파일전송 수락 요청	
                }else if(msg.startsWith("req_fileSend")){ 
                	//req_fileSend|출력내용
                	//req_fileSend|[##] name 님께서 파일 전송을 시도합니다. 수락하시겠습니까?(Y/N)
                	// 상태변경(상대방이 현재 사용자에게 파일전송을 수락요청한 상태)
                	MultiClient.chatState = 5; 
                	// 메시지만 추출
                	System.out.println(msgArr[0]); 
                	System.out.print("▶선택:");   
                	sleep(100);
                // 파일을 보내기 위해 파일 서버 준비
                }else if(msg.startsWith("fileSender")){ 
                	               	
                    //fileSender|filepath;                	
                	System.out.println("fileSender:"+InetAddress.getLocalHost().getHostAddress());
                	System.out.println("fileSender:"+msgArr[0]);
            		//String ip=InetAddress.getLocalHost().getHostAddress();
            		
                	try {
                		// thread 실행
                		new FileSender(msgArr[0]).start(); 
					} catch (Exception e) {
						System.out.println("FileSender 쓰레드 오류:");
						e.printStackTrace();
					}
                // 파일 받기	
                }else if(msg.startsWith("fileReceiver")){ 
                	//fileReceiver|ip|fileName; 
                	
					System.out.println("fileReceiver:"+InetAddress.getLocalHost().getHostAddress());
					System.out.println("fileReceiver:"+msgArr[0]+"/"+msgArr[1]);
				   	// 서버의 아이피 전달 받음
            		String ip = msgArr[0];  
            		//서버에서 전송할 파일 이름
            		String fileName = msgArr[1]; 
            		       	
					try {
						// 쓰레드 실행
						new FileReceiver(ip,fileName).start(); 
					} catch (Exception e) {
						System.out.println("FileSender 쓰레드 오류:");
						e.printStackTrace();
					}
					
				// 종료
				}  else if(msg.startsWith("req_exit")){  
                	
					
					
                }
            
            }catch(SocketException e){            	
            	 System.out.println("예외:"+e);
            	 System.out.println("##접속중인 서버와 연결이 끊어졌습니다.");
            	return;
            	 
            } catch(Exception e){            	
                System.out.println("Receiver:run() 예외:"+e);
              
            }
        }//while----
    }//run()------
}//class Receiver -------
 

/////////////////////////////////////////////////////////////////////

//서버로 메시지를 전송하는 클래스
class Sender extends Thread {
    Socket socket;
    DataOutputStream out;
    String name;
   
    //생성자 (매개변수로 소켓과 사용자 이름 받음)
    public Sender(Socket socket){ 
        this.socket = socket;      
        try{
            out = new DataOutputStream(this.socket.getOutputStream());
           
            
        }catch(Exception e){
            System.out.println("예외:"+e);
        }
    }
   
    @Override
    public void run(){ 
       
        Scanner s = new Scanner(System.in);
        //키보드로부터 입력 받기 위한 스캐너 객체 생성
   
        System.out.println("▶이름을 입력해 주세요:");
    	/*try {
    		while(true){ 
	    		System.out.println("▶이름을 입력해 주세요:");
	    	     name = s.nextLine();
				 if(name!=null&&!name.trim().equals("")){				 
					 out.writeUTF("req_logon|"+name);
					 //반복문 중지
					 break; 
				 }else{
					 System.out.println("[##] 이름으로 공백을 입력할 수 없습니다.");
				 }
    		}
		} catch (IOException e1) {
			System.out.println("예외발생:run():이름입력 : "+e1.getMessage());
			e1.printStackTrace();
		}
        */
        //출력스트림이 null이 아니면 반복
        while(out!=null){ 
        	// while문 안에 try-catch문 사용 이유? while문 내부에서 예외가 발생하더라도
        	// 계속 반복할 수 있게끔!
        	try {                    
                
            	String msg = s.nextLine();
            	
               if(msg==null||msg.trim().equals("")){
            		
            	   msg=" ";
            	   //continue; //콘솔에선 공백으로 넘기는 것이 조금 더 효과적.
            	   //System.out.println("공백");
            	}
            	
            	if(MultiClient.chatState == 0){     		
            		//추후 대화명 관련 처리시 사용.
            		
            		 if(!msg.trim().equals("")){				 
            			 name=msg;
                 		 out.writeUTF("req_logon|"+msg);
    					
    				 }else{
    					 System.out.println("[##] 이름으로 공백을 입력할 수 없습니다.\r\n" +
    					 		"▶이름을 다시 입력해주세요:");
    				 }            	
            		
            	}else if(MultiClient.chatState == 1) {//로그온된 상태이며 그룹방을 입력받기 위한 상태
            		//req_enterRoom|대화명|지역명
            		
            		 if(!msg.trim().equals("")){				 
            			 out.writeUTF("req_enterRoom|"+name+"|"+msg);    					
    				 }else{
    					 System.out.println("[##] 공백을 입력할 수 없습니다.\r\n" +
    					 		"▶지역을 다시 입력해주세요:");
    				 }          
            		
            		
            	}else if(msg.trim().startsWith("/")){
            		//명령어 기능 추가. ( /접속자 , /귓속말 상대방아이디 전달할 메시지... 등 )	
            		//클라이언트 선에서 체크

		
            	/*	if(msg.equals("/접속자")
            		   ||msg.startsWith("/귓속말")
            		   ||msg.startsWith("/파일전송")
            		   ||msg.startsWith("/지역")
            		   ||msg.startsWith("/대화신청")
            		   ||msg.startsWith("/대화종료")){   
            			
            			out.writeUTF("req_cmdMsg|"+name+"|"+msg);
            			//req_cmdMsg|대화명|/접속자
            		}else if(msg.equalsIgnoreCase("/exit")){
            			  System.out.println("[##] 클라이언트를 종료합니다.");
            			  System.exit(0);
            			break;
            		}else{
            			System.out.println("[##] 잘못된 명령어입니다."); 
            			
            		}  */	
            		
            		//확장성을 위해 위 코드를 수정
            		
            		if(msg.equalsIgnoreCase("/exit")){
           			  System.out.println("[##] 클라이언트를 종료합니다.");
           			  System.exit(0);
           			  break;
            		}else{
            			out.writeUTF("req_cmdMsg|"+name+"|"+msg);
             			//req_cmdMsg|대화명|/접속자
            		}
            		
            	}/*else if(msg.startsWith("/귓속말")){
            		
            		out.writeUTF("req_whisper|"+name+"|"+msg);     
            	//3 : 상대방이 1:1 대화요청을 한 상태	
            	}*/else if(MultiClient.chatState==3){ 
            		//PvPchat|result)
            		// 메시지 공백 제거
            		msg = msg.trim(); 
            		
        			if(msg.equalsIgnoreCase("y")){
        				out.writeUTF("PvPchat|yes");                      		
        			}else if(msg.equalsIgnoreCase("n")){
        				out.writeUTF("PvPchat|no");                         		
        			}else{        				 
        				System.out.println("입력한 값이 올바르지 않습니다.");   
        				out.writeUTF("PvPchat|no");  
        			}
        			// 1:1대화 요청에 응답완료 상태
        			MultiClient.chatState=2; 
            	//5 : 상대방이 파일 전송을 시도하며 사용자의 수락요청을 기다림
            	}else if(MultiClient.chatState == 5) { 
            		//fileSend|result)
            		if(msg.trim().equalsIgnoreCase("y")){
        				out.writeUTF("fileSend|yes");                      		
        			}else if(msg.trim().equalsIgnoreCase("n")){
        				out.writeUTF("fileSend|no");                         		
        			}else{
        				System.out.println("입력한 값이 올바르지 않습니다."); 
        				out.writeUTF("fileSend|no");          
        			}
            		//파일 전송 수락요청에 대한 응답완료 상태
        			MultiClient.chatState=2; 
            		
            	}else{
            		//req_say|아이디|대화내용
            		out.writeUTF("req_say|"+name+"|"+msg);      			
            	}   
            	
            }catch(SocketException e){            	
	           	 System.out.println("Sender:run()예외:"+e);
	           	 System.out.println("##접속 중인 서버와 연결이 끊어졌습니다.");
	           	return;           	 
           } catch (IOException e) {
                System.out.println("예외:"+e);
           }
        }//while------
      
    }//run()------
}//class Sender-------

/////////////////////////////////////////////////////////////////////