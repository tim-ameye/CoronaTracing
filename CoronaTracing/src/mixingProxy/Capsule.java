package mixingProxy;


import java.io.Serializable;
import java.time.Instant;

public class Capsule implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6141754611786842771L;
	private String currentTimeInterval;
	
	public Capsule() {
		currentTimeInterval = "dsqf";
	}
	
	/*private byte[] usertoken;
	private String qrToken; //deze moet dan na toekomen geUnhashed worden
	
	public Capsule() {
		this.currentTimeInterval = null;
		this.usertoken = null;
		this.qrToken = null;
	}

	public Capsule(Instant day, byte[] token, String string) {
		this.currentTimeInterval = day.toString();
		this.usertoken = token;
		this.qrToken = string;
		
	}

	
	public byte[] getUsertoken() {
		return usertoken;
	}
	public void setUsertoken(byte[] usertoken) {
		this.usertoken = usertoken;
	}
	public String getQrToken() {
		return qrToken;
	}
	public void setQrToken(String qrToken) {
		this.qrToken = qrToken;
	}
	*/
}
