package HelperClasses;
import java.security.MessageDigest;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

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
	
	public static String testSha3(String dat) throws Exception {
	    SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
	    byte[] digest = digestSHA3.digest(dat.getBytes());

		return Hex.toHexString(digest).toString();
	}
	
//	public static void main(String[] args) throws Exception {
//		String in = "c3239a5e76e591d9b9fbb63492b1cc4e3269da9acdbd6f1d5ed32e8307566ac0ab9150b77c6fe3f4fe0238576d6a60d1df0992db09a0808ee196afaef0db36c6\r\n";
//		System.out.println(testSha3(in));
//	}
}
