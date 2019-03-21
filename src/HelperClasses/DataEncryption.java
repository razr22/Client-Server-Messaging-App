/*
 * @author: Zain Quraishi
 * @date: 2019-03-21
 * @filename: DataEncryption.java
*/
package HelperClasses;

import java.security.MessageDigest;

public class DataEncryption {
	public static String hashSHA256(String cumulativeData){
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(cumulativeData.getBytes("UTF-8"));
			
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}		
	}
}
