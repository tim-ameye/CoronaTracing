package mixingProxy;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Response implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 744473018201265242L;

	private String message;
	private String sessionKey;
	
	public Response() {}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	public Response encrypt(SecretKey sessionKey, PublicKey pk) {
		Response encrypted = new Response();
		try {
			Cipher encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			byte[] messageEnc = encryptText.doFinal(message.getBytes());
			encrypted.message = Base64.getEncoder().encodeToString(messageEnc);
			Cipher encryptSession = Cipher.getInstance("RSA");
			encryptSession.init(Cipher.ENCRYPT_MODE, pk);
			byte[] encryptedSessionKey = encryptSession.doFinal(sessionKey.getEncoded());
			encrypted.sessionKey = Base64.getEncoder().encodeToString(encryptedSessionKey);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}
	
	public Response decrypt(PrivateKey pk) {
		Response decrypted = new Response();
		try {
			Cipher cipherKey = Cipher.getInstance("RSA");
			cipherKey.init(Cipher.DECRYPT_MODE, pk);
			byte[] encryptedSessionKey = Base64.getDecoder().decode(this.sessionKey);
			SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(encryptedSessionKey), "AES");
			Cipher cipherToken = Cipher.getInstance("AES");
			cipherToken.init(Cipher.DECRYPT_MODE, sessionKey);
			byte[] messageDec = cipherToken.doFinal(Base64.getDecoder().decode(message));
			decrypted.message = new String(messageDec);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
}
