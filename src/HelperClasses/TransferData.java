/*
 * @authors: Zain Quraishi
 * @date: 2018-06-29
 * @filename: TransferData.java
 * @description: Helper class which provides data transfer protocols for data logs.
*/
package HelperClasses;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TransferData {
	//Client-side
	public static int serverRequest(int reqID, String cname, int chainSize, String ip, int port) throws UnknownHostException, IOException {
		Socket reqSocket = new Socket(ip, port);
		
		DataOutputStream dos = new DataOutputStream(reqSocket.getOutputStream());
		dos.writeInt(reqID);
		dos.writeUTF(cname);
		dos.writeInt(chainSize);
		dos.flush();
		
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(reqSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (dis.available() == 0) {}//wait
		int res = dis.readInt();
		if (res == 1) receiveFile2(reqSocket, cname);
		else if (res == 2) {System.out.println("Send file to server");}
		dos.close();
		reqSocket.close();
		return res;
	}
	
	//Server-side
	public static void checkForUpdatedLog(Socket reqSocket) throws IOException, ParseException {
		DataInputStream dis = new DataInputStream(reqSocket.getInputStream());
		
		while (dis.available() == 0) {}
		
		String fileName = dis.readUTF();
		int clientSize = dis.readInt();

		File sLog = new File("server/" + fileName + ".json");

		if (sLog.exists()) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(sLog));
	        Gson gson = new Gson();
	        Type listType = new TypeToken<ArrayList<Block>>() {}.getType();
	        ArrayList<Block> convHistory = gson.fromJson(bufferedReader, listType);
	        
	        int serverSize = convHistory.size();
	        if (serverSize > clientSize) {writeOut(reqSocket, sLog, 1);}	//send server log to client
	        else { System.out.println("Client log up-to-date..."); sendResponse(reqSocket, 0); }	//retrieve client log      
		}
		
		dis.close();
	}
	
	//Server-side
	public static void sendResponse(Socket reqSocket, int response) throws IOException {
		DataOutputStream dos = new DataOutputStream(reqSocket.getOutputStream());
		dos.writeInt(response);
		dos.flush();
		dos.close();
	}
	
	//Server-side
	public static void retrieveServerLog(Socket reqSocket) throws IOException {
		//retrieve name bytes
		int exists = 0;
		DataInputStream dis = new DataInputStream(reqSocket.getInputStream());
		
		while (dis.available() == 0) {}
		
		String fileName = dis.readUTF();
		File sLog = new File("server/" + fileName + ".json");
		
		if (sLog.exists()) exists = 1;

		if (exists == 1) {
			writeOut(reqSocket, sLog, exists);
		}
		
		dis.close();
	}
	
	private static void writeOut(Socket socket, File log, int response) throws IOException {
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		BufferedReader bufferedReader = new BufferedReader(new FileReader(log));
		String str = null;
		String fmt = null;
		System.out.println("Transmitting file... " + log.getName());
		dos.writeInt(response);
		while ((str = bufferedReader.readLine()) != null) {
			fmt = str + "\n";
			dos.write(fmt.getBytes());
			dos.flush();
		}
		bufferedReader.close();
		dos.write("e\n".getBytes());
		dos.flush();
		dos.close();
	}
	
	public static void receiveFile2(Socket socket, String cname) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		File sLog = new File(cname + ".json");
		Path newPath = Paths.get("local/" + sLog);

		try {
			Files.createDirectories(newPath.getParent());
			Files.createFile(newPath);
		}
		catch(FileAlreadyExistsException e) {}	

		String str = null;
		String tmp = null;
		
		str = null;
		ArrayList<String> read = new ArrayList<String>();
		while (!(str = br.readLine()).equals("e")) {	
			if (str.equals("]")) tmp = str;
			else tmp = str + "\n";
			read.add(tmp);
		}

		int i = 0;
		FileOutputStream fos = new FileOutputStream(newPath.toString(), false);
		while (i < read.size()) {
			fos.write(read.get(i).getBytes());
			fos.flush();
			i++;			
		}
		fos.close();
		socket.close();
	}
	
	//sends log data to destination.
	public static int sendFile(String convName, ArrayList<Block> convLog, int first, String ip, int port) throws IOException {
		Socket temp = new Socket(ip, port);
		DataOutputStream dos = new DataOutputStream(temp.getOutputStream());
		String name = convName + ".json";
		File local = new File("local/" + name);
		
		if (first == 0) {
			dos.writeInt(1);
			dos.flush();
			
			String test = local.getName() + "\n";
			dos.write(test.getBytes());
			dos.flush();
			
			first = 1;
		}
		if (local.exists()) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(local));
			String str = null;
			String fmt = null;
			System.out.println("Transmitting file... " + local.getName());
			while ((str = bufferedReader.readLine()) != null) {
				fmt = str + "\n";
				dos.write(fmt.getBytes());
				dos.flush();
			}
			bufferedReader.close();
			first = 0;
		}
		else System.out.println("Error! local file DNE.");
		dos.write("e\n".getBytes());
		dos.flush();
		dos.close();
		temp.close();
		return first;        
    }
	
	//reads incoming file data into local log.
	public static void receiveFile(Socket socket) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String fname =  br.readLine();
		System.out.println("receiving : " + fname);
		
		File sLog = new File(fname);
		Path newPath = Paths.get("server/" + sLog);

		try {
			Files.createDirectories(newPath.getParent());
			Files.createFile(newPath);
		}
		catch(FileAlreadyExistsException e) {
			PrintWriter pw = new PrintWriter(newPath.toString());
			pw.close();
		}	

		String str = null;
		String tmp = null;
		
		str = null;
		ArrayList<String> read = new ArrayList<String>();
		while (!(str = br.readLine()).equals("e")) {	
			if (str.equals("]")) tmp = str;
			else tmp = str + "\n";
			
			read.add(tmp);
		}

		int i = 0;
		FileOutputStream fos = new FileOutputStream(newPath.toString(), true);
		while (i < read.size()) {
			fos.write(read.get(i).getBytes());
			fos.flush();
			i++;			
		}
		fos.close();
	}

	//writes log to file.
	public static void writeToFile(ArrayList<Block> convLog, String convName) {
		String convJson = new GsonBuilder().setPrettyPrinting().create().toJson(convLog);
		try {						
			File f = new File(convName + ".json");
			Path newPath = Paths.get("local/" + f);

			try {
				Files.createDirectories(newPath.getParent());
				Files.createFile(newPath);
			}
			catch(FileAlreadyExistsException e4) {}
			
			if (Files.exists(newPath)) {
				FileOutputStream outData = new FileOutputStream(newPath.toString(), false); 
				
				outData.write(convJson.getBytes());
				outData.flush();
				outData.close();
			}
		}
		catch(Exception e) {e.printStackTrace();}
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
}
