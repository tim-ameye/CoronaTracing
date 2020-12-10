package matchingServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatchingService extends UnicastRemoteObject implements MatchingServiceInterface{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6919696315736336452L;

	public MatchingService(Database db) throws RemoteException{
		
	}
}
