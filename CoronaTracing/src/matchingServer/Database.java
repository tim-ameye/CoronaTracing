package matchingServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Logger;

import cateringFacility.CateringFacility;

public class Database {

	ArrayList<Record> records;
	private File dbFile;
	private Logger logger;
	private Map<String, List<Record> > matchingService;		// key identifier catering facility and contains list of days 

	//
	// we're going to work with the same map from matchingService but that shouldn't matter too much
	// We should have for each entry in the map a corresponding file with as name the key
	// and than maybe another abstraction layer for the day, but not sure if that's really necessary.
	// and we should than always have a file for eacht time instance which contains all of the user tokens
	// 
	public Database(String dbFileName) {
		dbFile = new File(dbFileName);
		records = new ArrayList<>();
		logger = Logger.getLogger("Database");
		matchingService = new HashMap<>();
	}
	//TODO not yet tested these methods, but think should work with a few tweeks
	public void readFile() throws FileNotFoundException {
		logger.info("Started reading the database file.");
		// read the database file
		Scanner sc = new Scanner(dbFile);
		FileInputStream sigfig = null;
		File sig = null;
		sc.nextLine();
		String line = sc.nextLine();
		while (!line.equals("#End")) {
			//TODO controleren of deze 2 waarden goed ingelezen worden!
			String cfToken = line;
			line = sc.nextLine();
			String[] timeIntervals = line.split("_");
			// initiate our map before filling it! 
			List<Record> records = new ArrayList<>();
			matchingService.put(cfToken, records);
			
			for(int i = 1; i < timeIntervals.length; i++) {
				Instant date = Instant.parse(timeIntervals[i]);
				String time = timeIntervals[i].toString();
				String day = time.substring(0,10);
				String hour = time.substring(11, 13);
				String min = time.substring(14,16);
				Scanner fsc = new Scanner(new File("files\\MatchingService\\"+cfToken+"\\" + day + "_" + hour+"_"+ min + ".txt"));
				List<String> userTokens = new ArrayList<>();
				List<Boolean> informed = new ArrayList<>();
				
				// what is de first line?
				String firstLine = fsc.nextLine();
				boolean critical = false;
				if (firstLine.equals("CRITICAL")) {
					critical = true;
				}
				while (fsc.hasNextLine()) {
					String current = fsc.nextLine();
					String[] splitLine = current.split("_");
					userTokens.add(splitLine[0]);
					informed.add(Boolean.parseBoolean(splitLine[1]));
					
				}
				// Alle waarde uit de timeinterval[i] van cf met cfToken gehaald, nu deze info in onze map steken!
				List<Record> currentRecords = matchingService.get(cfToken);
				
				Record r = new Record(cfToken, date);
				for (int j = 0; j < userTokens.size(); j++) {
					// adding all the usertokens from the file
					r.addToken(userTokens.get(j));
					r.setInformed(informed);
					if (critical) {
						r.setCritical(true);
					}else {
						r.setCritical(false);
					}
				}
				
				
			}
			line = sc.nextLine();
		}					
		sc.close();
	}
	
	public void printFile() throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(dbFile);
		PrintWriter fpw;
		File sigfile = null;
		pw.println("#Records");
		
		// iterate over all cfToken - record list pairs
		for (Entry<String, List<Record>> entry : matchingService.entrySet() ) {
			String cfToken = entry.getKey().toString();
			pw.println(cfToken); 	// Key should be cfToken
			
			//make sure the file with in MatchingService with name cfToken is there
			sigfile = new File("files\\MatchingSerivce\\" + cfToken);
			if (!sigfile.exists()) {
				System.out.println("Make directory");
				sigfile.mkdir();
			}
			List<Record> records = entry.getValue();
			boolean first = true;
			for (Record r : records) {
				String time = r.getTime().toString();
				String day = time.substring(0,10);
				String hour = time.substring(11, 13);
				String min = time.substring(14,16);
				//TODO not sure if printwriter will actually make the file itself, i hope so, but if there is an error here, yeah..
				File file = new File("files\\MatchingService\\" + cfToken + "\\" + day + "_" + hour+"_"+ min + ".txt");
				fpw = new PrintWriter(file);
				//first line is the indicator for critical or not
				String firstLine = "";
				if (r.isCritical()) {
					firstLine = "CRITICAL";
				}else {
					firstLine = "safe";
				}
				fpw.println(firstLine);
				
				fpw.println(getTokens(r));	// in the file set all the tokens
				if(first ) {
					pw.print(r.getTime());
					first = false;
				}
				pw.print("_" + r.getTime());	//In the database file, write all the keys aka different timeInstances
				
				fpw.flush();
				fpw.close(); // i think i need to close here 
			}
		pw.println();
		pw.println("#End");
		pw.flush();
		
		pw.close();
		}
	}
	
	private String getTokens(Record r) {
		String tokensString = "";
		List<String> userTokens = r.getTokens();
		List<Boolean> informed = r.getInformed();
		
		tokensString = userTokens.get(0) + "_" + informed.get(0);
		for (int i = 1; i < userTokens.size(); i++) {
			tokensString = tokensString + '\n' + userTokens.get(i) + "_" +informed.get(i);
		}
		return tokensString;
	}
	
	public Map<String, List<Record>> getMatchingService() {
		return matchingService;
	}

	public void setMatchingService(Map<String, List<Record>> matchingService) {
		this.matchingService = matchingService;
	}



}
