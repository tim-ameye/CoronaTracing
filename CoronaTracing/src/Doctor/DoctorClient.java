package Doctor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import matchingServer.MatchingServiceInterface;
import registrar.RegistrarInterface;

public class DoctorClient {
	
	public static void main(String[] args) throws RemoteException, NotBoundException {
		MatchingServiceInterface server;
		Scanner sc = new Scanner(System.in);
		
		
		Registry myRegistry = LocateRegistry.getRegistry("localhost", 55547);
		server = (MatchingServiceInterface) myRegistry.lookup("MatchingService");
		Doctor doctor = new Doctor();
		server.testConnection("Dit is een test");
		
	}
}
