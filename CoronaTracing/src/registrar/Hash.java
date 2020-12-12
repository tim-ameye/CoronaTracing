package registrar;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Hash implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6900361278173570190L;

	private Map<String, String> pseudonyms;
	private String sessionKey;
	
	public Hash() {
		pseudonyms = new HashMap<>();
	}

	public Map<String, String> getPseudonyms() {
		return pseudonyms;
	}

	public void setPseudonyms(Map<String, String> pseudonyms) {
		this.pseudonyms = pseudonyms;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	public void genHash(String message, MessageDigest md, Instant day) {
		byte[] nym = md.digest(message.getBytes());
		String pseudo = Base64.getEncoder().encodeToString(nym);
		pseudonyms.put(day.toString(), pseudo);
	}
	
	public String get(Instant day) {
		return pseudonyms.get(day.toString());
	}
	
	public void put(String day, String hash) {
		pseudonyms.put(day, hash);
	}
	
	public Hash encrypt(SecretKey sessionKey, PublicKey pk) {
		Hash encrypted = new Hash();
		try {
			Cipher encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			for(Map.Entry<String, String> entry : pseudonyms.entrySet()) {
				byte[] date = encryptText.doFinal(entry.getKey().getBytes());
				byte[] hash = encryptText.doFinal(entry.getValue().getBytes());
				String dateBase = Base64.getEncoder().encodeToString(date);
				String hashBase = Base64.getEncoder().encodeToString(hash);
				encrypted.pseudonyms.put(dateBase, hashBase);
			}
			Cipher encryptSession = Cipher.getInstance("RSA");
			encryptSession.init(Cipher.ENCRYPT_MODE, pk);
			byte[] encryptedSessionKey = encryptSession.doFinal(sessionKey.getEncoded());
			encrypted.sessionKey = Base64.getEncoder().encodeToString(encryptedSessionKey);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}
	
	public Hash decrypt(PrivateKey pk) {
		Hash decrypted = new Hash();
		try {
			Cipher cipherKey = Cipher.getInstance("RSA");
			cipherKey.init(Cipher.DECRYPT_MODE, pk);
			byte[] encryptedSessionKey = Base64.getDecoder().decode(this.sessionKey);
			SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(encryptedSessionKey), "AES");
			Cipher cipherToken = Cipher.getInstance("AES");
			cipherToken.init(Cipher.DECRYPT_MODE, sessionKey);
			for(Map.Entry<String, String> entry : pseudonyms.entrySet()) {
				byte[] date = cipherToken.doFinal(Base64.getDecoder().decode(entry.getKey().getBytes()));
				byte[] hash = cipherToken.doFinal(Base64.getDecoder().decode(entry.getValue().getBytes()));
				String dateBase = new String(date);
				String hashBase = new String(hash);
				decrypted.pseudonyms.put(dateBase, hashBase);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
	
}
