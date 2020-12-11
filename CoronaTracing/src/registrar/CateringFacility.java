package registrar;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
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

import cateringFacility.CateringInterface;


public class CateringFacility {
	private String businessNumber;
	private String name;
	private String adress;
	private String phoneNumber;
	private Logger logger;
	CateringInterface cateringInt;
	
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
	public CateringInterface getCateringInt() {
		return cateringInt;
	}
	public void setCateringInt(CateringInterface cateringInt) {
		this.cateringInt = cateringInt;
	}
	public SecretKey getSecretKey(KeyStore keyStore) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		if(secretKey == null) {
			secretKey = (SecretKey) keyStore.getKey(businessNumber, "AVB6589klP".toCharArray());
		}
		return secretKey;
	}
	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}
	public void generateSecretKey(KeyGenerator kg, KeyStore keyStore) throws KeyStoreException {
		secretKey = kg.generateKey();
		KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection("AVB6589klP".toCharArray());
		KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
		keyStore.setEntry(businessNumber, secretKeyEntry, protectionParam);
	}
	
	public void generateHashes(int period, SecretKeyFactory skf, SecureRandom random, MessageDigest md) throws InvalidKeySpecException {
		int overlap = 0;
		Date date = new Date(System.currentTimeMillis());
		Instant firstDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		//Check for what dates already a has has been created
		for(Map.Entry<Instant,byte[]> entry : hashMap.entrySet()) {
			if(entry.getKey().isAfter(firstDay)) overlap++;
		}
		for(int i = overlap; i < period; i++) {
			//Create the date for the hash
			Instant inst = date.toInstant().truncatedTo(ChronoUnit.DAYS);
			inst = inst.plus(i, ChronoUnit.DAYS);
			String dag = inst.toString();
			//Create a random seed
			byte[] seed = new byte[64];
			random.nextBytes(seed);
			//create the password for the secretKeyFactory
			char[] password = (businessNumber+dag+secretKey).toCharArray();
			PBEKeySpec spec = new PBEKeySpec(password, seed, 10000,128);
			byte[] key = skf.generateSecret(spec).getEncoded();
			//Create a pseudonym for that key with a cryptographic hash
			String message = key.toString() + adress + dag;
			byte[] nym = md.digest(message.getBytes());
			//save the key in to a map with as key the date and value the hash
			hashMap.put(inst, nym);
		}
	}
	
	public byte[] nymToday() {
		Date date = new Date(System.currentTimeMillis());
		Instant today = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		return hashMap.get(today);
	}
	
	public byte[] getHashToday() {
		Date date = new Date(System.currentTimeMillis());
		Instant instant = date.toInstant();
		byte[] answer = hashMap.get(instant);
		if(answer == null) logger.info("New hashes need to be created for catering facility: " + businessNumber);
		return answer;
	}
	
	public Map<Instant, byte[]> getHashFromToday(){
		Date date = new Date(System.currentTimeMillis());
		Instant instant = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		Map<Instant, byte[]> map = new HashMap<>();
		for(Map.Entry<Instant,byte[]> entry : hashMap.entrySet()) {
			if(entry.getKey().isAfter(instant) || entry.getKey().equals(instant)) map.put(entry.getKey(), entry.getValue());
		}
		if(map.isEmpty()) {
			logger.info("There are no more hashes created for catering facility: " + businessNumber);
			return null;
		}
		return map;
	}
	
	public String toString() {
		String catering = businessNumber + "_" + name + "_" + adress + "_" + phoneNumber;
		for(Map.Entry<Instant,byte[]> entry : hashMap.entrySet()) {
			catering += "_" + entry.getKey().toString();
		}
		return catering;
	}
	
	public String toStringFileName() {
		return businessNumber + "_" + phoneNumber;
	}
	
	public Map<Instant,byte[]> getHashes() {
		return hashMap;
	}
	
	public void addHashes(Instant instant, byte[] hash) {
		hashMap.put(instant, hash);
	}
}
