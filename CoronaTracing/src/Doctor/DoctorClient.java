package Doctor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import matchingServer.MatchingServiceInterface;

public class DoctorClient {
	
	public static void main(String[] args) throws RemoteException, NotBoundException {
		MatchingServiceInterface server;
		Scanner sc = new Scanner(System.in);
		
		
		Registry myRegistry = LocateRegistry.getRegistry("localhost", 5547);
		server = (MatchingServiceInterface) myRegistry.lookup("MatchingService");
		Doctor doctor = new Doctor();
		doctor.testConnection("Test from doctorside");
		
	}
}
