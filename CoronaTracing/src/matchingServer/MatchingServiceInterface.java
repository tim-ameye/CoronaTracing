package matchingServer;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import mixingProxy.Acknowledge;
import mixingProxy.Capsule;
import registrar.TokenList;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.List;

import Visitor.Visit;
import mixingProxy.Capsule;


public interface MatchingServiceInterface extends Remote{
	public TokenList getCriticalRecordsOfToday(PublicKey publicKey) throws RemoteException;
	public void RecieveInfectedUserToken(List<Visit> infectedVisits) throws FileNotFoundException, NoSuchAlgorithmException, RemoteException;


	public void sendCapsule(Capsule capsule) throws FileNotFoundException,  RemoteException;
	public void testConnection(String text) throws RemoteException;
	public void sendAck(Acknowledge ack) throws FileNotFoundException;
}
