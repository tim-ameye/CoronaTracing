package matchingServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import Doctor.Infection;
import Visitor.Visit;
import mixingProxy.Acknowledge;
import mixingProxy.Capsule;
import mixingProxy.MixingProxy;
import mixingProxy.MixingProxy.Send;
import registrar.RegistrarInterface;
import registrar.TokenList;

public class MatchingService extends UnicastRemoteObject implements MatchingServiceInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6919696315736336452L;

	private List<String> allTokens;
	private Map<String, List<Record>> matchingService; 	// key identifier catering facility and contains list of days
	private Map<String, List<Record>> criticalRecordsOfToday;		// contains string: hashedCFToken_instant
	private Database database;
	private String currentHashString;
	private PrivateKey privateKey;
	private SecureRandom secureRandom;
	private KeyGenerator keyGenerator;
	private RegistrarInterface registrar;
	private PublicKey registrarPubKey;
	private KeyStore keyStore;
	private PublicKey doctorPubKey;
	private final static String path = "files\\keystore.jks";

	public MatchingService() throws RemoteException, FileNotFoundException, NoSuchAlgorithmException, NotBoundException, KeyStoreException {
		matchingService = new HashMap<>();
		criticalRecordsOfToday = new HashMap<>();
		allTokens = new ArrayList<>();
		File file = new File("files\\MatchingService");
		if(!file.exists()) {
			file.mkdir();
		}
		database = new Database("files\\MatchingService\\Database.txt");
		database.readFile();
		matchingService = database.getMatchingService();
		criticalRecordsOfToday = database.getCriticals();
		System.out.println("[DATABASE] Initialised!  Type 'Start' to run the server.");
		
		Registry myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		registrar = (RegistrarInterface) myRegistry.lookup("Registrar");
		
		this.secureRandom = new SecureRandom();
		this.keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256, secureRandom);

		try {
			keyStore = KeyStore.getInstance("JKS");
			char[] password = "AVB6589klp".toCharArray();
			FileInputStream fis = new FileInputStream("files\\keystore.jks");
			keyStore.load(fis, password);
			
			privateKey = (PrivateKey) keyStore.getKey("matchingservice", password);
			registrarPubKey = keyStore.getCertificate("registrar").getPublicKey();
			doctorPubKey = keyStore.getCertificate("doctor").getPublicKey();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getNewTokens();
		
	}

	public void sendCapsule(Capsule capsule) throws FileNotFoundException, RemoteException {
		// disect the capsule and get it in the format of
		// a record and add it to the records array of which we
		// also have a database
		Date date = new Date(System.currentTimeMillis());
		Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		String currentDayString = currentDay.toString().substring(0, 10);

		// Step 1 decrypt the capsule
		Capsule cap = capsule.Decrypt(privateKey);
		// Step 2 get the information out of the capsule
		String userToken = cap.getUserTokenSigned();
		String cfToken = cap.getQrToken();
		Instant capsuleTimeInterval = cap.getCurrentTimeInterval();

		System.out.println("[MATCHINGSERVICE] recieving capusle with usertoken " + userToken + " and qrToken " + cfToken
				+ " and time interval " + capsuleTimeInterval);

		// get the cateringfacility identifiers out of that token

		// before using the cateringFacilities map we should first synchronise it with
		// our files by running a read command :p
		
		// Synchronise our
		System.out.println("[MATCHINGSERVICE] Synchronising database and adding the capsule...");	

		// see if this cateringFacility is already in our matchingService database
		if (matchingService.containsKey(cfToken)) {
			// iterate over it's records and search for matching time interval
			List<Record> records = matchingService.get(cfToken);

			boolean match = false;
			for (Record r : records) {
				if (r.getTime().equals(capsuleTimeInterval)) {
					match = true;
					// adding our user token to this record
					r.addToken(userToken);
				}
			}
			if (!match) {
				// no match found -> create new record, add the usertoken to this and add it to
				// these records
				Record record = new Record(cfToken, capsuleTimeInterval);
				record.addToken(userToken);

				records.add(record);

			}

		} else {
			// create entry in the map
			List<Record> records = new ArrayList<>();
			Record temp = new Record(cfToken, capsuleTimeInterval);
			temp.addToken(userToken);
			records.add(temp);

			matchingService.put(cfToken, records);

		}
		// normally is now the cateringFacilities Map updated and so we should reprint
		// it so it's kept up to date
		System.out.println("[MATCHINGSERVICE] Database succesfully synchronised and the capsule was added.");

	}

	public void recieveInfectedUserToken(Infection infection) throws FileNotFoundException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		//TODO Step 1 decrypt this object
		Infection inf = infection.decrypt(privateKey);
		// Step 2 check if this object is signed by a doctor
		List<String> infectedVisits = inf.getUnsignedVisits();
		List<String> signedInfectedVisits = inf.getSignedVisits();
		
		Signature sig = Signature.getInstance("SHA512withRSA");
		sig.initVerify(doctorPubKey);
		sig.update(infectedVisits.get(0).getBytes());
		
		boolean b = sig.verify(Base64.getDecoder().decode(signedInfectedVisits.get(0)));
		if (!b) {
			System.out.println("[Matchingservice] Token not valid!");
			return;
		}
		
		for (int i = 0; i < infectedVisits.size(); i++) {
			String entry = infectedVisits.get(i);
			String[] entrySplitted = entry.split("_");
			
			String randomNumber = entrySplitted[0];
			String userTokenSigned = entrySplitted[1];
			String userTokenUnsigned = entrySplitted[2];
			String cfToken = entrySplitted[3];
			String instant = entrySplitted[4];


			Visit visit = new Visit(Integer.parseInt(randomNumber), userTokenSigned, userTokenUnsigned, cfToken);
			visit.setBeginTime(Instant.parse(instant));
			//TODO add a time to that visit ey

			// synchronise database
			database.readFile();
			matchingService = database.getMatchingService();
			criticalRecordsOfToday = database.getCriticals();

			// step 1: make sure we were sent valid information!
			if(allTokens.isEmpty())
				try {
					getNewTokens();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (checkValid(visit.getCateringFacilityToken(), visit.getRandomNumber())) {
				System.out.println("[Matchingservice] The token from the visit was valid!");

				System.out.println("[Matchingservice] So everyone in this record should be contacted!");

				if (matchingService.containsKey(visit.getCateringFacilityToken())) {
					List<Record> records = matchingService.get(visit.getCateringFacilityToken());
					Record record = null;
					boolean found = false;
					for (int j = 0; j < records.size(); j++) {
						if (records.get(i).getTime().equals(visit.getBeginTime())) {
							found = true;
							record = records.get(i);
						}
					}
					if (found) {
						// we now have the record which contains all the information
						record.setCritical(true);
						for (int j = 0; j < record.getTokens().size(); j++) {
							if (record.getTokens().get(j).equals(visit.getUserTokenSigned())) {
								record.getInformed().set(j, true);
							}
						}
						// adding this record to the is critical
						String criticalRecord = currentHashString + "_" +visit.getBeginTime().toString();
						if(criticalRecordsOfToday.containsKey(visit.getCateringFacilityToken())) {
							criticalRecordsOfToday.get(visit.getCateringFacilityToken()).add(record);
						} else {
							List<Record> temp = new ArrayList<>();
							temp.add(record);
							criticalRecordsOfToday.put(visit.getCateringFacilityToken(), temp);
						}
						Thread send = new Thread(new Send(this, visit.getCateringFacilityToken(), visit.getBeginTime()));
						send.start();
						// Starting countdown timer that will check if users are informed or not.
						System.out.println("start countdown");
						new Countdown(1296000, this, visit.getCateringFacilityToken(), visit.getBeginTime());	
						//give record so we can see how the record is doing 
						System.out.println("passed it");
						
					} else {
						System.out.println(
								"[Matchingservice] No records for this catering facility with the corresponding time.");
					}
				} else {
					System.out.println("[Matchingservice] No records for this catering facility.");
				}
			} else {
				System.out.println("[MatchingService] The token from the visit was invalid!");
			}
		}
		database.setCriticals(criticalRecordsOfToday);
		database.setMatchingService(matchingService);
		database.printFile();

	}
	
