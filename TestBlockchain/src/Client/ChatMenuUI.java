/*
 * @author: Zain Quraishi
 * @date: 2018-06-29
 * @filename: ChatMenuUI.java
 * @description: Main menu screen appears when a successfull connection has been made with the application server.
*/

package Client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import HelperClasses.Block;
import HelperClasses.Data;
import HelperClasses.DataEncryption;
import HelperClasses.TransferData;

public class ChatMenuUI extends Thread{
	protected static Socket chatsocket;
	public static boolean flag = false;
	static JFrame frame = new JFrame("WutsGud");
	JButton convo = createNewConvo();
	JButton existing = openExisting();
	JButton exitApp = createQuit();

	public ChatMenuUI(Socket clientSocket) {
		ChatMenuUI.chatsocket = clientSocket;

		frame.setSize(250, 110);
		existing.setSize(20, 5);
		exitApp.setSize(20, 5);
	
		frame.add(convo, BorderLayout.NORTH);
		frame.add(existing, BorderLayout.CENTER);
		frame.add(exitApp, BorderLayout.SOUTH);
		
		frame.setResizable(false);
		//frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.toFront();
	}
	
    public static JButton createNewConvo() {
		JButton newconvo = new JButton("New Conversation");
		class MyListener implements ActionListener
		{
		    public void actionPerformed(ActionEvent e){
				JTextField convName = new JTextField(10);
			    JTextField username = new JTextField(10);
			    
			    JPanel myPanel = new JPanel();
			    myPanel.add(new JLabel("Conversation Name:"));
			    myPanel.add(convName);
			    myPanel.add(Box.createHorizontalStrut(15)); 
			    myPanel.add(new JLabel("Enter Username:"));
			    myPanel.add(username);

			    int result = JOptionPane.showConfirmDialog(null, myPanel, "Create Conversation", JOptionPane.OK_CANCEL_OPTION);
		  
			    if (result == JOptionPane.OK_OPTION) {
			    	if (!(convName.getText() == null) && !convName.getText().isEmpty()){
			    		if (!(username.getText() == null) && !username.getText().isEmpty()){
					    	
					    	File logFile = new File(convName.getText() + ".json");
				    		if (!logFile.exists()){
		
			    			final String timeStampKey = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
					    	String res = convName.getText() + username.getText();
					    	
					    	while (res.length() < 16) {res += "0";}
					    	if (res.length() > 16) res = res.substring(0, 15);
					  
					    		try {
					    			String pkey = DataEncryption.testSha3(res);
		
									JPanel myPanel2 = new JPanel();
									myPanel2.setLayout(new GridLayout(2,1));
									
									JLabel alert = new JLabel("THIS IS THE UNIQUE KEY FOR YOUR CONVERSATION ! DO NOT LOSE THIS KEY!");
									JTextArea key = new JTextArea(pkey);
									key.setEditable(false);
									
									myPanel2.add(alert, BorderLayout.NORTH);
									myPanel2.add(key, BorderLayout.SOUTH);
									
									JOptionPane.showMessageDialog(null, myPanel2, "PRIVATE KEY", JOptionPane.OK_OPTION);
								} catch (Exception e2) {
									e2.printStackTrace();
								}
					    		
						    	if (!(convName.getText() == null) && !convName.getText().isEmpty() && !logFile.exists()) {	    	
							    	ArrayList<Block> newChain = new ArrayList<Block>();
							    	newChain.add(new Block(new Data(username.getText(), "Start of Message History... '" + convName.getText() +"'", timeStampKey), "0"));
				
								    try {
										logFile.createNewFile();
										TransferData.writeToFile(newChain, convName.getText());
									} catch (IOException e1) {
										e1.printStackTrace();
									} 
								   
									new ChatWindowUI(chatsocket, username.getText(), convName.getText(), newChain).start();
									frame.dispose();
						    	}
				    		}
					    	else JOptionPane.showMessageDialog(null, "Conversation Name Taken!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
			    		}
				    	else JOptionPane.showMessageDialog(null, "Invalid Username!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
			    	}
			    	else JOptionPane.showMessageDialog(null, "Invalid Conversation Name!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
			    }
		    }
		}
		//make listener
		ActionListener listener = new MyListener();
		//add listener
		newconvo.addActionListener(listener);
	    return newconvo;
	}
	
	public static JButton openExisting() {
		JButton existing = new JButton("Open Existing Conversation");
		class MyListener implements ActionListener
		{
		    public void actionPerformed(ActionEvent e)
		    {
		    	JTextField convName = new JTextField(10);
			    JTextField userB = new JTextField(10);
			    JTextArea inKey = new JTextArea();
			    JPanel myPanel = new JPanel();
			    myPanel.setLayout(new GridLayout(3,1));
			    myPanel.add(new JLabel("Enter Conversation Name:"), BorderLayout.NORTH);
			    myPanel.add(convName);
			    myPanel.add(new JLabel("Enter Admin ID:"), BorderLayout.CENTER);
			    myPanel.add(userB);
			    myPanel.add(new JLabel("Enter Access Key:"),BorderLayout.SOUTH);
			    myPanel.add(inKey);

			    int result = JOptionPane.showConfirmDialog(null, myPanel, "Open Existing Conversation", JOptionPane.OK_CANCEL_OPTION);
		    	if (result == JOptionPane.OK_OPTION) {
		    		String res = null;
		    	try {
		    		res = convName.getText() + userB.getText();
		    		while (res.length() < 16) {res += "0";}
		    		if (res.length() > 16) res = res.substring(0, 15);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
		    	
			    File f = new File(convName.getText() + ".json");
		    	
					try {
						if (inKey.getText().equals(DataEncryption.testSha3(res))) {
							if (f.exists()) {
						        BufferedReader bufferedReader = new BufferedReader(new FileReader(f));

						        Gson gson = new Gson();
						        Type listType = new TypeToken<ArrayList<Block>>() {}.getType();
						        ArrayList<Block> convHistory = gson.fromJson(bufferedReader, listType);
						        String yourUID = null;
						       int i = 0;
						       int flag = 0;
						        while (i < convHistory.size() && flag == 0) {
						        	//if user found in conversation
						        	if (!convHistory.get(i).data.getUserID().equals(userB)) {
						        		yourUID = convHistory.get(i).data.getUserID();
						        		flag = 1;
						        	}
						        	i++;
						        }
						        if (flag == 0) {
						        	yourUID = JOptionPane.showInputDialog(null, "Select Username: ");
						        }
						        if (!yourUID.isEmpty() || !yourUID.equals(null)) {
						        	frame.dispose();
						        	new ChatWindowUI(chatsocket,yourUID, convName.getText(), convHistory);
						        }
						        else
						    		JOptionPane.showMessageDialog(null, "Invalid Username!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
		    	}
		    }
		}
		//make listener
		ActionListener listener2 = new MyListener() ;
		//add listener
		existing.addActionListener(listener2);
	    return existing;
	}
	public static JButton createQuit() {
		JButton quit = new JButton("Quit");
		class MyListener implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
				frame.dispose();
		    }
		}
		//make listener
		ActionListener listener3 = new MyListener() ;
		//add listener
		quit.addActionListener(listener3);
	    return quit;
	}
}
