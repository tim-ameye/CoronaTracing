package mixingProxy;


import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Capsule implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6141754611786842771L;
	private String currentTimeInterval;
	private String userTokenSigned;
	private String userTokenUnsigned;
	private String qrToken; //deze moet dan na toekomen geUnhashed worden
	private String sessionKey;
	
	public Capsule() {
		this.currentTimeInterval = null;
		this.userTokenSigned = null;
		this.qrToken = null;
	}

	public Capsule(Instant day, String signed, String unsigned, String string) {
		this.currentTimeInterval = day.toString();
		this.userTokenSigned = signed;
		this.userTokenUnsigned = unsigned;
		this.qrToken = string;
		
	}

	
	public Instant getCurrentTimeInterval() {
		return Instant.parse(currentTimeInterval);
	}

	public void setCurrentTimeInterval(Instant currentTimeInterval) {
		this.currentTimeInterval = currentTimeInterval.toString();
	}

	public String getUserTokenSigned() {
		return userTokenSigned;
	}
	public void setUserTokenSigned(String usertoken) {
		this.userTokenSigned = usertoken;
	}
	public String getQrToken() {
		return qrToken;
	}
	public void setQrToken(String qrToken) {
		this.qrToken = qrToken;
	}

	public String getUserTokenUnsigned() {
		return userTokenUnsigned;
	}

	public void setUserTokenUnsigned(String userTokenUnsigned) {
		this.userTokenUnsigned = userTokenUnsigned;
	}
	
	public byte[] getSignedBytes() {
		return Base64.getDecoder().decode(userTokenSigned);
	} 
	
	public byte[] getUnsignedBytes() {
		return Base64.getDecoder().decode(userTokenUnsigned);
	}
	
	public Capsule encrypt(SecretKey sessionKey, PublicKey pk) {
		Capsule encrypted = new Capsule();
		try {
			Cipher encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			byte[] time = encryptText.doFinal(currentTimeInterval.toString().getBytes());
			encrypted.currentTimeInterval = Base64.getEncoder().encodeToString(time);
			byte[] hash = encryptText.doFinal(qrToken.getBytes());
			encrypted.qrToken = Base64.getEncoder().encodeToString(hash);
			byte[] signed = encryptText.doFinal(userTokenSigned.getBytes());
			encrypted.userTokenSigned = Base64.getEncoder().encodeToString(signed);
			byte[] unsigned = encryptText.doFinal(userTokenUnsigned.getBytes());
			encrypted.userTokenUnsigned = Base64.getEncoder().encodeToString(unsigned);
			Cipher encryptSession = Cipher.getInstance("RSA");
			encryptSession.init(Cipher.ENCRYPT_MODE, pk);
			byte[] encryptedSessionKey = encryptSession.doFinal(sessionKey.getEncoded());
			encrypted.sessionKey = Base64.getEncoder().encodeToString(encryptedSessionKey);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}
	
	public Capsule Decrypt(PrivateKey pk) {
		Capsule decrypted = new Capsule();
		try {
			Cipher cipherKey = Cipher.getInstance("RSA");
			cipherKey.init(Cipher.DECRYPT_MODE, pk);
			byte[] encryptedSessionKey = Base64.getDecoder().decode(this.sessionKey);
			SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(encryptedSessionKey), "AES");
			Cipher cipherToken = Cipher.getInstance("AES");
			cipherToken.init(Cipher.DECRYPT_MODE, sessionKey);
			byte[] time = cipherToken.doFinal(Base64.getDecoder().decode(currentTimeInterval.toString()));
			decrypted.currentTimeInterval = new String(time);
			byte[] hash = cipherToken.doFinal(Base64.getDecoder().decode(qrToken));
			decrypted.qrToken = new String(hash);
			byte[] signed = cipherToken.doFinal(Base64.getDecoder().decode(userTokenSigned));
			decrypted.userTokenSigned = new String(signed);
			byte[] unsigned = cipherToken.doFinal(Base64.getDecoder().decode(userTokenUnsigned));
			decrypted.userTokenUnsigned = new String(unsigned);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
}
