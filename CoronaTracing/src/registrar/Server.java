package registrar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.logging.Level;


public class Server{

	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("Registrar"); 
		Scanner sc = new Scanner(System.in);
		Registry registry = null;
		String dbFile = "files\\Registrar.txt";
		Database db = new Database(dbFile);
		
		String input = "";
		while(!input.equals("Stop")) {
			if(input.equals("Start")) {
				logger.info("A database has been initialised with file name: " + dbFile);
				try {
					db.readFile();
				} catch (FileNotFoundException e1) {
					logger.log(Level.SEVERE, "The db-file has not been found! " + e1.getLocalizedMessage());
					e1.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					registry = LocateRegistry.createRegistry(55545);
					registry.rebind("Registrar", new Registrar(db));
				} catch (Exception e) {
					logger.log(Level.SEVERE, "System has failed to start: "+ e.getLocalizedMessage());
					e.printStackTrace();
				}
			} else if(input.equals("Save")) {
				try {
					db.printFile();
					System.out.println("[Registrar] The database has been updated.");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(input.equals("Expired")) {
				db.removeOutOfDateHashes(10);
				db.removeOutOfDateHashes(10);
				System.out.println("[Registrar] Out of date hashes and tokens have been removed. Save to put the changes in the files.");
			}
			input = sc.nextLine();
		}
		try {
			db.printFile();
			registry.unbind("Registrar");
			sc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
