package cateringFacility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import registrar.Hash;

public class Database {

	private ArrayList<CateringFacility> facilities;
	private File dbFile;
	private Logger logger;

	public Database(String dbFileName) {
		dbFile = new File(dbFileName);
		facilities = new ArrayList<>();
		logger = Logger.getLogger("Database");
	}

	public void readFile() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		logger.info("Started reading the database file.");
		// read the database file
		Scanner sigfig = null;
		Hash hash = new Hash();
		String[] lijst = dbFile.list();
		for(String line: lijst) {
			String[] info = line.split("_");
			CateringFacility cf = new CateringFacility(info[0], info[1], info[2], info[3]);
			sigfig = new Scanner(new File("files\\CateringFacilities\\" + line));
			while (sigfig.hasNextLine()) {
				String date = sigfig.nextLine();
				String pseudo = sigfig.nextLine();
				hash.put(date, pseudo);
			}
			cf.setHash(hash);
			facilities.add(cf);
		}
		
		
	}

	public void addFacility(CateringFacility cateringFacility)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		// check if it's not already in our db
		printFile();
		readFile(); // this will update the facilities

		CateringFacility cf = findCateringFacility(cateringFacility.getBusinessNumber(),
				cateringFacility.getPhoneNumber());
		if (cf == null) {
			facilities.add(cateringFacility);
			System.out.println("[DATABASE] Catering facility added");
		} else {
			System.out.println("[DATABASE] No catering facility added, already in our list!");
		}
	}

	public Hash getSavedHashes(CateringFacility cateringFacility)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		Hash hash = new Hash();
		// make sure our cateringfacilities is updated
		readFile();

		CateringFacility cf = findCateringFacility(cateringFacility.getBusinessNumber(),
				cateringFacility.getPhoneNumber());
		hash = cf.getHashes(); // hmmmmmmmmmmm not sure how things work here

		return hash;

	}

	public void printFile() throws IOException {
		PrintWriter sigfos = null;
		File sigfile = null;
		for (CateringFacility cf : facilities) {
			sigfile = new File("files\\CateringFacilities");
			if (!sigfile.exists()) {
				sigfile.mkdirs();
			}
			sigfos = new PrintWriter("files\\CateringFacilities\\" + cf.toString() + "_.txt");
			for (Map.Entry<String, String> entry : cf.getHashes().getPseudonyms().entrySet()) {
				sigfos.println(entry.getKey());
				sigfos.println(entry.getValue());
			}
			sigfos.close();
		}
	}

	public CateringFacility findCateringFacility(String business, String phoneNumber)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		readFile();
		for (CateringFacility cf : facilities) {
			if (cf.getBusinessNumber().toString() == (business) && cf.getPhoneNumber().toString() == (phoneNumber))
				System.out.println("[DATABASE] Found it!");
			return cf;
		}
		System.out.println("[DATABASE] Couldn't find current facility.");
		return null;
	}

	public void addCateringFacility(CateringFacility cf) {
		synchronized (facilities) {
			facilities.add(cf);
		}
	}
}
