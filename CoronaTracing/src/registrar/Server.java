package registrar;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.logging.Logger;
import java.util.logging.Level;


public class Server {

	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("Registrar"); 
		
		String dbFile = "Registrar.txt";
		Database db = new Database(dbFile);
		logger.info("A database has been initialised with file name: " + dbFile);
		db.readFile();
		
		try {
			Registry registry = LocateRegistry.createRegistry(55545);
			
			registry.rebind("Registrar", new Registrar(db));
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "System has failed to start: "+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
