package registrar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.registry.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.logging.Level;


public class Server{

	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("Registrar"); 
		
		String dbFile = "files\\Registrar.txt";
		Database db = new Database(dbFile);
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
			Registry registry = LocateRegistry.createRegistry(55545);
			
			registry.rebind("Registrar", new Registrar(db));
			Scanner sc = new Scanner(System.in);
			String input = "";
			while(!input.equals("Stop")) {
				if(sc.hasNext()) {
					input = sc.nextLine();
				}
			}
			registry.unbind("Registrar");
			db.printFile();
			sc.close();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "System has failed to start: "+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	}
