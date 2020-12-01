package registrar;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class CateringFacility {
	private String businessNumber;
	private String name;
	private String adress;
	private String phoneNumber;
	private Logger logger;
	
	private SecretKey secretKey;
	
	private Map<Instant, byte[]> hashMap;
	
	public CateringFacility(String businessNumber, String name, String adress, String phoneNumber) {
		super();
		this.businessNumber = businessNumber;
		this.name = name;
		this.adress = adress;
		this.phoneNumber = phoneNumber;
		hashMap = new HashMap<>();
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
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
		for(int i = 0; i < period; i++) {
			//Create the date for the hash
			Instant inst = date.toInstant();
			inst = inst.plus(i, ChronoUnit.DAYS);
			String dag = formatter.format(inst);
			//Create a random seed
			byte[] seed = new byte[64];
			random.nextBytes(seed);
			//create the password for the secretKeyFactory
			char[] password = (businessNumber+dag+secretKey).toCharArray();
			PBEKeySpec spec = new PBEKeySpec(password, seed, 10000);
			byte[] key = skf.generateSecret(spec).getEncoded();
			//Create a pseudonym for that key with a cryptographic hash
			String message = key.toString() + adress + dag;
			byte[] nym = md.digest(message.getBytes());
			//save the key in to a map with as key the date and value the hash
			hashMap.put(inst, nym);
		}
	}
	
	public byte[] getHashToday() {
		Date date = new Date(System.currentTimeMillis());
		Instant instant = date.toInstant();
		byte[] answer = hashMap.get(instant);
		if(answer == null) logger.info("New hashes need to be created for catering facility: " + businessNumber);
		return answer;
	}
}
