package matchingServer;

import java.time.Instant;
import java.util.ArrayList;

public class Record {

	private byte[] hash;
	private Instant time;
	private ArrayList<byte[]> tokens;
	
	public Record(byte[] hash, Instant time) {
		this.hash = hash;
		this.time = time;
		this.tokens = new ArrayList<>();
	}

	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}

	public ArrayList<byte[]> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<byte[]> tokens) {
		this.tokens = tokens;
	}
	
	public void addToken(byte[] token) {
		tokens.add(token);
	}
}
