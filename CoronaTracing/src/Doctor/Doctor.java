package Doctor;

import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class Doctor {
	private KeyPair keyPair;
	
	public Doctor() throws NoSuchAlgorithmException {
		keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
	}

	public void testConnection(String s) throws RemoteException {
		System.out.println("[CONNECTION_TEST]: "+s);
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}
	
}
