package Doctor;

import java.rmi.RemoteException;

public class Doctor {
	
	public Doctor() {
		
	}

	public void testConnection(String s) throws RemoteException {
		System.out.println("[CONNECTION_TEST]: "+s);
		
	}
}
