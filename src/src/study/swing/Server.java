package src.study.swing;

import java.lang.reflect.Field;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.io.*;
import java.util.HashMap;

public class Server extends Thread {
    static ArrayList<Socket> clients = new ArrayList<>();//소켓
    static HashMap<String, Object> clientsInfo = new HashMap<>();//아이디,소켓
    static HashMap<String, ArrayList> roomInfoList = new HashMap<>();//방제목,clientsInfo
    static Socket sock;

    public Server(Socket sock) {
        this.sock = sock;
        charset();
    }

    public void charset() {
        System.setProperty("file.encoding", "UTF-8");
        Field charset;
        try {
            charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //�����尡 �ϴ� ��
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        InputStreamReader isr = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            is = sock.getInputStream();
            os = sock.getOutputStream();
            isr = new InputStreamReader(is);
            osw = new OutputStreamWriter(os);
            br = new BufferedReader(isr);
            bw = new BufferedWriter(osw);

            int count;
            while (true) {
                String msg = br.readLine();

                System.out.println("Receive message : " + msg);
                String[] strArr = msg.split("\\|");
                String command="";
                String id = "";
                String data = "";
                if(strArr.length>0){
                	command = strArr[0];
                    id = strArr[1];
                    data = strArr[2];
                }
                
                System.out.println("command : " + command);
                System.out.println("id : " + id);
                System.out.println("data : " + data);

                if (command.equals("##UserID##")) {
                    // 접속하기 눌럿을때
                    // 회원 목록에 추가 - 아이디 중복??
                    // 방 목록, 회원목록 클라이언트에 응답
              	
                	clientsInfo.put(data, this.sock);
                	
                	for (Socket s : clients) {
                		OutputStream outputStream = s.getOutputStream();
                		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                		Set<String> key = clientsInfo.keySet();//아이디,소켓리스트
                        for(String k :key){
                        	bufferedWriter.write("##UserID##|"+ k);
                        	bufferedWriter.newLine();
                        	bufferedWriter.flush();
                        }
                	}
             
                } else if (command.equals("##RoomCreate##")) {
                    // 방만들기 - 방이름 입력하고 확인 눌렀을때
                    // 받은 방이름을 방리스트에 추가 - 전에 방 중복 체크
                    // 자동 접속되니까 그 방의 참가자 리스트에 추가
                	
                	roomInfoList.put(data, clients);//방제,
                	for (Socket s : clients) {
                		OutputStream outputStream = s.getOutputStream();
                		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                		Set<String> key = roomInfoList.keySet();

                        for(String k :key){
                        	bufferedWriter.write("##RoomCreate##|"+ k);
                        	bufferedWriter.newLine();
                        	bufferedWriter.flush();
                        }
                	}

//                    System.out.println(this.sock);

                } else if (command.equals("##RoomEnter##")) {
                    // 참가하기 눌렀을때
                    // 누른사람 ID, 참가할 방 이름 받기
                    // 그 방 참가자 리스트에 ID 추가
                	
                	for (Socket s : clients) {
                		OutputStream outputStream = s.getOutputStream();
                		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                		Set<String> key = clientsInfo.keySet();

                        for(String k :key){
                        	bufferedWriter.write("##RoomEnter##|"+ k);
                        	bufferedWriter.newLine();
                        	bufferedWriter.flush();
                        }
                	}

                } else if (command.equals("##SendText##")) {
                    // 채팅 보내기
                    // 채팅 받은거 같은방 사람들한테 보내기
                	for (Socket s : clients) {
                		OutputStream outputStream = s.getOutputStream();
                		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                		
                		
//                		 for(String k :key){
                			bufferedWriter.write("##SendText##|"+id+ "|"+ data);
                         	bufferedWriter.newLine();
                         	bufferedWriter.flush();
//                         }
                		
                		
                	}	
                }

//                
            }

        } catch (IOException e) {

        } finally {
            try {
                bw.close();
                br.close();
                osw.close();
                isr.close();
                os.close();
                is.close();
                sock.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(9929);

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println(serverSocket + ": accept");
            clients.add(client);

            Server myServer = new Server(client);
            myServer.start();
        }
    }

}
