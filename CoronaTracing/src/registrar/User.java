package registrar;

import java.security.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Visitor.VisitorInterface;

public class User {

	private String name;
	private String surname;
	private String phoneNumber;	//unique identifier
	
	private VisitorInterface visitorInt;
	
	private PublicKey publicKey;
	private Map<Instant,ArrayList<byte[]>> tokens;
	
	public User(String name, String surname, String phonNumber) {
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phonNumber;
		this.tokens = new HashMap<Instant, ArrayList<byte[]>>();
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
	
	public VisitorInterface getVisitorInt() {
		return visitorInt;
	}

	public void setVisitorInt(VisitorInterface visitorInt) {
		this.visitorInt = visitorInt;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	
	public void generateTokenSet(int size, SecureRandom random, Signature rsa) throws SignatureException, InvalidKeyException {
		Date date = new Date(System.currentTimeMillis());
		Instant day = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		if(tokens.containsKey(day)) return;
		ArrayList<byte[]> generated = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			byte[] data = new byte[64]; //TODO welk getal nemen we hier?
			random.nextBytes(data);
			rsa.update(data);
			rsa.update(day.toString().getBytes());
			byte[] token = rsa.sign();
			generated.add(token);
		}
		tokens.put(day, generated);
	}
	
	public ArrayList<byte[]> getTokensToday() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		Date date = new Date(System.currentTimeMillis());
		Instant day = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		ArrayList<byte[]> encrypted = new ArrayList<>();
		if(tokens.containsKey(day)) {
			for(byte[] token:tokens.get(day)) {
				encrypted.add(token);
			}			
			return encrypted;
		}
		else return null;
	}
	
	public Map<Instant, ArrayList<byte[]>> getTokens(){
		return tokens;
	}
	
	public void addTokens(Instant instant, ArrayList<byte[]> tokens) {
		this.tokens.put(instant, tokens);
	}
	
	public String toString() { 
		String user = phoneNumber + "_" + name + "_" + surname;
		return user;
	}
}
