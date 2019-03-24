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
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class TransferData {
	private static boolean flag = false;
	
	public static int serverRequest(int reqID, String cname, String ip, int port) throws UnknownHostException, IOException {
		Socket reqSocket = new Socket(ip, port);
		
		DataOutputStream dos = new DataOutputStream(reqSocket.getOutputStream());
		System.out.println("writing request to server... " + reqID);
		dos.writeInt(reqID);
		System.out.println("writing convName to server... " + cname);
		dos.writeUTF(cname);
		dos.flush();
		
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(reqSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("waiting for response... ");
		while (dis.available() == 0) {}//wait
		int res = dis.readInt();
		System.out.println("response ID... " + res);
		if (res == 1) receiveFile2(reqSocket, cname);
		else System.out.println("Conversation DNE");
		
		dos.close();
		reqSocket.close();
		return res;
	}
	
	public static void parseLogs(Socket reqSocket) throws IOException {
		//retrieve name bytes
		int exists = 0;
		DataInputStream dis = new DataInputStream(reqSocket.getInputStream());
		
		while (dis.available() == 0) {}
		
		String fileName = dis.readUTF();
		
		File sLog = new File("reference/" + fileName + ".json");
		
		if (sLog.exists()) exists = 1;
		else System.out.println(sLog.toString() + " does not exist.");
		DataOutputStream dos = new DataOutputStream(reqSocket.getOutputStream());
		
		if (exists == 1) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(sLog));
			String str = null;
			String fmt = null;
			System.out.println("Transmitting file... " + sLog.getName());
			dos.writeInt(exists);
			while ((str = bufferedReader.readLine()) != null) {
				fmt = str + "\n";
				dos.write(fmt.getBytes());
				dos.flush();
			}
			bufferedReader.close();
			dos.write("e\n".getBytes());
			dos.flush();
		}
		dos.close();
		dis.close();
	}
	
	public static void receiveFile2(Socket socket, String cname) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		if (br.ready()) {System.out.println("ready to receive data...");}
		else System.out.println("server unable to read data...");
		
		File sLog = new File(cname + ".json");
		Path newPath = Paths.get("local/" + sLog);

		try {
			Files.createDirectories(newPath.getParent());
			Files.createFile(newPath);
		}
		catch(FileAlreadyExistsException e) {
			e.printStackTrace();
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
		
		if (br.ready()) {System.out.println("ready to receive data...");}
		else System.out.println("server unable to read data...");
		
		String fname =  br.readLine();
		System.out.println("receiving : " + fname);
		
		File sLog = new File(fname);
		Path newPath = Paths.get("reference/" + sLog);

		try {
			Files.createDirectories(newPath.getParent());
			Files.createFile(newPath);
			System.out.println("Creating file...");
		}
		catch(FileAlreadyExistsException e) {
			System.out.println("file exists...");
			flag = true;

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
		if (flag) { 
			System.out.println("Checking file contents...");
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
