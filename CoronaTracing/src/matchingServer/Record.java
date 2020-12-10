package matchingServer;

import java.time.Instant;
import java.util.ArrayList;

public class Record {

	private String cfHash; 				// hash from the cateringfacility
	private Instant time;
	private ArrayList<String> tokens;	// the users at that time in the cf
	
	public Record(String hash, Instant time) {
		this.cfHash = hash;
		this.time = time;
		this.tokens = new ArrayList<>();
	}

	public String getHash() {
		return cfHash;
	}

	public void setHash(String hash) {
		this.cfHash = hash;
	}

	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}

	public ArrayList<String> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<String> tokens) {
		this.tokens = tokens;
	}
	
	public void addToken(String token) {
		tokens.add(token);
	}
}
