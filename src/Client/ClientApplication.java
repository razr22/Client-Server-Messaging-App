/*
 * @authors: Zain Quraishi
 * @date: 2018-06-29
 * @filename: ClientApplication.java
 * @description: Client-side application for performing app functions through accessing main server.
*/

package Client;

import java.io.IOException;
import java.net.Socket;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import HelperClasses.TransferData;

public class ClientApplication {
  
	private static void getConnectionInfo() throws IOException {
	    String[] ipList = {"localhost", "18.218.170.76"};
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
		    Socket servSock = TransferData.connect(ip, Integer.parseInt(port));
			if (ip.matches("^localhost") || ip.matches("^[1-9]{1,3}.[1-9]{1,3}.[1-9]{1,3}.[1-9]{1,3}")) {
				if (port.matches("^[0-9]{4}$")) {
					if (servSock.isConnected()) {
						System.out.println("Server is live at: " + ip + ":" + port);
						servSock.close();
						new ChatMenuUI(ip, Integer.parseInt(port));
					}
				}
				else
		    		JOptionPane.showMessageDialog(null, "Invalid Port Number!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
			}
			else 
	    		JOptionPane.showMessageDialog(null, "Invalid IP Address!", "ERROR", JOptionPane.INFORMATION_MESSAGE);
	    }
	    else System.exit(0);
	}
	
	public static void main(String[] args) throws IOException {
		getConnectionInfo();		
	}
}
