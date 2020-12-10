package matchingServer;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mixingProxy.Capsule;

public class MatchingService extends UnicastRemoteObject implements MatchingServiceInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6919696315736336452L;

	
	
	private Map<String, List<Record> > cateringFacilities;		// key identifier catering facility and contains list of days 
	Database database;
	
	public MatchingService(Database db) throws RemoteException{
		cateringFacilities = new HashMap<>();
	//	Database database = new Database(dbFile)
	//TODO vraag aan tim: te fuk moet ik hier met die db werken !?
	}
	public void RecieveCapsule(Capsule capsule) {
		// disect the capsule and get it in the format of
		// a record and add it to the records array of which we 
		// also have a database
		Date date = new Date(System.currentTimeMillis());
		Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		String currentDayString = currentDay.toString().substring(0, 10);
		
		//TODO Step one decrypt the capsule
		
		// Step 2 get the information out of the capsule
		String userToken = capsule.getUsertoken();
		String qrToken = capsule.getQrToken();
		Instant capsuleTimeInterval = capsule.getCurrentTimeInterval();
		
		// get the cateringfacility identifiers out of that token
		String[] spittedQrToken = qrToken.split("_");
		
		String cfToken = spittedQrToken[3];
		//TODO before using the cateringFacilities map we should first synchronise it with our files by running a read command :p
		File dbFile = new File("MatchingService\\"+cfToken);
		if (!dbFile.exists()) {
			dbFile.mkdirs();
		}
		
		
		// see if this cateringFacility is already in our matchingService database
		if (cateringFacilities.containsKey(cfToken)) {
			// iterate over it's records and search for matching time interval
			List<Record> records = cateringFacilities.get(cfToken);
		
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
			
			cateringFacilities.put(cfToken, records);
			
		}
		//TODO normally is now the cateringFacilities Map updated and so we should reprint it so it's kept up to date
		
	}
	
}
