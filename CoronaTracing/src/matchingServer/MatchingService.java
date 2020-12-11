package matchingServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Visitor.Visit;
import mixingProxy.Capsule;
import registrar.RegistrarInterface;

public class MatchingService extends UnicastRemoteObject implements MatchingServiceInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6919696315736336452L;

	List<byte[]> allTokens;
	private Map<String, List<Record>> matchingService; // key identifier catering facility and contains list of days
	Database database;

	public MatchingService() throws RemoteException, FileNotFoundException {
		matchingService = new HashMap<>();
		allTokens = new ArrayList<>();
		Database database = new Database("MatchingService\\Database.txt");
		database.readFile();
		System.out.println("[DATABASE] Initialised!");

		// TODO vraag aan tim: welke db wordt hier meegegeven, moet ik deze hier
		// gebruiken!?
	}

	public void RecieveCapsule(Capsule capsule) throws FileNotFoundException {
		// disect the capsule and get it in the format of
		// a record and add it to the records array of which we
		// also have a database
		Date date = new Date(System.currentTimeMillis());
		Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		String currentDayString = currentDay.toString().substring(0, 10);

		// TODO Step one decrypt the capsule

		// Step 2 get the information out of the capsule
		String userToken = capsule.getUserTokenSigned();
		String cfToken = capsule.getQrToken();
		Instant capsuleTimeInterval = capsule.getCurrentTimeInterval();

		System.out.println("[MATCHINGSERVICE] recieving capusle with usertoken " + userToken + " and qrToken " + cfToken
				+ " and time interval " + capsuleTimeInterval);

		// get the cateringfacility identifiers out of that token

		// before using the cateringFacilities map we should first synchronise it with
		// our files by running a read command :p
		File dbFile = new File("MatchingService\\" + cfToken);
		if (!dbFile.exists()) {
			dbFile.mkdirs();
		}
		// Synchronise our
		System.out.println("[MATCHINGSERVICE] Synchronising database and adding the capsule...");
		database.readFile();
		matchingService = database.getMatchingService();

		// see if this cateringFacility is already in our matchingService database
		if (matchingService.containsKey(cfToken)) {
			// iterate over it's records and search for matching time interval
			List<Record> records = matchingService.get(cfToken);

			boolean match = false;
			for (Record r : records) {
				if (r.getTime() == capsuleTimeInterval) {
					match = true;
					// adding our user token to this record
					r.getTokens().add(userToken);
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
		database.setMatchingService(matchingService);
		database.printFile();
		System.out.println("[MATCHINGSERVICE] Database succesfully synchronised and the caplsule was added.");

	}

	public void RecieveInfectedUserToken(List<Visit> infectedVisits)
			throws FileNotFoundException, NoSuchAlgorithmException {

		for (int i = 0; i < infectedVisits.size(); i++) {
			Visit visit = infectedVisits.get(i);

			// synchronise database
			database.readFile();
			matchingService = database.getMatchingService();

			// step 1: make sure we were sent valid information!
			if (checkValid(visit.getCateringFacilityToken(), visit.getRandomNumber())) {
				System.out.println("[Matchingservice] The token from the visit was valid!");

				System.out.println("[Matchingservice] So everyone in this record should be contacted!");

				if (matchingService.containsKey(visit.getCateringFacilityToken())) {
					List<Record> records = matchingService.get(visit.getCateringFacilityToken());
					Record record = null;
					boolean found = false;
					for (int j = 0; j < records.size(); j++) {
						if (records.get(i).getTime().equals(visit.getInstant())) {
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
					} else {
						System.out.println(
								"[Matchingservice] No records for this catering facility with the corresponding time, strange...");
					}
				} else {
					System.out.println("[Matchingservice] No records for this catering facility, strange...");
				}
			} else {
				System.out.println("[MatchingService] The token from the visit was invalid! Problem");
			}
		}
		database.printFile();

	}


	private boolean checkValid(String cateringFacilityToken, int randomNumber) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		// Hash all items in our list with this random integer and check if it's equal
		// to our cateringfacilityToken
		for (int i = 0; i < allTokens.size(); i++) {
			// hash the current token with the Ri
			String currentToken = Base64.getEncoder().encodeToString(allTokens.get(i));
			String currentTokenString = Integer.toString(randomNumber) + currentToken;

			byte[] nym = md.digest(currentTokenString.getBytes());
			String currentHashString = Base64.getEncoder().encodeToString(nym);

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

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void getNewTokens() throws RemoteException, NotBoundException {
		Registry myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		RegistrarInterface registrar = (RegistrarInterface) myRegistry.lookup("Registrar");

		allTokens.addAll(registrar.getCfHashesFromToday());

	}

}
