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

public class TransferData {
	private static boolean flag = false;
		
	public static int sendFile(String convName, ArrayList<Block> convLog, int first, String ip, int port) throws IOException {
		Socket temp = new Socket(ip, port);
		DataOutputStream dos = new DataOutputStream(temp.getOutputStream());
		String name = convName + ".json";
		File local = new File(name);
		
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
		
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		if (br.ready()) {System.out.println("ready to receive data...");}
		else System.out.println("server unable to read data...");
		
		String fname =  br.readLine();
		System.out.println("receiving : " + fname);
		
		File sLog = new File(fname);
		Path newPath = Paths.get("Reference/" + sLog);

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

	public static void writeToFile(ArrayList<Block> convLog, String convName) {
		String convJson = new GsonBuilder().setPrettyPrinting().create().toJson(convLog);
		try {						
			File f = new File(convName + ".json");
			
			if (f.exists()) {
				FileOutputStream outData = new FileOutputStream(f, false); 
				
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
