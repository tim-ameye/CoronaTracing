package cateringFacility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.google.zxing.WriterException;

import mixingProxy.Response;
import registrar.Hash;
import registrar.RegistrarInterface;

public class CateringClient {
	/*
	 * private int businessNumber; private String name; private String adress;
	 * private String phoneNumber;
	 * 
	 */
	
	private static Instant today;
	private static int randomInteger = -1;

	public static void main(String[] args)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NotBoundException {
		RegistrarInterface server;
		Scanner sc = new Scanner(System.in);

		System.out.println("Please enter business number: ");
		String businessNumber = sc.nextLine();
		System.out.println("Please enter business name: ");
		String name = sc.nextLine();
		System.out.println("Please enter business adress: ");
		String adress = sc.nextLine();
		System.out.println("Please enter business phoneNumber: ");
		String phoneNumber = sc.nextLine();
		CateringFacility cateringFacility = new CateringFacility(businessNumber, name, adress, phoneNumber);

		File dbFile = new File("files\\CateringFacilities");

		if (!dbFile.exists()) {
			dbFile.mkdirs();
		}
		Database db = new Database("files\\CateringFacilities");

		Registry myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		server = (RegistrarInterface) myRegistry.lookup("Registrar");
		PublicKey registrarPubKey = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			char[] password = "AVB6589klp".toCharArray();
			FileInputStream fis = new FileInputStream("files\\keystore.jks");
			keyStore.load(fis, password);
			
			registrarPubKey = keyStore.getCertificate("registrar").getPublicKey();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		
		SecretKey sessionKey = KeyGenerator.getInstance("AES").generateKey();
		CateringFacility cf = cateringFacility.encrypt(sessionKey, registrarPubKey);
		Response ans = server.registerCateringFacility(cf, keyPair.getPublic()).decrypt(keyPair.getPrivate());
		if (ans.getMessage().equals("registered")) {
			System.out.println("[REGISTRAR] We registered the new cateringfacility!");
		} else {
			System.out.println("[REGISTRAR] You are already registrated to the registrar, please try to log in.");
			server.loginCF(cf, keyPair.getPublic()).decrypt(keyPair.getPrivate());
		}
		db.printFile();

		CateringFacility temp = db.findCateringFacility(cateringFacility.getBusinessNumber(),
				cateringFacility.getPhoneNumber());

		today = (new Date(System.currentTimeMillis())).toInstant().truncatedTo(ChronoUnit.DAYS);
		
		if (temp != null) {
			System.out.println("[DATABASE] Already in database, checking if tokens are up to date");
			Hash TempHashes = db.getSavedHashes(temp);
			cateringFacility.setHashes(TempHashes);

			String currentToken = cateringFacility.getCurrentToken();
			if (currentToken == null) {
				// this means that our cateringfacility is in our database, but it's outdated
				// overwrite the current file with our new tokens
				HashGenerator(cateringFacility, server, registrarPubKey);
				db.printFile();

			} else {
				System.out.println("[DATABASE] Tokens are up to date!");
				// we have our cateringfacility in our database and it's up to date!
			}

		} else {
			System.out.println("[DATABASE] Not yet in database, adding it to our database!");

			// we should generate a new set of hashes and add it to the database
			HashGenerator(cateringFacility, server, registrarPubKey);
			db.addFacility(cateringFacility);
		}
		db.printFile();
		// once here db should be good and tokens should be good for atleast a day!

		System.out.println();
		System.out.println("----------------------------------------------");
		System.out.println("Write \'QR\' to generate the QR code for this day.");
		System.out.println("Write Stop to stop this application.");
		String input = "";
		while (!input.equals("Stop")) {
			if (sc.hasNext()) {
				input = sc.nextLine();
				if (input.equals("QR")) {
					MakeQRForToday(cateringFacility);
				}
			}
		}
		sc.close();
	}

	public static void HashGenerator(CateringFacility cateringFacility, RegistrarInterface server, PublicKey pk) {
		Hash ans = null;
		try {
			SecretKey sessionKey = KeyGenerator.getInstance("AES").generateKey();
			ans = server.getHashesCatering(cateringFacility.encrypt(sessionKey, pk),
					cateringFacility.getPublic());
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Hash hash = ans.decrypt(cateringFacility.getPrivate());
		cateringFacility.setHash(hash);
	}

	public static void MakeQRForToday(CateringFacility cateringFacility) throws NoSuchAlgorithmException {
		
		Date date = new Date(System.currentTimeMillis());
		Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		
		if(!today.equals(currentDay) || randomInteger == -1) {
			randomInteger = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
		}

		String randomNumString = Integer.toString(randomInteger);
		
		String cfIndentifier = cateringFacility.toStringFileName();
		
		String currentDayString = currentDay.toString().substring(0, 10);

		String currentToken = cateringFacility.getCurrentToken();

		String newHashString = randomNumString + currentToken;
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] nym = md.digest(newHashString.getBytes());

		String newHashAsString = Base64.getEncoder().encodeToString(nym);

		String QRString = randomNumString + "_" + cfIndentifier + "_" + newHashAsString;

		// writing the tokes as a string to a qr code located at
		// QRCodes/Brn+Buisnissnummer+Date.png
		try {

			System.out.println("[SYSTEM] Trying to make the QR for string: " + QRString);
			File qrCodesSave = new File("files\\QRCodes");
			if(!qrCodesSave.exists()) {
				qrCodesSave.mkdir();
			}
			cateringFacility.generateQRCodeImage(QRString, 200, 200,
					"files\\QRCodes\\Bnr" + cateringFacility.getBusinessNumber() + "D" + currentDayString + ".png");
			System.out.println("[SYSTEM] qr code should be found at: ./QRCodes/Bnr"
					+ cateringFacility.getBusinessNumber() + "D" + currentDayString + ".png");

		} catch (WriterException e) {
			System.out.println("[SYSTEM] Could not generate QR Code, WriterException :: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("[SYSTEM] Could not generate QR Code, IOException :: " + e.getMessage());
		}

	}

}
