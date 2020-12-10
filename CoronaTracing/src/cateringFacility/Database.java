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
		Scanner sc = new Scanner(dbFile);
		FileInputStream sigfig = null;
		File sig = null;
		sc.nextLine();
		String line = sc.nextLine();
		while (!line.equals("#End")) {
			String[] info = line.split("_");
			CateringFacility cf = new CateringFacility(info[0], info[1], info[2], info[3]);
			for(int i = 4; i < info.length; i++) {
				Instant date = Instant.parse(info[i]);
				sig = new File("CateringFacilities\\"+cf.toStringFileName()+"\\" + info[i].toString().substring(0, 10));
				sigfig = new FileInputStream("CateringFacilities\\"+cf.toStringFileName()+"\\" + info[i].toString().substring(0, 10));
				byte[] token = new byte[(int) sig.length()];
				sigfig.read(token);
				
				cf.getHashes().put(date, token);		//TODO Check if this actually works, not so sure 
			}
			facilities.add(cf);
			line = sc.nextLine();
		}					
		sc.close();
	}

	public void addFacility(CateringFacility cateringFacility) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		// check if it's not already in our db
		printFile();
		readFile(); // this will update the facilities 
		
		CateringFacility cf = findCateringFacility(cateringFacility.getBusinessNumber(), cateringFacility.getPhoneNumber());
		if (cf == null) {			
			facilities.add(cateringFacility);
			System.out.println("[DATABASE] Catering facility added");
		}else {
			System.out.println("[DATABASE] No catering facility added, already in our list!");
		}
	}
	
	public Map<Instant, byte[]> getSavedHashes(CateringFacility cateringFacility) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException{
		Map<Instant, byte[]> hashes = new HashMap<>();
		// make sure our cateringfacilities is updated
		readFile();
		
		CateringFacility cf = findCateringFacility(cateringFacility.getBusinessNumber(), cateringFacility.getPhoneNumber());
		hashes = cf.getHashes(); // hmmmmmmmmmmm not sure how things work here
		
		
		return hashes;
		
	}
	
	
	
	public void printFile() throws IOException {
		PrintWriter pw = new PrintWriter(dbFile);
		FileOutputStream sigfos = null;
		File sigfile = null;
		pw.println("#Facilities");
		for (CateringFacility cf : facilities) {
			pw.println(cf.toString());
			sigfile = new File("CateringFacilities\\" + cf.toStringFileName());
			if (!sigfile.exists()) {
				sigfile.mkdirs();
			}
			Map<Instant, byte[]> tokens = cf.getHashes();
			for (Map.Entry<Instant, byte[]> entry : tokens.entrySet()) {
				sigfos = new FileOutputStream("CateringFacilities\\" + cf.toStringFileName() + "\\" + entry.getKey().toString().substring(0, 10));
				sigfos.write(entry.getValue());
				sigfos.close();
			}
		}
		
		pw.println("#End");
		pw.flush();
		pw.close();
	}

	public CateringFacility findCateringFacility(String business, String phoneNumber) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		readFile();
		for (CateringFacility cf : facilities) {
			if (cf.getBusinessNumber().toString() == (business) && cf.getPhoneNumber().toString() ==(phoneNumber))
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
