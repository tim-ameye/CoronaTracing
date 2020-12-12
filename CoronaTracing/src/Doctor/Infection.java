package Doctor;

import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;

import Visitor.Visit;

public class Infection {
	private ArrayList<String> signedVisits;
	private ArrayList<String> unsignedVisits;
	Signature signature;
	
	public Infection(Doctor doctor) {
		this.signedVisits = new ArrayList<>();
		this.unsignedVisits = new ArrayList<>();
		try {
			signature.initSign(doctor.getKeyPair().getPrivate());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	
}
