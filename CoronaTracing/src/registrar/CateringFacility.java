package registrar;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class CateringFacility {
	private String businessNumber;
	private String name;
	private String adress;
	private String phoneNumber;
	
	private SecretKey secretKey;
	
	private ArrayList<byte[]> dayKeys;
	
	public CateringFacility(String businessNumber, String name, String adress, String phoneNumber) {
		super();
		this.businessNumber = businessNumber;
		this.name = name;
		this.adress = adress;
		this.phoneNumber = phoneNumber;
	}
	
	public String getBusinessNumber() {
		return businessNumber;
	}
	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public void generateSecretKey(KeyGenerator kg) {
		secretKey = kg.generateKey();
	}
	
	public void generateHashes(int period, SecretKeyFactory skf, SecureRandom random, MessageDigest md) throws InvalidKeySpecException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date(System.currentTimeMillis());
		String dateString = formatter.format(date);
		int dateInt = Integer.parseInt(dateString);
		for(int i = dateInt; i < dateInt+period; i++) {
			byte[] seed = new byte[64];
			random.nextBytes(seed);
			char[] password = (businessNumber+String.valueOf(dateInt+i)+secretKey).toCharArray();
			int iterations = 10000;
			PBEKeySpec spec = new PBEKeySpec(password, seed, iterations);
			byte[] key = skf.generateSecret(spec).getEncoded();
			String message = key.toString() + adress + String.valueOf(dateInt+i);
			byte[] nym = md.digest(message.getBytes());
			dayKeys.add(nym);
		}
	}
}
