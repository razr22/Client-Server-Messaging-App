/*
 * @author: Zain Quraishi
 * @date: 2018-06-29
 * @filename: ChatMenuUI.java
 * @description: Main menu screen appears when a successful connection has been made with the application server.
*/

package Client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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

public class ChatMenuUI {

	private static String serverIP;
	private static int serverPort;
	
	private static JFrame frame = new JFrame("WutsGud");
	
	private JButton convo = createNewConvo();
	private JButton existing = openExisting();
	private JButton exitApp = createQuit();

	public ChatMenuUI(String sIP, int sPort) {
		serverIP = sIP;
		serverPort = sPort;
		
		frame.setSize(250, 110);
		existing.setSize(20, 5);
		exitApp.setSize(20, 5);
	
		frame.add(convo, BorderLayout.NORTH);
		frame.add(existing, BorderLayout.CENTER);
		frame.add(exitApp, BorderLayout.SOUTH);
		
		frame.setResizable(false);
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
							Path newPath = Paths.get("local/" + logFile);

				    		if (!Files.exists(newPath)){
//				    			try {
//									Files.createDirectories(newPath.getParent());
//									Files.createFile(newPath);
//								}
//								catch(FileAlreadyExistsException e4) {} catch (IOException e1) {}
				    			
						    	String res = convName.getText() + username.getText();
						    	
						    	while (res.length() < 16) {res += "0";}
						    	if (res.length() > 16) res = res.substring(0, 15);
						    	String pkey = "";
					    		try {
					    			pkey = DataEncryption.hashSHA256(res);
		
									JPanel myPanel2 = new JPanel();
									myPanel2.setLayout(new GridLayout(2,1));
									
									JLabel alert = new JLabel("THIS IS THE UNIQUE KEY FOR YOUR CONVERSATION! STORE THIS KEY IN A SECURE LOCATION!");
									JTextArea key = new JTextArea(pkey);
									key.setEditable(false);
									key.requestFocus();
									key.selectAll();
									
									
									myPanel2.add(alert, BorderLayout.NORTH);
									myPanel2.add(key, BorderLayout.SOUTH);
									
									JOptionPane.showMessageDialog(null, myPanel2, "PRIVATE KEY", JOptionPane.OK_OPTION);
								} catch (Exception e2) {
									e2.printStackTrace();
								}
						    		
						    	if (!(convName.getText() == null) && !convName.getText().isEmpty() && !logFile.exists()) {	    	
							    	ArrayList<Block> newChain = new ArrayList<Block>();
							    	newChain.add(new Block(new Data(username.getText(),"Start of Message History... '" + convName.getText() + "'"), "0", pkey));
				
								    //logFile.createNewFile();
									TransferData.writeToFile(newChain, convName.getText()); 
								   
									new ChatWindowUI(serverIP, serverPort, username.getText(), convName.getText(), newChain);
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

		ActionListener listener = new MyListener();
		newconvo.addActionListener(listener);
		
	    return newconvo;
	}
	
public static JButton openExisting() {
	frame.setVisible(false);
	JButton existing = new JButton("Join Existing Conversation");
	class MyListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	    	JTextField convName = new JTextField(10);
		    JTextField admin = new JTextField(10);
		    JTextField user = new JTextField(10);
		    JTextArea inKey = new JTextArea();
		    JPanel myPanel = new JPanel();
		    myPanel.setLayout(new GridLayout(4,1));
		    myPanel.add(new JLabel("Conversation ID:"), BorderLayout.NORTH);
		    myPanel.add(convName);
		    myPanel.add(new JLabel("Your Username:"), BorderLayout.CENTER);
		    myPanel.add(user);
		    myPanel.add(new JLabel("Admin Username:"), BorderLayout.CENTER);
		    myPanel.add(admin);
		    myPanel.add(new JLabel("Access Key:"),BorderLayout.SOUTH);
		    myPanel.add(inKey);

		    int result = JOptionPane.showConfirmDialog(null, myPanel, "Open Existing Conversation", JOptionPane.OK_CANCEL_OPTION);
	    	if (result == JOptionPane.OK_OPTION) {

	    		String res = Block.processHash(admin.getText(), convName.getText());
	    		
				try {
					// if entered key is result of info provided
					if (inKey.getText().equals(res)) {
						//check if log exists locally 
					    File f = new File(convName.getText() + ".json");
						Path newPath = Paths.get("local/" + f);

						try {
							Files.createDirectories(newPath.getParent());
							Files.createFile(newPath);
						}
						catch(FileAlreadyExistsException e4) {}	
						
						if (Files.exists(newPath)) {
							BufferedReader bufferedReader = new BufferedReader(new FileReader(newPath.toString()));
							System.out.println(newPath.toString());
					        Gson gson = new Gson();
					        Type listType = new TypeToken<ArrayList<Block>>() {}.getType();
					        ArrayList<Block> convHistory = gson.fromJson(bufferedReader, listType);
	
	
					        if (!user.getText().isEmpty() || !user.getText().equals(null)) {
					        	new ChatWindowUI(serverIP, serverPort, user.getText(), convName.getText(), convHistory);
					        	frame.dispose();
					        }
					        else
					    		JOptionPane.showMessageDialog(null, "Invalid Username!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
						}
						//request server to check for log file
						else {
							System.out.println("log dne locally, checking server...");
							int req = TransferData.serverRequest(2, convName.getText(), serverIP, serverPort);
							if (req != 1)
								JOptionPane.showMessageDialog(null, "Conversation does not exist!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else JOptionPane.showMessageDialog(null, "Access Key Invalid...", "ERROR!", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	    	}
	    }
	}

	ActionListener listener2 = new MyListener() ;
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
		ActionListener listener3 = new MyListener() ;
		quit.addActionListener(listener3);
		
	    return quit;
	}
}
