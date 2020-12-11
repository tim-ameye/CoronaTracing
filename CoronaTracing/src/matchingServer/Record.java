package matchingServer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Record {

	private String cfHash; 				// hash from the cateringfacility
	private boolean critical;
	private Instant time;
	private ArrayList<String> tokens;	// the users at that time in the cf
	private List<Boolean> informed;
	
	public Record(String hash, Instant time) {
		this.cfHash = hash;
		this.time = time;
		this.tokens = new ArrayList<>();
		this.informed = new ArrayList<>();
		this.critical = false;
	}

	public boolean isCritical() {
		return critical;
	}

	public void setCritical(boolean critical) {
		this.critical = critical;
	}

	public List<Boolean> getInformed() {
		return informed;
	}

	public void setInformed(List<Boolean> informed) {
		this.informed = informed;
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
		informed.add(false);
	}
}
