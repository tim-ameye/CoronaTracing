package registrar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Logger;

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

	public void readFile() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		logger.info("Started reading the database file.");
		// read the database file
		Scanner sc = new Scanner(dbFile);
		Scanner fileScanner = null;
		if (sc.hasNext()) {
			FileInputStream sigfig = null;
			File sig = null;
			sc.nextLine();
			String line = sc.nextLine();
			while (!line.equals("#Users")) {
				String[] info = line.split("_");
				CateringFacility cf = new CateringFacility(info[0], info[1], info[2], info[3]);
				for (int i = 4; i < info.length; i++) {
					Instant date = Instant.parse(info[i]);
					sig = new File(
							"files\\catering\\" + cf.toStringFileName() + "\\" + info[i].toString().substring(0, 10));
					sigfig = new FileInputStream(
							"files\\catering\\" + cf.toStringFileName() + "\\" + info[i].toString().substring(0, 10));
					byte[] token = new byte[(int) sig.length()];
					sigfig.read(token);
					cf.addHashes(date, token);
				}
				facilities.add(cf);
				line = sc.nextLine();
			}
			line = sc.nextLine();
			while (!line.equals("#End")) {
				String[] info = line.split("_");
				User user = new User(info[1], info[2], info[0]);
				for (int i = 3; i < info.length; i++) {
					Instant date = Instant.parse(info[i]);
					Token token = new Token();
					token.setDay(date);
					fileScanner = new Scanner("files\\users\\" + user.toString() + "\\" + info[i].substring(0, 10) + ".txt");
					while(fileScanner.hasNextLine()) {
						String tokenLineSigned = fileScanner.nextLine();
						String tokenLineUnsigned = fileScanner.nextLine();
						token.addToken(tokenLineSigned, tokenLineUnsigned);
					}
					fileScanner.close();
					user.addTokens(token);
				}
				users.add(user);
				line = sc.nextLine();
			}
		}
		sc.close();
	}

	public void printFile() throws IOException {
		PrintWriter pw = new PrintWriter(dbFile);
		FileOutputStream sigfos = null;
		PrintWriter fileWriter = null;
		File sigfile = null;
		pw.println("#Facilities");
		for (CateringFacility cf : facilities) {
			pw.println(cf.toString());
			sigfile = new File("files\\catering\\" + cf.toStringFileName());
			if (!sigfile.exists()) {
				sigfile.mkdirs();
			}
			String[] filesArray = sigfile.list();
			List<String> files = Arrays.asList(filesArray);
			Map<Instant, byte[]> tokens = cf.getHashes();
			for (Map.Entry<Instant, byte[]> entry : tokens.entrySet()) {
				if (files.contains(entry.getKey().toString().substring(0, 10))) {
					files.remove(entry.getKey().toString().substring(0, 10));
				}
				sigfos = new FileOutputStream("files\\catering\\" + cf.toStringFileName() + "\\"
						+ entry.getKey().toString().substring(0, 10));
				sigfos.write(entry.getValue());
				sigfos.close();
			}
			for (String file : files) {
				File remove = new File(sigfile.getPath(), file);
				remove.delete();
			}
		}
		pw.println("#Users");
		for (User u : users) {
			pw.print(u.toString());
			ArrayList<Token> outOfDate = u.getTokens();
			ArrayList<Token> tokens = u.getTokens();
			for (Token token: tokens) {
				sigfile = new File("files\\users\\" + u.toString());
				if (!sigfile.exists()) {
					sigfile.mkdirs();
				}
				outOfDate.remove(token);
				// Store signatures in files
				pw.println("_" + token.getDay().toString());
				for (int i = 0; i < token.getSignedTokens().size(); i++) {
					fileWriter = new PrintWriter("files\\users\\" + u.toString() + "\\"
							+ token.getDay().toString().substring(0, 10) + ".txt");
					fileWriter.println(token.getSignedTokens().get(i));
					fileWriter.println(token.getUnsignedTokens().get(i));
				}
				fileWriter.close();
			}
			for (Token token: outOfDate) {
				sigfile = new File("files\\users\\" + u.toString() + "\\" + token.getDay().toString().substring(0, 10) + ".txt");
				sigfile.delete();
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

	public User findUserWithToken(String infectedToken) {
		for (User user : users) {
			for (Token token: user.getTokens()) {
				if(token.contains(infectedToken)) {
					return user;
				}
			}
		}
		return null;
	}

	public CateringFacility findCateringWithHash(byte[] infected) {
		for (CateringFacility cf : facilities) {
			for (Entry<Instant, byte[]> entry : cf.getHashes().entrySet()) {
				if (infected.equals(entry.getValue()))
					return cf;
			}
		}
		return null;
	}

	public void removeOutOfDateTokens(int expirationTime) {
		Instant expirationDay = new Date(System.currentTimeMillis()).toInstant().minus(expirationTime, ChronoUnit.DAYS);
		for (User user : users) {
			ArrayList<Token> outOfDate = new ArrayList<>();
			for (Token token: user.getTokens()) {
				if (token.getDay().isBefore(expirationDay)) {
					outOfDate.add(token);
				}
			}
			for (Token token : outOfDate) {
				user.getTokens().remove(token);
			}
		}
	}

	public void removeOutOfDateHashes(int expirationTime) {
		Instant expirationDay = new Date(System.currentTimeMillis()).toInstant().minus(expirationTime, ChronoUnit.DAYS);
		for (CateringFacility cf : facilities) {
			ArrayList<Instant> outOfDate = new ArrayList<>();
			for (Entry<Instant, byte[]> entry : cf.getHashes().entrySet()) {
				if (entry.getKey().isBefore(expirationDay)) {
					outOfDate.add(entry.getKey());
				}
			}
			for (Instant instant : outOfDate) {
				cf.getHashes().remove(instant);
			}
		}
	}
}
