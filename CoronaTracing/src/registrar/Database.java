package registrar;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Database {
	
	private ArrayList<User> users;
	private ArrayList<CateringFacility> facilities;
	private File dbFile;
	private Logger logger;
	
	public Database(String dbFileName) {
		dbFile = new File(dbFileName);
		users = new ArrayList<>();
		facilities = new ArrayList<>();
		logger = Logger.getLogger("Database");
	}
	
	public void readFile() {
		
		logger.info("Started reading the database file.");
		//read the database file
		
	}
}
