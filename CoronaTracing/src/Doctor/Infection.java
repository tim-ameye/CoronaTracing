package Doctor;

import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.SecretKey;

import Visitor.Visit;

public class Infection {

	private ArrayList<String> signedVisits;
	private ArrayList<String> unsignedVisits;
	Signature signature;
	
	public Infection() {
		this.signedVisits = new ArrayList<>();
		this.unsignedVisits = new ArrayList<>();
	}
	
	public void signSignature(Doctor doctor) {
		try {
			signature.initSign(doctor.getKeyPair().getPrivate());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getSignedVisits() {
		return signedVisits;
	}

	public void setSignedVisits(ArrayList<String> signedVisits) {
		this.signedVisits = signedVisits;
	}

	public ArrayList<String> getUnsignedVisits() {
		return unsignedVisits;
	}

	public void setUnsignedVisits(ArrayList<String> unsignedVisits) {
		this.unsignedVisits = unsignedVisits;
	}

	public void add(Visit visit) {
		unsignedVisits.add(visit.toExtendedString());
		byte[] visitByte = visit.toExtendedString().getBytes();
		try {
			signature.update(visitByte);
			byte[] signed = signature.sign();
			signedVisits.add(Base64.getEncoder().encodeToString(signed));
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Infection encrypt(SecretKey sessionKey, PublicKey pk) {
		Infection encrypted = new Infection();
		Cipher 
		
	}
	
	
	
	
}
