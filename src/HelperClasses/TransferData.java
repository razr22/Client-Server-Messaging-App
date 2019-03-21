/*
 * @authors: Zain Quraishi
 * @date: 2018-06-29
 * @filename: TransferData.java
 * @description: Helper class which provides data transfer protocols for data logs.
*/

package HelperClasses;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.google.gson.GsonBuilder;

import Client.ChatMenuUI;

public class TransferData {
	protected static Socket tsocket;
	public static boolean flag = false;
	
	public TransferData(Socket socket) {
		TransferData.tsocket = socket;
	}
		
	public static int sendFile(String convName, ArrayList<Block> convLog, int first, String serverIP, int serverPort) throws IOException {
		Socket temp = new Socket(serverIP, serverPort);
		DataOutputStream dos = new DataOutputStream(temp.getOutputStream());
		String name = convName + ".json";
		File local = new File(name);
		
		if (first == 0) {
			dos.writeInt(1);
			dos.flush();
			
			String test = local.getName() + "\n";
			//System.out.println("Transmitting file... " + test);
			dos.write(test.getBytes());
			dos.flush();
			
			first = 1;
		}
		
		if (local.exists()) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(local));
			String str = null;
			String fmt = null;
//			System.out.println("Transmitting file... " + local.getName());
			while ((str = bufferedReader.readLine()) != null) {
				fmt = str + "\n";
				//System.out.print(fmt);
				dos.write(fmt.getBytes());
				dos.flush();
			}
			bufferedReader.close();
			first = 0;
		}
		
		dos.write("e\n".getBytes());
		dos.flush();
		dos.close();
		temp.close();
		return first;        
    }
	
	public static void receiveFile(Socket socket) throws IOException {
		//receive the file, if it exists then check if the received file has more info than the current server log
		//if the received file is larger, then update server log file, and push file to outputstream.
		System.out.println("------ Attempting to receive data ------");
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String fname = br.readLine();
		
		if (br.ready()) {System.out.println("stream is ready to be read...");}
		else System.out.println("stream is not currently able to be read...");
		
		System.out.println("receiving : " + fname);
		
		File sLog = new File(fname);
		System.out.println("Creating: " + sLog.toString());
		Path newPath = Paths.get("logs/" + sLog);

		try {
			Files.createDirectories(newPath.getParent());
			Files.createFile(newPath);
			System.out.println("Creating log directory...\n");
		}
		catch(FileAlreadyExistsException e) {
			System.out.println("file exists...\n");
			flag = true;

			PrintWriter pw = new PrintWriter(newPath.toString());
			pw.close();
		}	

		String str = null;
		String tmp = null;
		
		str = null;
		ArrayList<String> read = new ArrayList<String>();
		while (!(str = br.readLine()).equals("e")) {	
			///System.out.println("LINE : " + str);
			if (str.equals("]")) tmp = str;
			else tmp = str + "\n";
			
			read.add(tmp);
		}
	//	br.close(); //
		if (flag) { 
			//System.out.println("------ Checking file contents ------\n");
			//compareData(read, newPath.toString());
		}

		int i = 0;
		FileOutputStream fos = new FileOutputStream(newPath.toString(), true);
		while (i < read.size()) {
			//System.out.print("WRITE TO FILE : " + read.get(i));
			fos.write(read.get(i).getBytes());
			fos.flush();
			i++;			
		}
		fos.close();
	}
	
	//may have to check if file contents are identical
	public static void compareData(ArrayList<String> read, String filePath) throws IOException {
		BufferedReader readLog = new BufferedReader(new FileReader(new File(filePath)));

		//store contents in array for comparison
		String logData = null;
		ArrayList<String> serverLog = new ArrayList<String>();
		while ((logData = readLog.readLine()) != null) {
			serverLog.add(logData);
		}
		
		//if server log is larger than client log than client log needs to be updated
		//else if the client log is larger than the server log than the server log needs to be updated and needs to be pushed to the client.
		//create and return updated array list with correct chronologically correct contents.
		
		int clientIndex = 0;

			String client = read.get(clientIndex);
			if (!(logData.equals(client))) System.out.println("LINE " + clientIndex + "Mismatch\n--- CLIENT ---\n" + client + "\n\n--- SERVER ---\n" + logData);
			clientIndex++;
		
		readLog.close();
		
		//
		//if client log larger than current server log
		//boolean larger = (read.size() > logSize) && (read.size() != logSize) ? true : false ;
		//if client log smaller than current server log, (not possible?)
		
		//if (larger) {}
	}

	public static Socket connect(String ip, int port) {
		Socket socket = null;
		try {
			socket = new Socket(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
	}
	
	public static void writeToFile(ArrayList<Block> convLog, String convName) {
		String convJson = new GsonBuilder().setPrettyPrinting().create().toJson(convLog);
		try {						
			File f = new File(convName + ".json");
			
			if (f.exists()) {
				@SuppressWarnings("resource")
				FileOutputStream outData = new FileOutputStream(f, false); 
				
				outData.write(convJson.getBytes());
				outData.flush();
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}
}
