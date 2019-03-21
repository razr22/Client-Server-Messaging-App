/*
 * @authors: Zain Quraishi
 * @date: 2018-06-29
 * @filename: ClientApplication.java
 * @description: Client-side application for performing app functions through accessing main server.
*/

package Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientApplication {
  
	public static void connect(String ip, int port) {
		@SuppressWarnings("unused")
		Socket socket = null;
		try {
			socket = new Socket(ip, port);
			System.out.println("Connected to Server at : " + ip + ":" + port + " ...");
			new ChatMenuUI(socket).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void getConnectionInfo() {
		//JTextField ipField = new JTextField(10);
	    String[] ipList = {"18.222.174.178", "localhost"};
		JComboBox<?> ipField = new JComboBox<Object>(ipList);
		JTextField portField = new JTextField(5);
	    
	    //for debugging
	    portField.setText("5001");
	    
	    JPanel myPanel = new JPanel();
	    myPanel.add(new JLabel("IP Address:"));
	    myPanel.add(ipField);
	    myPanel.add(Box.createHorizontalStrut(15)); 
	    myPanel.add(new JLabel("Port #:"));
	    myPanel.add(portField);

	    int result = JOptionPane.showConfirmDialog(null, myPanel, "Connection Information", JOptionPane.OK_CANCEL_OPTION);
	    
	    if (result == JOptionPane.OK_OPTION) {
		    String ip = ipList[ipField.getSelectedIndex()];
		    String port = portField.getText();
		      	
			if (ip.matches("^localhost") || ip.matches("^[1-9]{1,3}.[1-9]{1,3}.[1-9]{1,3}.[1-9]{1,3}")) {
				if (port.matches("^[0-9]{4}$")) {
					connect(ip, Integer.parseInt(port));
				}
				else
		    		JOptionPane.showMessageDialog(null, "Invalid Port Number!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
			}
			else 
	    		JOptionPane.showMessageDialog(null, "Invalid IP Address!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
	    }
	    else System.exit(0);
	}
	
	public static void main(String[] args) {
		getConnectionInfo();		
	}
}
