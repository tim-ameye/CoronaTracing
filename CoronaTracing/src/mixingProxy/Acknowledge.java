package mixingProxy;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Acknowledge {
	private String userTokenSigned;
	private String qrToken;
	private String instant;
	private String sessionKey;
	
	
	public Acknowledge(String userTokenSigned, String qrToken, String instant) {
		super();
		this.userTokenSigned = userTokenSigned;
		this.qrToken = qrToken;
		this.instant = instant;
	}
	
	public Acknowledge() {
		// TODO Auto-generated constructor stub
	}

	public String getInstant() {
		return instant;
	}

	public void setInstant(String instant) {
		this.instant = instant;
	}

	public String getUserTokenSigned() {
		return userTokenSigned;
	}
	public void setUserTokenSigned(String userTokenSigned) {
		this.userTokenSigned = userTokenSigned;
	}
	public String getQrToken() {
		return qrToken;
	}
	public void setQrToken(String qrToken) {
		this.qrToken = qrToken;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public Acknowledge encrypt(SecretKey sessionKey, PublicKey pk) {
		Acknowledge encrypted = new Acknowledge();
		try {
			Cipher encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			byte[] time = encryptText.doFinal(instant.toString().getBytes());
			encrypted.instant = Base64.getEncoder().encodeToString(time);
			byte[] hash = encryptText.doFinal(Base64.getDecoder().decode(qrToken));
			encrypted.qrToken = Base64.getEncoder().encodeToString(hash);
			byte[] signed = encryptText.doFinal(Base64.getDecoder().decode(userTokenSigned));
			encrypted.userTokenSigned = Base64.getEncoder().encodeToString(signed);
			Cipher encryptSession = Cipher.getInstance("RSA");
			encryptSession.init(Cipher.ENCRYPT_MODE, pk);
			byte[] encryptedSessionKey = encryptSession.doFinal(sessionKey.getEncoded());
			encrypted.sessionKey = Base64.getEncoder().encodeToString(encryptedSessionKey);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}
	
	public Acknowledge Decrypt(PrivateKey pk) {
		Acknowledge decrypted = new Acknowledge();
		try {
			Cipher cipherKey = Cipher.getInstance("RSA");
			cipherKey.init(Cipher.DECRYPT_MODE, pk);
			byte[] encryptedSessionKey = Base64.getDecoder().decode(this.sessionKey);
			SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(encryptedSessionKey), "AES");
			Cipher cipherToken = Cipher.getInstance("AES");
			cipherToken.init(Cipher.DECRYPT_MODE, sessionKey);
			byte[] time = cipherToken.doFinal(Base64.getDecoder().decode(instant.toString()));
			decrypted.instant = new String(time);
			byte[] hash = cipherToken.doFinal(Base64.getDecoder().decode(qrToken));
			decrypted.qrToken = Base64.getEncoder().encodeToString(hash);
			byte[] signed = cipherToken.doFinal(Base64.getDecoder().decode(userTokenSigned));
			decrypted.userTokenSigned = Base64.getEncoder().encodeToString(signed);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}

}
