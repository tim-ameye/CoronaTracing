package registrar;

import java.security.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class User {

	private String name;
	private String surname;
	private String phoneNumber;	//unique identifier
	
	private KeyPair keyPair;
	private ArrayList<byte[]> tokens;
	
	public User(String name, String surname, String phonNumber) throws NoSuchAlgorithmException {
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phonNumber;
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		keyGen.initialize(2048);
		keyPair = keyGen.generateKeyPair();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public void generateTokenSet(int size) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		SecureRandom random = new SecureRandom();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(System.currentTimeMillis());
		String dateString = formatter.format(date);
		Signature dsa = Signature.getInstance("SHA256withDSA");
		dsa.initSign(keyPair.getPrivate());
		
		for(int i = 0; i < size; i++) {
			byte[] data = new byte[50]; //TODO welk getal nemen we hier?
			random.nextBytes(data);
			dsa.update(data);
			dsa.update(dateString.getBytes());
			byte[] token = dsa.sign();
			tokens.add(token);
		}
	}
	
}
