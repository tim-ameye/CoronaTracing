package Doctor;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import Visitor.Visit;

public class Infection {

	private ArrayList<String> signedVisits;
	private ArrayList<String> unsignedVisits;
	Signature signature;
	private String sessionKey;
	
	public Infection() {
		this.signedVisits = new ArrayList<>();
		this.unsignedVisits = new ArrayList<>();
	}
	
	public void signSignature(Doctor doctor) {
		try {
			signature.initSign(doctor.getKeyPair().getPrivate());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getSignedVisits() {
		return signedVisits;
	}

	public void setSignedVisits(ArrayList<String> signedVisits) {
		this.signedVisits = signedVisits;
	}

	public ArrayList<String> getUnsignedVisits() {
		return unsignedVisits;
	}

	public void setUnsignedVisits(ArrayList<String> unsignedVisits) {
		this.unsignedVisits = unsignedVisits;
	}

	public void add(Visit visit) {
		unsignedVisits.add(visit.toExtendedString());
		byte[] visitByte = visit.toExtendedString().getBytes();
		try {
			signature.update(visitByte);
			byte[] signed = signature.sign();
			signedVisits.add(Base64.getEncoder().encodeToString(signed));
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Infection encrypt(SecretKey sessionKey, PublicKey pk) {
		Infection encrypted = new Infection();
		try {
			Cipher encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			for(String visit : unsignedVisits) {
				byte[] encrypt = encryptText.doFinal(visit.getBytes());
				String encryptedVisit = Base64.getEncoder().encodeToString(encrypt);				
				encrypted.unsignedVisits.add(encryptedVisit);
			}
			for(String visit : signedVisits) {
				byte[] encrypt = encryptText.doFinal(visit.getBytes());
				String encryptedVisit = Base64.getEncoder().encodeToString(encrypt);				
				encrypted.unsignedVisits.add(encryptedVisit);
			}
			Cipher encryptSession = Cipher.getInstance("RSA");
			encryptSession.init(Cipher.ENCRYPT_MODE, pk);
			byte[] encryptedSessionKey = encryptSession.doFinal(sessionKey.getEncoded());
			encrypted.sessionKey = Base64.getEncoder().encodeToString(encryptedSessionKey);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return encrypted;	
	}
	
	public Infection decrypt(PrivateKey pk) {
		Infection decrypted = new Infection();
		try {
			Cipher cipherKey = Cipher.getInstance("RSA");
			cipherKey.init(Cipher.DECRYPT_MODE, pk);
			byte[] encryptedSessionKey = Base64.getDecoder().decode(this.sessionKey);
			SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(encryptedSessionKey), "AES");
			Cipher cipherToken = Cipher.getInstance("AES");
			cipherToken.init(Cipher.DECRYPT_MODE, sessionKey);
			for(String visit : unsignedVisits) {
				byte[] decrypt = cipherToken.doFinal(Base64.getDecoder().decode(visit));
				decrypted.unsignedVisits.add(new String(decrypt));
			}
			for(String visit : signedVisits) {
				byte[] decrypt = cipherToken.doFinal(Base64.getDecoder().decode(visit));
				decrypted.unsignedVisits.add(new String(decrypt));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
	
	
	
	
}
