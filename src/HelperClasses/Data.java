/*
 * @author: Zain Quraishi
 * @date: 2019-03-21
 * @filename: DataEncryption.java
*/

package HelperClasses;

public class Data {
	private String uid;
	private String message;
	private String timeStamp;
	
	public Data(String uid, String message, String timeStamp) {
		this.uid = uid;
		this.message = message;
		this.timeStamp = timeStamp;
	}
	
	public String getUserID() {
		return uid;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
}
