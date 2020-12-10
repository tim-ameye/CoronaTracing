package mixingProxy;


import java.time.Instant;

public class Capsule{
	private Instant currentTimeInterval;
	private byte[] usertoken;
	private String qrToken; //deze moet dan na toekomen geUnhashed worden
	
	public Capsule() {
		this.currentTimeInterval = null;
		this.usertoken = null;
		this.qrToken = null;
	}

	public Capsule(Instant day, byte[] token, String string) {
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
	
	
}
