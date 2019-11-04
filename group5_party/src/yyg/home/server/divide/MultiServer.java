package yyg.home.server.divide;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
 
/*콘솔 멀티채팅 서버 프로그램*/
public class MultiServer {
	//지역별 해시맵을 관리하는 해시맵
    HashMap<String,HashMap<String,ServerRecThread>> globalMap; 
    ServerSocket serverSocket = null; 
    Socket socket = null;
    //서버 접속자 카운트
    static int connUserCount = 0; 
   
    //생성자
    public MultiServer(){
       globalMap = new HashMap<String,HashMap<String, ServerRecThread>>();
       
       	// 클라이언트의 출력스트림을 저장할 해시맵
    	//clientMap = new HashMap<String,DataOutputStream>(); 
       	
       	// 해시맵 동기화 설정
        Collections.synchronizedMap(globalMap); 
        
        HashMap<String,ServerRecThread> group01 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group01); 
        
        HashMap<String,ServerRecThread> group02 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group02); 
        
        HashMap<String,ServerRecThread> group03 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group03); 
       
        HashMap<String,ServerRecThread> group04 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group04); 
        
        HashMap<String,ServerRecThread> group05 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group05); 
        
        HashMap<String,ServerRecThread> group06 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group06); 
        
        HashMap<String,ServerRecThread> group07 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group07); 
        
        
        globalMap.put("서울",group01);
        globalMap.put("경기",group02);
        globalMap.put("충청",group03);
        globalMap.put("강원",group04);
        globalMap.put("전라",group05);
        globalMap.put("경상",group06);
        globalMap.put("제주",group07);
        
        
    }
    
    // 서버 구동
    public void init(){
        try{
        	// 5001 포트로 서비스
            serverSocket = new ServerSocket(5001); 
            System.out.println("##서버 시작");
            // 서버 싱행되는 동안 클라이언트 접속을 계속 기다리는 녀석
            while(true){ 
            	// 클라 기다리다 접속되면 socket 객체 생성
                socket = serverSocket.accept(); 
                // 클라 정보 출력
                System.out.println(socket.getInetAddress()+":"+socket.getPort()); 
                // thread 생성
                Thread msr = new ServerRecThread(socket); 
                // thread 시작
                msr.start();
            }      
           
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   
    
    
    /* 접속된 모든 클라이언트에게 메시지 전달 */
    public void sendAllMsg(String msg){
       
        Iterator global_it = globalMap.keySet().iterator();
       
        while(global_it.hasNext()){
            try{
            	HashMap<String, ServerRecThread> it_hash = globalMap.get(global_it.next());
            	Iterator it = it_hash.keySet().iterator();
            	while(it.hasNext()){
            		ServerRecThread st = it_hash.get(it.next());
            		st.out.writeUTF(msg);
            	}               
            }catch(Exception e){
                System.out.println("예외:"+e);
            }
        }
    }//sendAllMsg()-----------
    
    
    /* 해당 클라이언트가 속해있는 그룹에만 메시지 전달 */
    public void sendGroupMsg(String loc,String msg){       
       
    	HashMap<String, ServerRecThread> gMap = globalMap.get(loc);    	
    	Iterator<String> group_it = globalMap.get(loc).keySet().iterator();        
        while(group_it.hasNext()){
            try{ 	
            		ServerRecThread st = gMap.get(group_it.next());
            		// 1:1 대화모드가 아닌 사람에게만.
            		if(!st.chatMode){ 
            			st.out.writeUTF(msg);	
            		}
            }catch(Exception e){
                System.out.println("예외:"+e);
            }
        }   
    }//sendGroupMsg()-----------
    
    
    /**1:1 대화*/ 
    public void sendPvPMsg(String loc,String fromName, String toName, String msg){
     
    		try {
				globalMap.get(loc).get(toName).out.writeUTF(msg);
				globalMap.get(loc).get(fromName).out.writeUTF(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
         
    }//sendPvPMsg()-----------
    
    /** 귓속말 */
    public void sendToMsg(String loc, String fromName, String toName, String msg){    	
     	  
 	   	try{  	
 	   		
 	   		
 	   			globalMap.get(loc).get(toName).out.writeUTF("whisper|"+fromName+"|"+msg);
             	globalMap.get(loc).get(fromName).out.writeUTF("whisper|"+fromName+"|"+msg);
             	
           }catch(Exception e){
        	    System.out.println("예외:"+e);
           }
         
     }//sendAllMsg()-----------
    
    
    /**각 그룹의 접속자 수, 서버에 접속된 유저를 반환**/
    public String getEachMapSize(){
    	return getEachMapSize(null);     
    }//getEachMapSize()-----------
    
    /**각 그룹의 접속자 수, 서버에 접속된 유저 반환
     * 추가 지역을 전달 받으면 해당 지역 체크
     * */
    public String getEachMapSize(String loc){
    	
        Iterator global_it = globalMap.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        int sum=0;
        sb.append("=== 그룹 목록 ==="+System.getProperty("line.separator"));
        while(global_it.hasNext()){
            try{
            	String key = (String) global_it.next();
            	
            	HashMap<String, ServerRecThread> it_hash = globalMap.get(key);
            	//if(key.equals(loc)) key+="(*)"; // 현재 유저가 접속된 곳 표시
            	int size = it_hash.size();
            	sum +=size;
            	sb.append(key+": ("+size+"명)"+(key.equals(loc)?"(*)":"")+"\r\n");
                
            }catch(Exception e){
                System.out.println("예외:"+e);
            }
        }
        //sb.append("▣ 현재 대화에 참여하고 있는 유저 수 :"+ MultiServer.connUserCount);
        sb.append("▣ 현재 대화에 참여하고 있는 유저 수 :"+ sum+ "명 \r\n");
        //System.out.println(sb.toString());
        return sb.toString();
    }//getEachMapSize()-----------
    
    
    /**접속된 유저 중복체크*/        
    public boolean isNameGlobla(String name){
    	boolean result=false;
        Iterator<String> global_it = globalMap.keySet().iterator();
        while(global_it.hasNext()){
            try{
            	String key = global_it.next();            	
            	HashMap<String, ServerRecThread> it_hash = globalMap.get(key);
                if(it_hash.containsKey(name)){
                	// 중복된 아이디가 존재
                	result= true; 
                	break;
                }
            	
            }catch(Exception e){
                System.out.println("isNameGlobla()예외:"+e);
            }
        }
     
        return result;
    }//isNameGlobla()-----------

    
    /**문자열 null 값 및 ""은 대체 문자열로 삽입가능.*/
    public String nVL(String str, String replace){
    	String output="";
    	if(str==null || str.trim().equals("")){
    		output = replace; 		
    	}else{
    		output = str;
    	}
    	return output;    	
    }
    
    
    
    //main 메소드
    public static void main(String[] args) {
        MultiServer ms = new MultiServer(); //서버객체 생성.
        ms.init();//실행.
    }//main()------  
   
    
    
    ////////////////////////////////////////////////////////////////////////
    //----// 내부 클래스 //--------//
   
    // 클라이언트로부터 읽어온 메시지를 다른 클라이언트(socket)에 보내는 역할을 하는 메소드
    class ServerRecThread extends Thread {
       
        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        String name=""; //이름 저장
        String loc="";  //지역 저장
        String toNameTmp = null;//1:1대화 상대 
        String fileServerIP; //파일 서버 아이피 저장
        String filePath; //파일 서버에서 전송할 파일 패스 저장.
        boolean chatMode; //1:1대화모드 여부
        
        
        //생성자.
        public ServerRecThread(Socket socket){
            this.socket = socket;
            try{
                //Socket으로부터 입력스트림을 얻는다.
                in = new DataInputStream(socket.getInputStream());
                //Socket으로부터 출력스트림을 얻는다.
                out = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                System.out.println("ServerRecThread 생성자 예외:"+e);
            }
        }//생성자 ------------
        
        
        
        /**접속된 유저리스트 문자열로 반환*/        
        public String showUserList(){
         	
         	StringBuilder output = new StringBuilder("==접속자 목록==\r\n");
         	//해시맵에 등록된 사용자 이름 가져오기
         	Iterator it = globalMap.get(loc).keySet().iterator(); 
         	// 반복하면서 사용자 이름을 StringBuilder에 추가
         	while(it.hasNext()){ 
                 try{
                 	String key= (String) it.next();                 	            	
                    //out.writeUTF(output);
                 	// 현재 사용자 체크
                 	if(key.equals(name)){ 
                 		key += " (*) ";
                 	}    
                 	
                 	output.append(key+"\r\n");                 	
                 }catch(Exception e){
                     System.out.println("����:"+e);
                 }
             }//while---------
         	output.append("=="+ globalMap.get(loc).size()+"명 접속중==\r\n");
         	System.out.println(output.toString());
     		return output.toString();
         }//showUserList()-----------
        
       
       /**메시지 파서 */     
       public String[] getMsgParse(String msg){
        	System.out.println("msgParse():msg?   "+ msg);        	
        	String[] tmpArr = msg.split("[|]");        	
        	return tmpArr;
        }
        
        // 쓰레드를 사용하기 위해서 run()메소드 재정의
        @Override
        public void run(){ 
        	//현재 클라이언트가 저장되어있는 해시맵
        	HashMap<String, ServerRecThread> clientMap=null;           
           
        	try{   
        		// 입력스트립이 null이 아니면 반복
                while(in!=null){ 
                	// 입력스트림을 통해 읽어온 문자열을 msg에 할당
                    String msg = in.readUTF();                 	
                    String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
                    
                    //메시지 처리 ----------------------------------------------
                    // 로그온 시도(대화명)
                    if(msg.startsWith("req_logon")){                     	
                    	//req_logon|대화명
                    	                    	
                    	if(!(msgArr[0].trim().equals(""))&&!isNameGlobla(msgArr[0])){
                    		//넘어온 대화명은 전역변수 name에 저장
	                    	name = msgArr[0];
	                    	//접속자 수 증가. (static 변수를 사용)
	                    	MultiServer.connUserCount++; 
	                    	// 접속된 클라이언트에게 그룹목록 제공
	                    	out.writeUTF("logon#yes|"+getEachMapSize()); 
	                    }else{
	                    	 out.writeUTF("logon#no|err01");
	                    }
                    // 그룹 입장 시도
                    }else if(msg.startsWith("req_enterRoom")){ 
                    	
                    	//req_enterRoom|대화명|지역
                    	// 메시지에서 지역부분만 추출하여 전역변수에 저장
                    	 loc = msgArr[1]; 
                    	 
                    	 if(isNameGlobla(msgArr[0])){
                    		 out.writeUTF("logon#no|"+name);   
                    		 
                    	 }else if(globalMap.containsKey(loc)){
                    		 sendGroupMsg(loc, "show|[##] "+name + "님이 입장하셨습니다.");
                    		 // 현재 그룹의 해시맵을 따로 저장.
                        	 clientMap= globalMap.get(loc); 
                        	 // 현재 MultiServerRec 인스턴스를 클라이언트 맵에 저장. 
                        	 clientMap.put(name, this); 
                        	 // 서버에 그룹리스트 출력
                        	 System.out.println(getEachMapSize()); 
                        	 // 접속된 클라이언트에게 그룹목록 제공
                        	 out.writeUTF("enterRoom#yes|"+loc); 
                        	 
                    	 }else{                    		
                    		 out.writeUTF("enterRoom#no|"+loc);                     		 
                    	 }
                    	
                    	 
                    	
                    	 
                    // 명령어 전송
                    }else if(msg.startsWith("req_cmdMsg")){ 
                    	//req_cmdMsg|대화명|/접속자
                    	if(msgArr[1].trim().equals("/접속자")){ 
                    		// 접속자 출력
                			out.writeUTF("show|"+showUserList());   
                		
							
                		}else if(msgArr[1].trim().startsWith("/귓속말")){
                			//req_cmdMsg|대화명|/귓속말 상대방대화명 대화내용
                			//받아온 msg를 " "(공백)을 기준으로 3개로 분리
                			String[] msgSubArr = msgArr[1].split(" ",3); 
                			//System.out.println("msgSubArr:"+Arrays.toString(msgSubArr)+"/"+name+"/"+loc);
                			
                			if(msgSubArr==null||msgSubArr.length<3){
                				out.writeUTF("show|[##] 귓속말 사용법이 잘못되었습니다.\r\n usage : /귓속말 [상대방이름] [보낼메시지].");
                			}else if(name.equals(msgSubArr[1])){
                				out.writeUTF("show|[##] 자신에게 귓속말을 할 수 없습니다.\r\n usage : /귓속말 [상대방이름] [보낼메시지].");
                			}else{
                				String toName = msgSubArr[1];
              					//String toMsg = "귓:from("+name+")=>"+((msgArr[2]!=null)?msgArr[2]:"");
                    			String toMsg = msgSubArr[2];
                    			// 유저 체크
                				if(clientMap.containsKey(toName)){ 
                					System.out.println("귓속말!");
                    				sendToMsg(loc,name,toName,toMsg);
                    				
                    			}else{
                    				out.writeUTF("show|[##] 해당 유저가 존재하지 않습니다.");
                    			}
                				
                			}//if
                			
                			
                    	}else if(msgArr[1].trim().startsWith("/지역")){                    		
                			
                			String[] msgSubArr = msg.split(" ");
                			// 변경할 지역을 입력하지 않고 /지역 만 입력했을 경우 지역목록 출력
                			if(msgSubArr.length==1){                 				
                				out.writeUTF("show|"+getEachMapSize(loc));                				
                			}else if(msgSubArr.length==2) {
                				// 지역
                				String tmpLoc = msgSubArr[1]; 
                				
                				if(loc.equals(tmpLoc)){
                					out.writeUTF("show|[##] 명령어 사용법이 잘못되었습니다.\r\n "
                							+ "본인이 참여하고 있는 지역을 지정하실 수 없습니다.\r\n " +
		                					    "usage : 지역목록 보기 : /지역" +
	                    						"\r\n usage : 지역변경 하기 : /지역 [변경할 지역 이름].");
		                			continue;
                				}
                				//지역 체크
                    			if(globalMap.containsKey(tmpLoc)&& !this.chatMode){ 
                    					out.writeUTF("show|[##] 지역을 "+loc+"에서 "+ tmpLoc+"로 변경합니다. ");                			
                    					// 현재 지역 해시맵에서 해당 스레드 제거
	                    				clientMap.remove(name);
	                        			sendGroupMsg(loc, "show|[##] "+name+"님이 퇴장하셨습니다.");
	                        			
	                        			System.out.println("이전지역("+loc+")에서 "+name +"제거");
	                        			loc = tmpLoc;
	                    				clientMap = globalMap.get(loc);
	    	                			sendGroupMsg(loc, "show|[##] "+name+"님이 입장하셨습니다.");
	    	                			// 새로 변경된 지역에 서버 스레드 저장.
	    	                			clientMap.put(name, this); 	
                    			
                    			}else{
                    				out.writeUTF("##입력한 지역이 존재하지 않거나 현재 이동할 수 없는 상태입니다.");
                    			}//if-----
	                			
                			}else{
                				out.writeUTF("show|[##] 명렁어 사용법이 잘못되었습니다.\r\n " +
                						"usage : 지역목록 보기 : /지역" +
                						"\r\n usage : 지역변경 하기 : /지역 [변경할 지역 이름].");
                				
                			}//if---------
                			
                			
                		}else if(msgArr[1].trim().startsWith("/대화신청")){
                			String[] msgSubArr =  msgArr[1].split(" ",2);
                			           			
                			
                			if(msgSubArr.length!=2){
                				out.writeUTF("show|[##] 명령어 사용법이 잘못되었습니다.\r\n " +
                						"usage : 1:1대화신청하기 : /대화신청 [상대방대화명]");
                				continue;
                			}else if(name.equals(msgSubArr[1])){
	                				out.writeUTF("show|[##] 명령어 사용법이 잘못되었습니다.\r\n "
	                						+ "본인의 대화명을 지정하실 수 없습니다."
	                						+ "1:1대화를 할 상대방의 대화명을 지정해주세요.\r\n " +
	                						"usage : 1:1대화신청하기 : /대화신청 [상대방대화명]");
	                			continue;
                			}
                			
                			if(!chatMode){
	                			
                				String toName = msgSubArr[1].trim(); 
	                			out.writeUTF("show|[##] "+toName +"님께 대화신청을 합니다. ");
	                			//유저체크
	                			if(clientMap.containsKey(toName) && !clientMap.get(toName).chatMode){
	                				//req_PvPchat|신청자|응답자|메시지 .... 취소
	                				//req_PvPchat|메시지 .... 로 변경
	                				
	                				clientMap.get(toName).out.writeUTF("req_PvPchat|[##] "+name
	                						+"님께서 1:1대화신청을 요청하였습니다.\r\n 수락하시겠습니까?(y,n)");	
	                				toNameTmp = toName;
	                				clientMap.get(toNameTmp).toNameTmp = name;
	                			}else{
	                				out.writeUTF("show|[##] 해당 유저가 존재하지 않거나 상대방이"
	                						+ " 1:1대화를 할 수 없는 상태입니다.");
	                			}
	                			
                			}else{
                				out.writeUTF("show|[##] 1:1대화 모드이므로 대화신청을 하실 수 없습니다.");
                			}
                			
                		}else if(msgArr[1].startsWith("/대화종료")){
	            			 
                			if(chatMode){
                				// 1:1대화모드 해제
                				chatMode = false; 
                				out.writeUTF("show|[##] "+toNameTmp+"님과 1:1대화를 종료합니다.");
                				// 상대방도 1:1대화모드 해제
                				clientMap.get(toNameTmp).chatMode=false; 
                				clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name +"님께서 1:1대화를 종료하였습니다.");
                    			toNameTmp="";
                    			clientMap.get(toNameTmp).toNameTmp="";
                    			
	                        }else{
	                        	out.writeUTF("show|[##] 1:1�대화중일때만 사용할 수 있는 명령어입니다. ");
	                        }
                			
                		}else if(msgArr[1].trim().startsWith("/파일전송")){   
                			
                			if(!chatMode){
                				out.writeUTF("show|[##] 1:1대화 중일 때만 사용할 수 있는 명령어입니다. ");
                				continue;                				
                			}
                			
                			String[] msgSubArr = msgArr[1].split(" ",2);
                			if(msgSubArr.length!=2){
                				out.writeUTF("show|[##] 파일 전송 명령어 사용법이 잘못되었습니다.\r\n "
                						+ "usage : /파일전송 [전송할 파일 경로]");
                				continue;                				
                			}
                			filePath = msgSubArr[1];                			
                			File sendFile = new File(filePath);
                			String availExtList = "txt,java,jpeg,jpg,png,gif,bmp";
                			
                			
                			if(sendFile.isFile()){         				
                				String fileExt = filePath.substring(filePath.lastIndexOf(".")+1);
                				if(availExtList.contains(fileExt)){
                					//파일서버역할을 하는 클라이언트의 아이피 주소를 알기 위해 소켓 객체 얻어옴
	                				Socket s = globalMap.get(loc).get(toNameTmp).socket; 
	                    			
	                    			
	                    			//System.out.println("s.getLocalSocketAddress()=>"+s.getLocalSocketAddress());
	                    			//System.out.println("s.getLocalAddress()=>"+s.getLocalAddress());
	                    			System.out.println("s.getInetAddress():파일서버 아이피=>"+s.getInetAddress()); 
	                    			//파일 서버 역할을 하는 클라이언트의 아이피 출력
	                    			
	    			                fileServerIP = s.getInetAddress().getHostAddress();
	    			                clientMap.get(toNameTmp).out.writeUTF("req_fileSend|[##] "
	    			                +name +"님께서 파일["+sendFile.getName()+"] 전송을 시도합니다. \r\n"
	    			                		+ "수락하시겠습니까?(Y/N)");			                
	    			                out.writeUTF("show|[##] "+toNameTmp 
	    			                		+"님께 파일["+sendFile.getAbsolutePath()+"] 전송을 시도합니다.");
	    			               
                				}else{
                					
                					out.writeUTF("show|[##] 전송가능한 파일이 아닙니다. \r\n["+availExtList
                							+"] 확장자를 가진 파일만 전송 가능합니다.");                				
                				} //if               			
                			
                			}else{                				
                				out.writeUTF("show|[##] 존재하지 않는 파일입니다.");                				
                			} //if
                		}else{
                			out.writeUTF("show|[##] 잘못된 명령어입니다.");
                		}//if
                    // 대화내용 전송
                    }else if(msg.startsWith("req_say")){ 
                    	  if(!chatMode){
                    		//req_say|아이디|대화내용
                          	sendGroupMsg(loc, "say|"+name+"|"+msgArr[1]);
                          	//출력스트림으로 보낸다.
                          }else{
                          	sendPvPMsg(loc, name,toNameTmp , "say|"+name+"|"+msgArr[1]);
                          }
                    // 귓속말 전송
                    }else if(msg.startsWith("req_whisper")){ 
                    	if(msgArr[1].trim().startsWith("/귓속말")){
                			//req_cmdMsg|대화명|/귓속말 상대방대화명 대화내용
                    		// 받아온 msg를 " "(공백)을 기준으로 3개로 분리
                			String[] msgSubArr = msgArr[1].split(" ",3); 
                			               	    			
                			if(msgSubArr==null||msgSubArr.length<3){
                				out.writeUTF("show|[##] 귓속말 사용법이 잘못되었습니다.\r\n "
                						+ "usage : /귓속말 [상대방이름] [보낼메시지].");
                			}else{
                				String toName = msgSubArr[1];
              					//String toMsg = "귓:from("+name+")=>"+((msgArr[2]!=null)?msgArr[2]:"");
                    			String toMsg = msgSubArr[2];
                    			// 유저 체크
                				if(clientMap.containsKey(toName)){ 
                    				sendToMsg(loc,name,toName,toMsg);
                    				
                    			}else{
                    				out.writeUTF("show|[##] 해당 유저가 존재하지 않습니다.");
                    			}
                				
                			}//if
                    	}//if
                    // 1:1대화신청 수락 결과에 대한 처리
                    }else if(msg.startsWith("PvPchat")){ 
                    	//PvPchat|result                    	
                    	String result = msgArr[0];                				
            			if(result.equals("yes")){                				
            				chatMode = true;    
            				clientMap.get(toNameTmp).chatMode=true;
            				System.out.println("##1:1대화 모드 변경");                				
            	        	try {
            					out.writeUTF("show|[##] "+toNameTmp + "님과 1:1 대화를 시작합니다.");
            					clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name + "님과 1:1 대화를 시작합니다.");
            				} catch (IOException e) {
            					e.printStackTrace();
            				}
            			}else /*(r.equals("no"))*/{
            				clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name+" 님께서 대화신청을 거절하셨습니다.");
            			}            			
            		// 파일 전송
                    } else if(msg.startsWith("fileSend")){ 
                    	//fileSend|result    
                    	String result = msgArr[0]; 
                    	if(result.equals("yes")){
            				System.out.println("##파일전송##YES");                				
            	        	try {		               
            	        		String tmpfileServerIP = clientMap.get(toNameTmp).fileServerIP;
            	        		String tmpfilePath = clientMap.get(toNameTmp).filePath;
            	        		
            	        		//fileSender|filepath;     
            	        		clientMap.get(toNameTmp).out.writeUTF("fileSender|"+tmpfilePath); 
            	        		//파일을 전송할 클라이언트에서 서버소켓을 열고 filePath로 저장된 파일을 읽어와서 OutputStream으로 출력
            	        		
            	        		//fileReceiver|ip|fileName; 
            	        		//파일 명만 추출
            	        		//String fileName = tmpfilePath.substring(tmpfilePath.lastIndexOf("\\")+1); 
            	        		String fileName = new File(tmpfilePath).getName();
            	        		out.writeUTF("fileReceiver|"+tmpfileServerIP+"|"+fileName);    			        					
            	        		
            	        		/*리셋*/
            	        		clientMap.get(toNameTmp).filePath="";
            	        		clientMap.get(toNameTmp).fileServerIP="";
            	        		
            				} catch (IOException e) {
            					e.printStackTrace();
            				}
            			}else /*(result.equals("no"))*/{
            				clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name+" 님께서 파일 전송을 거절하였습니다.");
            			}//if            			
                    // 종료
	                }else if(msg.startsWith("req_exit")){ 
	                	
	                }
                    //------------------------------------------------- 메시지 처리
                    
              
                }//while()---------
            }catch(Exception e){
                System.out.println("MultiServerRec:run():"+e.getMessage() + "----> ");
                //e.printStackTrace();
            }finally{
                //예외가 발생할 때 퇴장. 해시맵에서 해당 데이터 제거
                //보통 종료하거나 나가면 java.net.SocketException: 예외 발생
                if(clientMap!=null){
                	clientMap.remove(name);
                	sendGroupMsg(loc,"## "+ name + "님이 퇴장하셨습니다.");
                	System.out.println("##현재 서버에 접속된 유저는 "+(--MultiServer.connUserCount)+"명입니다.");
                }               
            }
        }//run()------------
    }//class MultiServerRec-------------
    //////////////////////////////////////////////////////////////////////
}
 