package matchingServer;

import java.io.File;
import java.util.ArrayList;

public class Database {

	ArrayList<Record> records;
	
	//TODO
	// we're going to work with the same map from matchingService but that shouldn't matter too much
	// We should have for each entry in the map a corresponding file with as name the key
	// and than maybe another abstraction layer for the day, but not sure if that's really necessary. //TODO ask tim x
	// and we should than always have a file for eacht time instance which contains all of the user tokens
	// 
	public Database(String dbFile) {
		
	}
	
	public void readFile() {
		
	}
	
	public void printFile() {
		
	}
}