public class Send extends Thread {
		
		MatchingService mp;
		String cfToken;
		Instant i;
		
		public Send(MatchingService mp, String cfToken, Instant i) {
			this.mp = mp;
			this.cfToken = cfToken;
			this.i = i;
		}
		
		private volatile boolean exit = false;

		public void run() {
			long current = System.currentTimeMillis();
			long start = System.currentTimeMillis();
			while(!exit) {
				if(current > start +3000000) {
					start = System.currentTimeMillis();
					try {
						mp.contactUsers(cfToken, i);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					};
					exit = true;
				}
				current = System.currentTimeMillis();
			}
		}
		
		public void stopThread() {
			exit = true;
		}
			
	}

	private boolean checkValid(String cateringFacilityToken, int randomNumber) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		// Hash all items in our list with this random integer and check if it's equal
		// to our cateringfacilityToken
		for (int i = 0; i < allTokens.size(); i++) {
			// hash the current token with the Ri
			//TODO moet niet meer in een string omgezet worden!!
			String currentTokenString = Integer.toString(randomNumber) + allTokens.get(i);

			byte[] nym = md.digest(currentTokenString.getBytes());
			currentHashString = Base64.getEncoder().encodeToString(nym);

			if (currentHashString.equals(cateringFacilityToken)) {
				// our hashes match so it's valid!
				return true;
			}

		}

		return false;
	}

	public Map<String, List<Record>> getMatchingService() {
		return matchingService;
	}

	public void setMatchingService(Map<String, List<Record>> matchingService) {
		this.matchingService = matchingService;
	}

	public Database getDatabase() {
		return database;
	}

	public TokenList getCriticalRecordsOfToday(PublicKey publicKey) {
		
		
		TokenList encrypted = new TokenList();
		for (Entry<String, List<Record>> entry: criticalRecordsOfToday.entrySet()) {
			for(Record rec: entry.getValue()) {
				encrypted.add(entry.getKey() + "_" + rec.getTime().toString());
			}
		}
		SecretKey sessionKey = keyGenerator.generateKey();
		TokenList temp = encrypted.encrypt(sessionKey, publicKey);
		
		return temp;
	}

	public void setCriticalRecordsOfToday(Map<String, List<Record>> criticalRecordsOfToday) {
		this.criticalRecordsOfToday = criticalRecordsOfToday;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	
	
	public void getNewTokens() throws RemoteException, NotBoundException {
		Registry myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		RegistrarInterface registrar = (RegistrarInterface) myRegistry.lookup("Registrar");
		TokenList ans = registrar.getCfHashesFromToday();
		TokenList list = ans.decryt(privateKey);
		allTokens.addAll(list.getTokens());
		
	}

	void contactUsers(String cfToken, Instant instant) throws FileNotFoundException, RemoteException, NotBoundException {
		Record criticRecord = null;
		if(matchingService.containsKey(cfToken)) {
			List<Record> record = matchingService.get(cfToken);
			boolean found = false;
			for (Record r : record) {
				if (r.getTime().equals(instant)) {
					found = true;
					criticRecord = r;
				}
			}
			if (found) {
				// check which users did not yet get informed and contact them by using the registrar
				List<Boolean> informed = criticRecord.getInformed();
				List<String> userTokens = criticRecord.getTokens();

				List<String> notInformed = new ArrayList<>();
				
				for (int i = 0; i < informed.size(); i++) {
					if (!informed.get(i)) {
						notInformed.add(userTokens.get(i));
						
					}
				}
				criticRecord.setAllInformed();
				if (notInformed.size() > 0) {
					System.out.println("[Matchingservice] Trying to contact users who came into contact with an infected person, but were not informed yet");
					
					// convert notInformed into a tokenList that is encrypted
					TokenList temp = new TokenList();
					for (int i = 0; i < notInformed.size(); i++) {
						temp.add(notInformed.get(i));
					}
					
					SecretKey sessionKey = keyGenerator.generateKey();
					TokenList encrypted = temp.encrypt(sessionKey, registrarPubKey);
				
					//TODO
					registrar.InformUsers(encrypted);

					System.out.println("[Matchingservice] Users were succesfully contacted!");

				}else {
					System.out.println("[Matchingservice] All users were already update about their contact with an infected person, very nice! ");
				}
								
				
				
			}else {
				System.out.println("[Matchingservice] ERROR");
			}
		}else {
			System.out.println("[Matchingservice] ERROR.");
		}
		
	}

	@Override
	public void testConnection(String text) throws RemoteException {
		System.out.println("[Matching Service] " + text);
		
	}

	@Override
	public void sendAck(Acknowledge ack) throws FileNotFoundException {
		// step one decrypt this acknowlegde	
		Acknowledge acknowledge = ack.decrypt(privateKey);
		
		// search the matching record and set the user that was send with this ack on informed!
		database.readFile();
		matchingService  = database.getMatchingService();
		criticalRecordsOfToday = database.getCriticals();
		
		if (matchingService.containsKey(acknowledge.getQrToken())) {
			List<Record> records = matchingService.get(acknowledge.getQrToken());
			boolean foundRecord = false;
			boolean foundPerson = false;
			for (Record r : records) {
				if (r.getTime().toString().equals(acknowledge.getInstant().toString()) ) {
					foundRecord = true;
					List<String> userTokens = r.getTokens();
					
					for (int i = 0; i < userTokens.size(); i++) {
						if (userTokens.get(i).equals(acknowledge.getUserTokenSigned())) {
							foundPerson = true;
							r.getInformed().set(i, true);
							System.out.println("We zijn er");
						}
					}
					
				}
			}
			if (!foundRecord) {
				System.out.println("[Matchingservice] We did not find the record that was send with our acknowledge in our database!");
			}
			if (!foundPerson) {
				System.out.println("[Matchingservice] We did not find the user in our record that was send with our acknowledge in our database!");
			}
		}else {
			System.out.println("[Matchingservice] We did not find the cfToken that was send with our acknowledge in our database!");
		}
		database.setCriticals(criticalRecordsOfToday);
		database.setMatchingService(matchingService);
		database.printFile();
		
		
	}





}
