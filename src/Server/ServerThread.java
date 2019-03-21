/*
 * @authors: Zain Quraishi
 * @date: 2018-06-29 
 * @filename: ServerThread.java	
 * @description: Implementation of Server.java functionality allows for concurrent user connections 
 * to server through the use of multi-threading.
*/

package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import HelperClasses.TransferData;

public class ServerThread extends Thread {
	protected static Socket serversocket;
	//protected DataInputStream dis;
	public ServerThread(Socket socket) {
		ServerThread.serversocket = socket;
	}
		
	public void run() {
		System.out.println("Client at : " + serversocket.getRemoteSocketAddress().toString() + " connected...\n");
    	System.out.println("Waiting for incoming data...");
    	
    	DataInputStream dis = null;
    	
    	try {
			dis = new DataInputStream(serversocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	while (true) {
	    	try {
	    		if (dis.available() > 0) {
	    			//On user input receive incoming log file
	    			if (dis.readInt() == 1) {
	    				TransferData.receiveFile(serversocket);
	    			}
	    		}
			} catch (IOException e) {
				
			}
}
	}
}