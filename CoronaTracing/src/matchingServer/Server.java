package matchingServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;
import java.util.logging.Level;

import registrar.Registrar;

public class Server {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		Registry registry = null;
		String dbFile = "files\\MatchingService.txt";
		Database db = new Database(dbFile);
		
		String input = "";
		while(!input.equals("Stop")) {
			if(input.equals("Start")) {
				System.out.println("[MatchingService] The database has been initialised.");
				
					db.readFile();
				
				
				try {
					registry = LocateRegistry.createRegistry(55547);
					registry.rebind("MatchingService", new MatchingService(db));
				} catch (Exception e) {
					System.out.println("[MatchingService] System has failed to start: "+ e.getLocalizedMessage());
					e.printStackTrace();
				}
			} else if(input.equals("Save")) {
				db.printFile();
				System.out.println("[MatchingService] The database has been updated.");
			} else if(input.equals("Expired")) {
				System.out.println("[MatchingService] Out of date hashes and tokens have been removed. Save to put the changes in the files.");
			}
			input = sc.nextLine();
		}
	}

}
