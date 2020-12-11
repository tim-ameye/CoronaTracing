package registrar;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Token implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4419465365514647531L;
	
	private ArrayList<String> unsignedTokens;
	private ArrayList<String> signedTokens;
	private Instant day;
	private String sessionKey;
	private int itterator;

	public Token() {
		unsignedTokens = new ArrayList<>();
		signedTokens = new ArrayList<>();
		itterator = 0;
	}

	public ArrayList<String> getUnsignedTokens() {
		return unsignedTokens;
	}

	public void setUnsignedTokens(ArrayList<String> unsignedTokens) {
		this.unsignedTokens = unsignedTokens;
	}

	public ArrayList<String> getSignedTokens() {
		return signedTokens;
	}

	public void setSignedTokens(ArrayList<String> signedTokens) {
		this.signedTokens = signedTokens;
	}

	public Instant getDay() {
		return day;
	}

	public void setDay(Instant day) {
		this.day = day;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public void genToken(SecureRandom random, Signature rsa, Instant day) {
		this.day = day;
		try {
			for (int i = 0; i < 48; i++) {
				byte[] token = new byte[64];
				random.nextBytes(token);
				rsa.update(token);
				rsa.update(day.toString().getBytes());
				byte[] dag = day.toString().getBytes();
				byte[] unsigned = concat(token, dag);
				byte[] signed = rsa.sign();
				signedTokens.add(Base64.getEncoder().encodeToString(signed));
				unsignedTokens.add(Base64.getEncoder().encodeToString(unsigned));
			}
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] concat(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i];
		}
		for (int i = 0; i < b.length; i++) {
			result[a.length + i] = b[i];
		}
		return result;
	}
	
	public Token encrypt(SecretKey sessionKey, PublicKey pk) {
		Token encrypted = new Token();
		try {
			Cipher encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			for(int i = 0; i < signedTokens.size(); i++) {
				byte[] signed = encryptText.doFinal(Base64.getDecoder().decode(signedTokens.get(i)));
				byte[] unsigned = encryptText.doFinal(Base64.getDecoder().decode(unsignedTokens.get(i)));
				encrypted.signedTokens.add(Base64.getEncoder().encodeToString(signed));
				encrypted.unsignedTokens.add(Base64.getEncoder().encodeToString(unsigned));
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
	
	public Token decrypt(PrivateKey pk) {
		Token decrypted = new Token();
		try {
			Cipher cipherKey = Cipher.getInstance("RSA");
			cipherKey.init(Cipher.DECRYPT_MODE, pk);
			byte[] encryptedSessionKey = Base64.getDecoder().decode(this.sessionKey);
			SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(encryptedSessionKey), "AES");
			Cipher cipherToken = Cipher.getInstance("AES");
			cipherToken.init(Cipher.DECRYPT_MODE, sessionKey);
			for(int i = 0; i < signedTokens.size(); i++) {
				byte[] signed = cipherToken.doFinal(Base64.getDecoder().decode(signedTokens.get(i)));
				byte[] unsigned = cipherToken.doFinal(Base64.getDecoder().decode(unsignedTokens.get(i)));
				decrypted.signedTokens.add(Base64.getEncoder().encodeToString(signed));
				decrypted.unsignedTokens.add(Base64.getEncoder().encodeToString(unsigned));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
	
	public void addToken(String signed, String unsigned) {
		signedTokens.add(signed);
		unsignedTokens.add(unsigned);
	}
	
	public boolean contains(String infected) {
		for(String signed: signedTokens) {
			if(signed.equals(infected))
				return true;
		}
		return false;
	}
	
	public String[] getUnusedToken() {
		String[] result = new String[2];
		result[0] = signedTokens.get(itterator);
		result[1] = unsignedTokens.get(itterator);
		return result;
	}
}
