package cateringFacility;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.google.zxing.WriterException;

import registrar.Hash;
import registrar.RegistrarInterface;

public class CateringClient {
	/*
	 * private int businessNumber; private String name; private String adress;
	 * private String phoneNumber;
	 * 
	 */
	
	private Instant today;
	private int randomInteger;

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

		cateringFacility.testConnection("Test from cateringside");

		Registry myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		server = (RegistrarInterface) myRegistry.lookup("Registrar");

		if (server.registerCateringFacility(cateringFacility)) {
			System.out.println("[REGISTRAR] We registered the new cateringfacility!");
		} else {
			System.out.println("[REGISTRAR] You are already registrated to the registrar, please try to log in.");
			server.loginCF(cateringFacility);
		}
		db.printFile();

		CateringFacility temp = db.findCateringFacility(cateringFacility.getBusinessNumber(),
				cateringFacility.getPhoneNumber());

		today = new Date(System.currentTimeMillis())
		
		if (temp != null) {
			System.out.println("[DATABASE] Already in database, checking if tokens are up to date");
			Hash TempHashes = db.getSavedHashes(temp);
			cateringFacility.setHashes(TempHashes);

			String currentToken = cateringFacility.getCurrentToken();
			if (currentToken == null) {
				// this means that our cateringfacility is in our database, but it's outdated
				// overwrite the current file with our new tokens
				HashGenerator(cateringFacility, server);
				db.printFile();

			} else {
				System.out.println("[DATABASE] Tokens are up to date!");
				// we have our cateringfacility in our database and it's up to date!
			}

		} else {
			System.out.println("[DATABASE] Not yet in database, adding it to our database!");

			// we should generate a new set of hashes and add it to the database
			HashGenerator(cateringFacility, server);
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

	public static void HashGenerator(CateringFacility cateringFacility, RegistrarInterface server) {
		Hash ans = null;
		try {
			ans = server.getHashesCatering(cateringFacility.getBusinessNumber(), cateringFacility.getPhoneNumber(),
					cateringFacility.getPublic());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		Hash hash = ans.decrypt(cateringFacility.getPrivate());
		cateringFacility.setHash(hash);
	}

	public static void MakeQRForToday(CateringFacility cateringFacility) throws NoSuchAlgorithmException {

		int randomNum = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
		String randomNumString = Integer.toString(randomNum);

		String cfIndentifier = cateringFacility.toStringFileName();

		Date date = new Date(System.currentTimeMillis());
		Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
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
