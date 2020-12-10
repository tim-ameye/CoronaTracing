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
	private ArrayList<Token> tokens;
	
	public User(String name, String surname, String phonNumber) {
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phonNumber;
		this.tokens = new ArrayList<>();
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
	
	public void generateTokenSet(SecureRandom random, Signature rsa) throws SignatureException, InvalidKeyException {
		Date date = new Date(System.currentTimeMillis());
		Instant day = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		Token token = new Token();
		token.genToken(random, rsa, day);
		tokens.add(token);
	}
	
	public Token getTokensToday() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		Date date = new Date(System.currentTimeMillis());
		Instant day = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		for(Token token: tokens) {
			if(token.getDay().equals(day)) return token;
		}
		return null;
	}
	
	public ArrayList<Token> getTokens(){
		return tokens;
	}
	
	public void addTokens(Token token) {
		this.tokens.add(token);
	}
	
	public String toString() { 
		String user = phoneNumber + "_" + name + "_" + surname;
		return user;
	}
}
