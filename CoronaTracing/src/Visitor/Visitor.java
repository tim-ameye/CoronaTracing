package Visitor;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class Visitor implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1915407624181236202L;
	
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private KeyPair keyPair;
	private String pubKey;
	private String sessionKey;
	
	public Visitor(){
		try {
			keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pubKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
	}
	
	public Visitor(String firstName, String lastName, String phoneNumber) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		try {
			keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public PublicKey getPublicKey() {
		return keyPair.getPublic();
	}
	
	public PrivateKey getPrivateKey() {
		return keyPair.getPrivate();
	}

	public Visitor encrypt(SecretKey sessionKey, PublicKey publicKey) {
		Visitor encrypted = new Visitor();
		try {
			Cipher encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			byte[] firstNameByte = encryptText.doFinal(firstName.getBytes());
			encrypted.firstName = Base64.getEncoder().encodeToString(firstNameByte);
			byte[] lastNameByte = encryptText.doFinal(lastName.getBytes());
			encrypted.lastName = Base64.getEncoder().encodeToString(lastNameByte);
			byte[] phoneNumberByte = encryptText.doFinal(phoneNumber.getBytes());
			encrypted.phoneNumber = Base64.getEncoder().encodeToString(phoneNumberByte);
			byte[] pubKeyByte = encryptText.doFinal(pubKey.getBytes());
			encrypted.pubKey = Base64.getEncoder().encodeToString(pubKeyByte);
			Cipher encryptSession = Cipher.getInstance("RSA");
			encryptSession.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedSessionKey = encryptSession.doFinal(sessionKey.getEncoded());
			encrypted.sessionKey = Base64.getEncoder().encodeToString(encryptedSessionKey);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}
	
	public Visitor decrypt(PrivateKey privateKey) {
		Visitor decrypted = new Visitor();
		try {
			Cipher cipherKey = Cipher.getInstance("RSA");
			cipherKey.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] encryptedSessionKey = Base64.getDecoder().decode(this.sessionKey);
			SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(encryptedSessionKey), "AES");
			Cipher cipherToken = Cipher.getInstance("AES");
			cipherToken.init(Cipher.DECRYPT_MODE, sessionKey); 
			byte[] firstNameByte = cipherToken.doFinal(Base64.getDecoder().decode(firstName));
			decrypted.firstName = new String(firstNameByte);
			byte[] lastNameByte = cipherToken.doFinal(Base64.getDecoder().decode(lastName));
			decrypted.lastName = new String(lastNameByte);
			byte[] phoneNumberByte = cipherToken.doFinal(Base64.getDecoder().decode(phoneNumber));
			decrypted.phoneNumber = new String(phoneNumberByte);
			byte[] pubKeyByte = cipherToken.doFinal(Base64.getDecoder().decode(pubKey));
			decrypted.pubKey = new String(pubKeyByte);
			keyPair = new KeyPair((PublicKey) new X509EncodedKeySpec(Base64.getDecoder().decode(pubKey.getBytes())), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
	
}
