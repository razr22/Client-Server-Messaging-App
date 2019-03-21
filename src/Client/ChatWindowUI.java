/*
 * @authors: Zain Quraishi
 * @date: 2018-06-29
 * @filename: ChatWindowUI.java
 * @description: Main client-side chat window. Processes user input, updates UI, sends and receives information to server.
*/

package Client;

import javax.swing.*;

import HelperClasses.Block;
import HelperClasses.Data;
import HelperClasses.TransferData;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChatWindowUI extends Thread {
JPanel panel;
JPanel panel1;
JTextArea textField;
JTextArea textArea;
JFrame frame;
JButton exit;
boolean typing;
private String timeStamp;
private String convName;
private ArrayList<Block> convLog;
private String uid;
private String serverIP; 
private int serverPort;

//protected static Socket windowsocket; 
protected int first = 0;

    public ChatWindowUI(String sIP, int sPort, String UID, String convName, ArrayList<Block> convLog){
    	serverIP = sIP;
    	serverPort = sPort;
        uid = UID;	
    	this.convName = convName;
        this.convLog = convLog;
        generateChat(this.convName, this.convLog);
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
					logMessage(textField.getText());
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
//				try {
//					windowsocket.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
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
      
       // button.setMaximumSize(new Dimension(40,20));
        panel.add(exit, BorderLayout.SOUTH);
        frame.add(panel,BorderLayout.SOUTH);
         
        //CREATE MESSAGE VIEWING SPACE
        textArea=new JTextArea();
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setForeground(Color.BLUE);
        //textArea.setPreferredSize(new Dimension(350,400));
        textArea.setEditable(false);
       
        // Set some margin, for the text
        textArea.setMargin(new Insets(7,7,7,7));
        
        //POPULATE MESSAGES IN WINDOW
        //readHistory(textArea, convLog);
        if (convLog.size() > 1) {
        	textArea.append(convLog.get(0).data.getMessage() + "\n");
	        for (int i = 1; i < convLog.size(); i++) {
	        	String out = convLog.get(i).data.getUserID() +  " [" + convLog.get(i).data.getTimeStamp() + "]: " + convLog.get(i).data.getMessage();
	        	textArea.append(out + "\n");
	        }
        }
        else textArea.append(convLog.get(0).data.getMessage() + "\n");
        JScrollPane scroll = new JScrollPane(textArea);
        frame.add(scroll);
       
        //FRAME INFO
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        //pack();
        frame.setSize(350,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    //USER HITS ENTER
    private void logMessage(String text) throws IOException{
        // If text is empty return
        if(text.trim().isEmpty()) return;

        timeStamp = new SimpleDateFormat("hh:mm:ss a").format(new Date());
        String out = uid +  " [" + timeStamp + "]: " + text;
        textArea.append(out + "\n");
        
        convLog.add(new Block(new Data(uid,text,timeStamp), convLog.get(convLog.size()-1).getCurrentHash()));
        
        TransferData.writeToFile(convLog, convName);

        first = TransferData.sendFile(convName, convLog, first, serverIP, serverPort);
    }
}