package src.study.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

//import org.omg.CORBA.ExceptionList;

//버튼꾸미기
class RoundedButton extends JButton {
	public RoundedButton() {
        super();
        decorate();
    }
	public RoundedButton(String text) {
        super(text);
        decorate();
    }
	public RoundedButton(Action action) {
		super(action);
		decorate();
    }
	public RoundedButton(Icon icon) {
		super(icon);
		decorate();
    }
	public RoundedButton(String text, Icon icon) {
		super(text, icon);
		decorate();
    }
	protected void decorate() {
		setBorderPainted(false);
		setOpaque(false);
	}
	protected void paintComponent(Graphics g) {
		Color c = new Color(000, 165, 155, 250); //占쏙옙占쏙옙 占쏙옙占쏙옙
		Color o = new Color(255, 255, 255); //占쏙옙占쌘삼옙 占쏙옙占쏙옙
		int width = getWidth();
		int height = getHeight();
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (getModel().isArmed()) {
			graphics.setColor(c.darker());
        } else if (getModel().isRollover()) {
        	graphics.setColor(c.brighter());
        } else {
            graphics.setColor(c);
        }
		graphics.fillRoundRect(0, 0, width, height, 20, 20);
		FontMetrics fontMetrics = graphics.getFontMetrics();
		Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();
		int textX = (width - stringBounds.width) / 2;
		int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
		graphics.setColor(o);
//       graphics.setFont(getFont()); 
		graphics.setFont(new Font("나눔고딕", 0, 13));
		graphics.drawString(getText(), textX, textY);
		graphics.dispose();
		super.paintComponent(g);
	}
}

public class Client extends JFrame implements ActionListener {
    static DefaultListModel<String> listModel = new DefaultListModel<>();
    static DefaultListModel<String> roomModel = new DefaultListModel<>();
    static DefaultListModel<String> joinModel = new DefaultListModel<>();

    String id = "";
    String ip = "";
    JMenu menu, version;
    JMenuBar mb;
    JMenuItem exit, naemChange, roomChange;
    ////////
    Container con;
    JPanel panel, panel1, panel2, panel3, loginBg;
    JList jlist, jl, userList, roomList;
    JScrollPane jspane1, jspane2, jspane3;
    //////
    JFrame room, list;
    JLabel logIp, logPass, logId, userListLabel, roomListLabel;
    JTextField ipTa, passTa, idTa;
    JButton startBttn, joinMember, roomCreate, roomJoin, sendButtn;
    ///////
    String roomInfo;
    //////
    static JTextField tf;
    static JTextArea chatTa, viewTa;
    static Socket socket;
    static BufferedWriter bw;
    static BufferedReader br;

    Client() {
        creatMenu();
        loginUi();
        listUi();
        roomUi();
        charset();	
    }
///////////////////////////////    
    public void listUi() {
    	
    	
    	list = new JFrame();
        list.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        list.setBounds(200, 200, 390, 350);
        list.setVisible(false);
        list.setLayout(null);

        userListLabel = new JLabel("채 팅 접 속 자");
// 		userListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userListLabel.setBounds(44, 30, 100, 15);
        userListLabel.setFont(new Font("나눔고딕", 1, 13));
        list.add(userListLabel);
        userList = new JList(listModel);
        
        userList.setBounds(23, 56, 130, 220);
        list.add(userList);

        roomListLabel = new JLabel("채 팅 방 목 록");
        roomListLabel.setFont(new Font("나눔고딕", 1, 13));
// 		userListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roomListLabel.setBounds(217, 30, 100, 15);
        list.add(roomListLabel);
        roomList = new JList(roomModel);
        roomList.setBounds(173, 56, 180, 180);
        list.add(roomList);

        roomCreate = new RoundedButton("방만들기");
        roomCreate.setBounds(173, 246, 82, 30);
        list.add(roomCreate);

        roomJoin = new RoundedButton("참가하기");
        roomJoin.setBounds(271, 246, 82, 30);
        list.add(roomJoin);

        roomCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String roomInfo = JOptionPane.showInputDialog(list, "방제를 설정해 주세요");
                boolean creat = true;
                if (roomInfo != null) {
                    while(creat){
	                	if (roomInfo.equals("")) {
	                        JOptionPane.showMessageDialog(list, "다시 확인해 주세요.", "방제를 입력해주세요.", JOptionPane.WARNING_MESSAGE);
	                        roomInfo = JOptionPane.showInputDialog(list, "방제를 설정해 주세요");
	
	                    } else {
	                        try {
								bw.write("##RoomCreate##|"+id+"|"+roomInfo);
								bw.newLine();
								bw.flush();
								creat=false;
								list.setVisible(false);
								setVisible(true);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                    }    
                        //채팅창 ui
                    }
                }
            }

        });
        roomJoin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	try {
                    Client.bw.write("##RoomEnter##|" + id + "|" + id);
                    Client.bw.newLine();
                    Client.bw.flush();
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            	list.setVisible(false);
            	setVisible(true);
            }
            

        });
    }

