package Doctor;

import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

public class Doctor {
	private PrivateKey privateKey;
	
	public Doctor() throws NoSuchAlgorithmException {
		
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	
	
}
