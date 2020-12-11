package matchingServer;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;


import mixingProxy.Capsule;


import java.security.NoSuchAlgorithmException;
import java.util.List;

import Visitor.Visit;
import mixingProxy.Capsule;


public interface MatchingServiceInterface extends Remote{
	public List<String> getCriticalRecordsOfToday() throws RemoteException;
	public void RecieveCapsule(Capsule capsule) throws FileNotFoundException, RemoteException;
	public void RecieveInfectedUserToken(List<Visit> infectedVisits) throws FileNotFoundException, NoSuchAlgorithmException, RemoteException;


	public void sendCapsule(Capsule capsule) throws RemoteException;
}
