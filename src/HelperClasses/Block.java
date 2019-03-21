package HelperClasses;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Block {

	public String previousHash;
	public String currentHash;
	private String timeStamp;
	public Data data;
	
	public Block(Data data, String previousHash) {
		this.timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		this.previousHash = previousHash;
		this.currentHash = getCurrentHash();
		this.data = data;
	}

	public String getPreviousHash() {
		return this.previousHash;
	}
		
	public String getTimeStamp() {
		return this.timeStamp;
	}
	
	public String getCurrentHash() {
		return DataEncryption.hashSHA256(this.previousHash + this.data + this.timeStamp);
	}
}
