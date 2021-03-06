/*
 * @authors: Zain Quraishi
 * @date: 2018-06-29
 * @filename: ChatWindowUI.java
 * @description: Main client-side chat window. Processes user input, updates UI, sends and receives information to server.
*/

package Client;

import javax.swing.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import HelperClasses.Block;
import HelperClasses.Data;
import HelperClasses.DataEncryption;
import HelperClasses.TransferData;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PingServer extends TimerTask {
	private int reqID;
	private String cname;
	private String serverIP;
	private int serverPort;
	
	public PingServer(int req, String cName, String serverIP, int serverPort) {
		this.reqID = req;
		this.cname = cName;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}
	
	public void run() {
		try {
			int req = 0;
			req = TransferData.serverRequest(reqID, ChatWindowUI.uid, cname, ChatWindowUI.convLog.size(), serverIP, serverPort);

			if (req == 1) {
				File f = new File(cname + ".json");
				Path newPath = Paths.get("local/" + f);
				BufferedReader newReader = new BufferedReader(new FileReader(newPath.toString()));
		        Gson gson = new Gson();
		        Type lType = new TypeToken<ArrayList<Block>>() {}.getType();
		        ChatWindowUI.convLog = null; 
		        ChatWindowUI.convLog = gson.fromJson(newReader, lType);
		        newReader.close();
		        ChatWindowUI.fillContent(ChatWindowUI.convLog);
			}
		} catch (IOException | InterruptedException e) {
			System.out.println("Could not request update from server...");
		}
	}
}

public class ChatWindowUI extends Thread {
private Timer timer;
	
private JPanel panel;
private JTextArea textField;
private static JTextArea textArea;
private JFrame frame;
private JButton exit;
boolean typing;

private String convName;
static ArrayList<Block> convLog;
static String uid;

private String serverIP; 
private int serverPort;

private int first = 0;

    public ChatWindowUI(String sIP, int sPort, String UID, String convName, ArrayList<Block> convLog){
    	serverIP = sIP;
    	serverPort = sPort;
        uid = UID;	
    	this.convName = convName;
        ChatWindowUI.convLog = convLog;
        generateChat(this.convName, ChatWindowUI.convLog);
        
        timer = new Timer();
        Random random = new Random();
        int refresh = random.nextInt(5000) + 1200;
        timer.schedule(new PingServer(3, convName, serverIP, serverPort), 50, refresh);
    }
    public static void fillContent(ArrayList<Block> log) {
        textArea.setText("");
    	if (log.size() > 1) {
        	String pattern = "([0-9]){2}:([0-9]){2}:([0-9]){2} [A|P]{1}[M]{1}$";
        	Pattern pat = Pattern.compile(pattern);

        	textArea.append(log.get(0).data.getMessage() + "\n");
	        for (int i = 1; i < log.size(); i++) {
	        	Matcher match = pat.matcher(log.get(i).getTimeStamp());
	        	if (match.find()) {
	        		String out = log.get(i).data.getUserID() +  " [" + match.group() + "]: " + log.get(i).data.getMessage();
	        		textArea.append(out + "\n");
	        	}
	        }
        }
        else textArea.append(log.get(0).data.getMessage() + "\n");
    }

    private void generateChat(String convName, ArrayList<Block> log){
    	 frame = new JFrame(uid);
    	 textField = new JTextArea();
		 textField.addFocusListener(new FocusListener() {
		     @Override
		     public void focusGained(FocusEvent e) {
		     	if (textField.getText().equals("Enter Message...") && textField.hasFocus()) {textField.setText("");}            	
		     }
		     @Override
		     public void focusLost(FocusEvent e) {
		     }
		 });
		
		 textField.addKeyListener(new KeyAdapter(){
		     public void keyPressed(KeyEvent key){
		     	if(key.getKeyCode()==KeyEvent.VK_ENTER && !textField.getText().equals("Enter Message...") && !textField.getText().isEmpty()) {
		 		try {
					logMessage(textField.getText(), log);
				} catch (IOException e) {
					e.printStackTrace();
				}
		 	}
		 }
		
		 public void keyReleased(KeyEvent key){
		     if(key.getKeyCode()==KeyEvent.VK_ENTER && !textField.getText().equals("Enter Message...") && !textField.getText().isEmpty()) {
		    	 textField.setText("");
		     	}
		     }
		 });
		
	     exit = new JButton("Exit");
		 exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.cancel();
				//t.stop()
				frame.dispose();
				System.exit(0);
			}
		 });
         
        frame.setTitle("WutsGud - " + convName);

        //CREATE TEXT ENTRY FIELD
        panel = new JPanel();
        panel.setLayout(new GridLayout(2,1));

        textField.setLineWrap(true);
        
        textField.setText("Enter Message...");
        panel.add(textField);
      
        panel.add(exit, BorderLayout.SOUTH);
        frame.add(panel,BorderLayout.SOUTH);
         
        //CREATE MESSAGE VIEWING SPACE
        textArea=new JTextArea();
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setForeground(Color.BLUE);

        textArea.setEditable(false);
       
        // Set some margin, for the text
        textArea.setMargin(new Insets(7,7,7,7));
        
        //POPULATE MESSAGES IN WINDOW
        fillContent(convLog);
        
        
        JScrollPane scroll = new JScrollPane(textArea);
        frame.add(scroll);
       
        //FRAME INFO
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(350,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
      
    }
    
    //USER HITS ENTER
    public ArrayList<Block> logMessage(String text, ArrayList<Block> log) throws IOException{
        // If text is empty return
        if(text.trim().isEmpty()) return null;

        String timeStamp = new SimpleDateFormat("hh:mm:ss a").format(new Date());
        String out = uid +  " [" + timeStamp + "]: " + text + "\n";
        textArea.append(out);
        
        log.add(new Block(new Data(uid,text), log.get(log.size()-1).getCurrentHash(), DataEncryption.hashSHA256(log.get(log.size()-1).getPreviousHash() + log.get(log.size()-1).data + log.get(log.size()-1).getTimeStamp())));
        
        TransferData.writeToFile(log, convName);

        first = TransferData.sendFile(convName, uid, log, first, serverIP, serverPort);
        
        return log;
    }
}