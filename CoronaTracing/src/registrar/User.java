package registrar;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class User {

	private String name;
	private String surname;
	private String phoneNumber;	//unique identifier
	
	private SecretKey secretKey;
	
	public User(String name, String surname, String phonNumber) {
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phonNumber;
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
	
	public void generateTokenSet(int size) {
		for(int i = 0; i < size; i++) {
			SecureRandom random = new SecureRandom();
			
		}
	}
	
}
