package matchingServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mixingProxy.Capsule;

public class MatchingService extends UnicastRemoteObject implements MatchingServiceInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6919696315736336452L;

	
	
	private Map<String, List<Record> > matchingService;		// key identifier catering facility and contains list of days 
	Database database;
	
	public MatchingService() throws RemoteException, FileNotFoundException{
		matchingService = new HashMap<>();
		Database database = new Database("MatchingService\\Database.txt");
		database.readFile();
		System.out.println("[DATABASE] Initialised!");

	//TODO vraag aan tim: welke db wordt hier meegegeven, moet ik deze hier gebruiken!?
	}
	public void RecieveCapsule(Capsule capsule) throws FileNotFoundException {
		// disect the capsule and get it in the format of
		// a record and add it to the records array of which we 
		// also have a database
		Date date = new Date(System.currentTimeMillis());
		Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		String currentDayString = currentDay.toString().substring(0, 10);
		
		//TODO Step one decrypt the capsule
		
		// Step 2 get the information out of the capsule
		String userToken = capsule.getUserTokenSigned();
		String cfToken = capsule.getQrToken();
		Instant capsuleTimeInterval = capsule.getCurrentTimeInterval();
		
		System.out.println("[MATCHINGSERVICE] recieving capusle with usertoken " + userToken +" and qrToken "+ cfToken + " and time interval "+ capsuleTimeInterval);

		// get the cateringfacility identifiers out of that token
		
		//before using the cateringFacilities map we should first synchronise it with our files by running a read command :p
		File dbFile = new File("MatchingService\\"+cfToken);
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
				//no match found -> create new record, add the usertoken to this and add it to these records
				Record record = new Record(cfToken, capsuleTimeInterval);
				record.addToken(userToken);
				
				records.add(record);
				
			}
			
		}else {
			//create entry in the map 
			List<Record> records = new ArrayList<>();
			Record temp = new Record(cfToken, capsuleTimeInterval);
			temp.addToken(userToken);
			records.add(temp);
			
			matchingService.put(cfToken, records);
			
		}
		//normally is now the cateringFacilities Map updated and so we should reprint it so it's kept up to date
		database.setMatchingService(matchingService);
		database.printFile();
		System.out.println("[MATCHINGSERVICE] Database succesfully synchronised and the caplsule was added.");

	}
	
	public void RecieveInfectedUserToken(List<String> infectedTokens) throws FileNotFoundException {
		// Input: a list of signed tokens that are from 1 user who has been infected
		
		//TODO get a list of all the user tokens that possibly came into contact with one of these infected tokens
		
		// synchronise database
		database.readFile();
		matchingService = database.getMatchingService();
		
		for (Entry<String, List<Record>> entry : matchingService.entrySet() ) {
			// get all the record for a certain cf
			List<Record> recordsOfCF = entry.getValue();
			
			for (Record record : recordsOfCF) {
				
			}
			
		}
		
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
	
}
