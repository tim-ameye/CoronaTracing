package registrar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.crypto.spec.SecretKeySpec;

public class Database {

	private ArrayList<User> users;
	private ArrayList<CateringFacility> facilities;
	private File dbFile;
	private Logger logger;
	private String path;

	public Database(String dbFileName) {
		dbFile = new File(dbFileName);
		users = new ArrayList<>();
		facilities = new ArrayList<>();
		logger = Logger.getLogger("Database");
		path = "C:\\Users\\timam\\git\\CoronaTracing\\CoronaTracing\\Keys";
	}

	public void readFile() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		logger.info("Started reading the database file.");
		// read the database file
		Scanner sc = new Scanner(dbFile);
		sc.nextLine();
		String line = sc.nextLine();
		while (!line.equals("#Users")) {
			String[] info = line.split("_");
			CateringFacility cf = new CateringFacility(info[0], info[1], info[2], info[3]);
			cf.setSecretKey(new SecretKeySpec(info[4].getBytes(), "AES"));
			for (int i = 5; i < info.length; i += 2) {
				cf.addHashes(Instant.parse(info[i]), info[i + 1].getBytes());
			}
			facilities.add(cf);
			line = sc.nextLine();
		}
		line = sc.nextLine();
		FileInputStream sigfig = null;
		File sig = null;
		while (!line.equals("#End")) {
			String[] info = line.split("_");
			User user = new User(info[1], info[2], info[0]);
			for (int i = 3; i < info.length; i++) {
				Instant date = Instant.parse(info[i]);
				ArrayList<byte[]> tokens = new ArrayList<>();
				for (int j = 0; j < 48; j++) {
					sig = new File("files\\" + user.toString() + "\\" + info[i].substring(0, 10) + "_" + j);
					sigfig = new FileInputStream("files\\" + user.toString() + "\\" + info[i].substring(0, 10) + "_" + j);
					byte[] signature = new byte[(int) sig.length()];
					sigfig.read(signature);
					sigfig.close();
					tokens.add(signature);
				}
				user.addTokens(date, tokens);
			}
			users.add(user);
			line = sc.nextLine();
		}
		sc.close();
	}

	public void printFile() throws IOException {
		PrintWriter pw = new PrintWriter(dbFile);
		pw.println("#Facilities");
		for (CateringFacility cf : facilities) {
			pw.println(cf.toString());
		}
		pw.println("#Users");
		FileOutputStream sigfos = null;
		File sigfile = null;
		for (User u : users) {
			pw.print(u.toString());
			Map<Instant, ArrayList<byte[]>> tokens = u.getTokens();
			for (Map.Entry<Instant, ArrayList<byte[]>> entry : tokens.entrySet()) {
				sigfile = new File("files\\" + u.toString());
				if (!sigfile.exists()) {
					sigfile.mkdir();
				}
				int i = 0;
				// Store signatures in files
				pw.println("_" + entry.getKey().toString());
				for (byte[] token : entry.getValue()) {
					sigfos = new FileOutputStream(
							"files\\" + u.toString() + "\\" + entry.getKey().toString().substring(0, 10) + "_" + i);
					sigfos.write(token);
					sigfos.close();
					i++;
				}
			}
		}
		pw.println("#End");
		pw.flush();
		pw.close();
	}

	public CateringFacility findCateringFacility(String business, String phoneNumber) {
		for (CateringFacility cf : facilities) {
			if (cf.getBusinessNumber().equals(business) && cf.getPhoneNumber().equals(phoneNumber))
				return cf;
		}
		return null;
	}

	public void addCateringFacility(CateringFacility cf) {
		synchronized (facilities) {
			facilities.add(cf);
		}
	}

	public User findUser(String phoneNumber) {
		for (User u : users) {
			if (u.getPhoneNumber().equals(phoneNumber))
				return u;
		}
		return null;
	}

	public void addVisitor(User u) {
		synchronized (users) {
			users.add(u);
		}
	}
}
