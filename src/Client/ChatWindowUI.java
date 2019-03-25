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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PingServer extends TimerTask {
	private int reqID;
	private String cname;
	private String tStamp;
	private String serverIP;
	private int serverPort;
	
	public PingServer(int req, String cName, String tStamp, String serverIP, int serverPort) {
		this.reqID = req;
		this.cname = cName;
		this.tStamp = tStamp;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}
	
	public void run() {
		try {
			int req = 0;
			TransferData.serverRequest(reqID, cname, tStamp, serverIP, serverPort);
			if (req == 1)
				ChatWindowUI.updateLog(cname, tStamp);
		} catch (IOException e) {
			System.out.println("Could not request update from server...");
		}
	}
}

//timer = new Timer();
//timer.schedule(new run(), 0, 500);

public class ChatWindowUI extends Thread {
private Timer timer;
	
private JPanel panel;
private JTextArea textField;
private static JTextArea textArea;
private JFrame frame;
private JButton exit;
boolean typing;

private String convName;
private static ArrayList<Block> convLog;
private String uid;

private String serverIP; 
private int serverPort;

//private Thread t;
private int first = 0;

    public ChatWindowUI(String sIP, int sPort, String UID, String convName, ArrayList<Block> convLog){
    	serverIP = sIP;
    	serverPort = sPort;
        uid = UID;	
    	this.convName = convName;
        ChatWindowUI.convLog = convLog;
        generateChat(this.convName, ChatWindowUI.convLog);
        
        timer = new Timer();
        timer.schedule(new PingServer(3, convName, convLog.get(convLog.size()-1).getTimeStamp(), serverIP, serverPort), 0, 3500);
    }
       
    public static void updateLog(String cname, String localTStamp) throws FileNotFoundException {
    	File cLog = new File("local/" + cname + ".json");

		if (cLog.exists()) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(cLog));
	        Gson gson = new Gson();
	        Type listType = new TypeToken<ArrayList<Block>>() {}.getType();
	        ArrayList<Block> newLog = gson.fromJson(bufferedReader, listType);
//	        convLog.clear();
//	        convLog.addAll(newLog);
	        convLog = newLog;
	        textArea.setText("");
	        fillContent(convLog);
		}
    }
		
    private void generateChat(String convName, ArrayList<Block> convLog){
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
					logMessage(textField.getText(), convLog);
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
				//t.stop();
				frame.dispose();
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
    
    public static void fillContent(ArrayList<Block> convLog) {
    	System.out.println("Updating window...");
    	if (convLog.size() > 1) {
        	String pattern = "([0-9]){2}:([0-9]){2}:([0-9]){2} [A|P]{1}[M]{1}$";
        	Pattern pat = Pattern.compile(pattern);

        	textArea.append(convLog.get(0).data.getMessage() + "\n");
	        for (int i = 1; i < convLog.size(); i++) {
	        	Matcher match = pat.matcher(convLog.get(i).getTimeStamp());
	        	if (match.find()) {
	        		String out = convLog.get(i).data.getUserID() +  " [" + match.group() + "]: " + convLog.get(i).data.getMessage();
	        		textArea.append(out + "\n");
	        	}
	        }
	    	System.out.println("Window updated...");
        }
        else textArea.append(convLog.get(0).data.getMessage() + "\n");
    }
    //USER HITS ENTER
    public ArrayList<Block> logMessage(String text, ArrayList<Block> log) throws IOException{
        // If text is empty return
        if(text.trim().isEmpty()) return null;

        String timeStamp = new SimpleDateFormat("hh:mm:ss a").format(new Date());
        String out = uid +  " [" + timeStamp + "]: " + text + "\n";
        textArea.append(out);
        
        log.add(new Block(new Data(uid,text), log.get(convLog.size()-1).getCurrentHash(), DataEncryption.hashSHA256(log.get(convLog.size()-1).getPreviousHash() + log.get(convLog.size()-1).data + log.get(convLog.size()-1).getTimeStamp())));
        
        TransferData.writeToFile(log, convName);

        first = TransferData.sendFile(convName, log, first, serverIP, serverPort);
        
        return log;
    }
}