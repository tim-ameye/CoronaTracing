package registrar;

import java.io.FileNotFoundException;
import java.rmi.registry.*;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.logging.Level;


public class Server{

	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("Registrar"); 
		
		String dbFile = "Registrar.txt";
		Database db = new Database(dbFile);
		logger.info("A database has been initialised with file name: " + dbFile);
		try {
			db.readFile();
		} catch (FileNotFoundException e1) {
			logger.log(Level.SEVERE, "The db-file has not been found! " + e1.getLocalizedMessage());
			e1.printStackTrace();
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
