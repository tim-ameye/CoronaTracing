package mixingProxy;


import java.io.Serializable;
import java.time.Instant;

public class Capsule implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6141754611786842771L;
	private Instant currentTimeInterval;
	private String usertoken;
	private String qrToken; //deze moet dan na toekomen geUnhashed worden
	
	public Capsule() {
		this.currentTimeInterval = null;
		this.usertoken = null;
		this.qrToken = null;
	}

	public Capsule(Instant day, String token, String string) {
		this.currentTimeInterval = day;
		this.usertoken = token;
		this.qrToken = string;
		
	}

	
	public Instant getCurrentTimeInterval() {
		return currentTimeInterval;
	}

	public void setCurrentTimeInterval(Instant currentTimeInterval) {
		this.currentTimeInterval = currentTimeInterval;
	}

	public String getUsertoken() {
		return usertoken;
	}
	public void setUsertoken(String usertoken) {
		this.usertoken = usertoken;
	}
	public String getQrToken() {
		return qrToken;
	}
	public void setQrToken(String qrToken) {
		this.qrToken = qrToken;
	}
	
}
