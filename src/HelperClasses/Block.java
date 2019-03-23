/*
 * @author: Zain Quraishi
 * @date: 2019-03-21
 * @filename: DataEncryption.java
*/

package HelperClasses;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Block {

	private String previousHash;
	private String currentHash;
	private String timeStamp;
	public Data data;
	
	public Block(Data data, String previousHash, String currentHash) {
		this.timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		this.previousHash = previousHash;
		this.currentHash = currentHash;
		this.data = data;
	}

	public String getTimeStamp() {
		return timeStamp;
	}
	
	public String getCurrentHash() {
		return currentHash;
	}
	
	public String getPreviousHash() {
		return previousHash;
	}
	
	
	public static String processHash(String uid, String cname) {
		String res = null;
    	try {
    		res = cname + uid;
    		while (res.length() < 16) {res += "0";}
    		if (res.length() > 16) res = res.substring(0, 15);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		return DataEncryption.hashSHA256(res);
	}
	
	public String messageHash() {
		return DataEncryption.hashSHA256(previousHash + data + timeStamp);
	}
}
