/*
 * @author: Zain Quraishi
 * @date: 2019-03-21
 * @filename: DataEncryption.java
*/

package HelperClasses;

public class Data {
	private String uid;
	private String message;
	
	public Data(String uid, String message) {
		this.uid = uid;
		this.message = message;
	}
	
	public String getUserID() {
		return uid;
	}
	
	public String getMessage() {
		return message;
	}
}
