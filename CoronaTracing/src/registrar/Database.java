package registrar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.crypto.spec.SecretKeySpec;

public class Database {
	
	private ArrayList<User> users;
	private ArrayList<CateringFacility> facilities;
	private File dbFile;
	private Logger logger;
	
	public Database(String dbFileName) {
		dbFile = new File(dbFileName);
		users = new ArrayList<>();
		facilities = new ArrayList<>();
		logger = Logger.getLogger("Database");
	}
	
	public void readFile() throws FileNotFoundException {
		logger.info("Started reading the database file.");
		//read the database file
		Scanner sc = new Scanner(dbFile);
		sc.nextLine();
		String line = sc.nextLine();
		while(!line.equals("#Users")) {
			String[] info = line.split("_");
			CateringFacility cf = new CateringFacility(info[0], info[1], info[2], info[3]);
			cf.setSecretKey(new SecretKeySpec(info[4].getBytes(), "DES"));
			for(int i = 5; i < info.length; i+=2) {
				cf.addHashes(Instant.parse(info[i]), info[i+1].getBytes());
			}
			facilities.add(cf);
			line = sc.nextLine();
		}
		if(sc.hasNext()) {
			sc.nextLine();
			while(sc.hasNext()) {
				String[] info = line.split("_");
				

				line = sc.nextLine();
			}
		}
		sc.close();
	}
	
	public void printFile() throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(dbFile);
		pw.println("#Facilities");
		for(CateringFacility cf: facilities) {
			pw.println(cf.toString());
		}
		pw.println("#Users");
		for(User u: users) {
			pw.println(u.toString());
		}
		pw.flush();
		pw.close();
	}
	
	public CateringFacility findCateringFacility(String business, String phoneNumber) {
		for(CateringFacility cf:facilities) {
			if(cf.getBusinessNumber().equals(business) && cf.getPhoneNumber().equals(phoneNumber)) return cf;
		}
		return null;
	}
	
	public void addCateringFacility(CateringFacility cf) {
		synchronized (facilities) {
			facilities.add(cf);
		}
	}
	
	public User findUser(String phoneNumber) {
		for(User u: users) {
			if(u.getPhoneNumber().equals(phoneNumber)) return u;
		}
		return null;
	}
	
	public void addVisitor(User u) {
		synchronized (users) {
			users.add(u);
		}
	}
}
