/*
 * @authors: Zain Quraishi
 * @date: 2018-06-29
 * @filename: Server.java
 * @description: Server side micro services which processes incoming client connections and coordinates conversation data.
*/

package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static ServerSocket serversocket;
	private static Socket socket = null;
	
//	public Server(ServerSocket socket) throws IOException {
//		Server.serversocket = socket;
//       
//	}
		
	 public static void main(String args[]) throws IOException {		
		//creating simple web server to listen to incoming messages.
		try {
		//creates socket based on address given.
	        //new Server(new ServerSocket(5001));
			serversocket = new ServerSocket(5001);
			 System.out.println("Server Listening for Connection Requests on Port : " + Server.serversocket.getLocalPort());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	   
	    try {
	    	while (true) {
	    		//run thread class to parse socket for message input 
	    		socket = serversocket.accept();
				new ServerThread(socket).start();
	    	}
	    } catch (IOException e) {
	    	System.out.println("Error! Could not establish connection...");
	    }
	 }
}
