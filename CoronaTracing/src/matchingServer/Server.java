package matchingServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;

import registrar.Registrar;

public class Server {

	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner sc = new Scanner(System.in);
		Registry registry = null;
		
		String dbFile = "MachingService\\Database.txt";
		MatchingService matchingService = new MatchingService()
		
		String input = "";
		Date date = new Date(System.currentTimeMillis());
		Instant startDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		
		while(!input.equals("Stop")) {
			Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
			if (startDay.isBefore(currentDay)) {
				System.out.println("[MatchingService] We have begun a new day, ask the registrar for tokens");
				
				matchingService.getNewTokens();
			}
			if(input.equals("Start")) {
				System.out.println("[MatchingService] The database has been initialised.");
				
				
				
				try {
					registry = LocateRegistry.createRegistry(55547);
					registry.rebind("MatchingService", matchingService);
					
				} catch (Exception e) {
					System.out.println("[MatchingService] System has failed to start: "+ e.getLocalizedMessage());
					e.printStackTrace();
				}
			} else if(input.equals("Save")) {
				System.out.println("[MatchingService] The database has been updated, jk this command does absolutly noting.");
			} else if(input.equals("Expired")) {
				System.out.println("[MatchingService] Out of date hashes and tokens have been removed. Save to put the changes in the files.");
			}
			input = sc.nextLine();
		}
	}

}