////////////////////////////////////////////////////////////
    public void loginUi() {
        room = new JFrame();
        room.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        room.setBounds(200, 200, 300, 340);
//		room.setLayout(new BorderLayout());

        loginBg = new JPanel();
        loginBg.setBackground(new Color(000, 165, 155, 190));
        loginBg.setLayout(null);
        room.add(loginBg);
//		room.setLocationRelativeTo(null);

        logId = new JLabel("I D");
        logId.setBounds(44, 60, 80, 20);
        logId.setForeground(Color.WHITE);
        idTa = new JTextField(10);
        idTa.setBorder(new EmptyBorder(0, 4, 0, 4));
        idTa.setBounds(148, 56, 100, 30);

        logPass = new JLabel("PASSWORD");
        logPass.setBounds(44, 110, 80, 20);
        logPass.setForeground(Color.WHITE);
        passTa = new JTextField(10);
        passTa.setBorder(new EmptyBorder(0, 4, 0, 4));
        passTa.setBounds(148, 106, 100, 30);

        joinMember = new RoundedButton("회 원 가 입");
        joinMember.setBounds(34, 160, 220, 38);
        joinMember.setForeground(Color.WHITE);
        loginBg.add(joinMember);

        startBttn = new RoundedButton("접 속");
        startBttn.setBounds(34, 205, 220, 38);
        loginBg.add(startBttn);

        loginBg.add(logId);
        loginBg.add(logPass);

        loginBg.add(passTa);
        loginBg.add(idTa);

        room.setVisible(true);

        startBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                id = idTa.getText();
                room.setVisible(false);
                list.setVisible(true);
                
                try {
                    Client.bw.write("##UserID##|" + id+"|"+id);
                    Client.bw.newLine();
                    Client.bw.flush();
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void creatMenu() {
        mb = new JMenuBar();
        menu = new JMenu("메뉴");
        version = new JMenu("버전");
        exit = new JMenuItem("종료");
        naemChange = new JMenuItem("닉네임변경");
        roomChange = new JMenuItem("방나가기");

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Container con = getContentPane();
                int result = JOptionPane.showConfirmDialog(con, "종료하시겠습니까??", "종료", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }

        });
        //미구현
        naemChange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Container con = getContentPane();
                String name = JOptionPane.showInputDialog(con, "바꾸실 닉네임을 정해주세요.", "닉네임 변경");
                if (name != null) {

                }
            }

        });
      //미구현
        roomChange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }

        });

        menu.add(naemChange);
        menu.add(roomChange);
        menu.add(exit);
        mb.add(menu);
        mb.add(version);
        setJMenuBar(mb);
    }
//////////////////////////////
    void roomUi() {
        setTitle("쑥떡숙덕");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(200, 200, 400, 800);
        setLayout(new BorderLayout());

        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        jlist = new JList(joinModel);
        jlist.setPreferredSize(new Dimension(this.getWidth(), 100));
//		jlist.setBackground(new Color(0,102,51,50));
        jspane1 = new JScrollPane(jlist);
        panel1.add(jlist);
        add(panel1, BorderLayout.NORTH);

        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        viewTa = new JTextArea();
        viewTa.setMargin(new Insets(8, 13, 8, 13));
        viewTa.setEditable(false);
        jspane2 = new JScrollPane(viewTa);
        jspane2.setBorder(new EmptyBorder(2, 0, 2, 0));
        jspane2.setPreferredSize(new Dimension(this.getWidth(), 300));
        panel2.add(jspane2);
        add(panel2, BorderLayout.CENTER);

        panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());
        chatTa = new JTextArea();
        sendButtn = new RoundedButton("보내기");
        chatTa.setMargin(new Insets(8, 13, 8, 13));
        jspane3 = new JScrollPane(chatTa);
        jspane3.setBorder(new EmptyBorder(0, 0, 0, 0));
        jspane3.setPreferredSize(new Dimension(this.getWidth(), 110));
        panel3.add(jspane3, BorderLayout.CENTER);
        panel3.add(sendButtn, BorderLayout.EAST);
        add(panel3, BorderLayout.SOUTH);

        this.setVisible(false);

        sendButtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = chatTa.getText();
                try {
                    bw.write("##SendText##|"+id+"|"+msg);
                    
                    bw.newLine();
                    bw.flush();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        String msg = chatTa.getText();
//        try {
//            bw.write(msg);
//            bw.newLine();
//            bw.flush();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }

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

    public static void main(String[] args) throws IOException {
        Client.socket = new Socket("localhost", 9929);

        InputStream is = Client.socket.getInputStream();
        OutputStream os = Client.socket.getOutputStream();
        InputStreamReader isr  = new InputStreamReader(is);
        OutputStreamWriter osw = new OutputStreamWriter(os);

        Client.br = new BufferedReader(isr);
        Client.bw = new BufferedWriter(osw);

        Client myf = new Client();

        try {
            while (true) {
                String data = br.readLine();
                String[] msg = data.split("\\|");
                if(msg[0].equals("##UserID##")){
                	if (listModel.size() == 0) {
                        listModel.addElement(msg[1]);
                    } else {
                        if (!listModel.contains(msg[1])) {
                            listModel.addElement(msg[1]);
                        }
                    }
                	if (joinModel.size() == 0) {
                		joinModel.addElement(msg[1]);
                    } else {
                        if (!joinModel.contains(msg[1])) {
                        	joinModel.addElement(msg[1]);
                        }
                    }
                
                }else if(msg[0].equals("##SendText##")){
                	viewTa.append(msg[1] + ">" + msg[2] + "\n");
                }else if(msg[0].equals("##RoomCreate##")){
                	if (roomModel.size() == 0) {
                		roomModel.addElement(msg[1]);
                    } else {
                        if (!roomModel.contains(msg[1])) {
                        	roomModel.addElement(msg[1]);
                        }
                    }
//                	if(roomModel)
//                	
                }else if(msg[0].equals("##RoomEnter##")){
                	if (joinModel.size() == 0) {
                		joinModel.addElement(msg[1]);
                    } else {
                        if (!joinModel.contains(msg[1])) {
                        	joinModel.addElement(msg[1]);
                        }
                    }
                }
                System.out.println(msg[1]+"val값");
            	
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	
            try {
                osw.close();
                isr.close();
                os.close();
                is.close();
                Client.br.close();
                Client.bw.close();
                Client.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}