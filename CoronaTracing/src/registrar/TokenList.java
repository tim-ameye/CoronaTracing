package registrar;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TokenList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3149249480781414074L;

	private ArrayList<String> tokens;
	private String sessionKey;
	
	public TokenList() {
		tokens = new ArrayList<>();
	}

	public ArrayList<String> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<String> tokens) {
		this.tokens = tokens;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	public TokenList encrypt(SecretKey sessionKey, PublicKey pk) {
		TokenList encrypted = new TokenList();
		try {
			Cipher encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			for(String pseudo: tokens) {
				byte[] enc = encryptText.doFinal(pseudo.getBytes());
				encrypted.add(Base64.getEncoder().encodeToString(enc));
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
	
	public TokenList decryt(PrivateKey pk) {
		TokenList decrypted = new TokenList();
		try {
			Cipher cipherKey = Cipher.getInstance("RSA");
			cipherKey.init(Cipher.DECRYPT_MODE, pk);
			byte[] encryptedSessionKey = Base64.getDecoder().decode(this.sessionKey);
			SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(encryptedSessionKey), "AES");
			Cipher cipherToken = Cipher.getInstance("AES");
			cipherToken.init(Cipher.DECRYPT_MODE, sessionKey);
			for(String enc: tokens) {
				byte[] pseudo = cipherToken.doFinal(Base64.getDecoder().decode(enc));
				decrypted.add(new String(pseudo));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
	
	public void add(String string) {
		tokens.add(string);
	}
	
	public void shuffle() {
		Collections.shuffle(tokens);
	}

	public void addAll(ArrayList<String> pseudo) {
		for(String s: pseudo) tokens.add(s);	
	}
}
