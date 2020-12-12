package matchingServer;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import mixingProxy.Acknowledge;
import mixingProxy.Capsule;
import registrar.TokenList;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.List;

import Doctor.Infection;
import Visitor.Visit;
import mixingProxy.Capsule;


public interface MatchingServiceInterface extends Remote{
	public TokenList getCriticalRecordsOfToday(PublicKey publicKey) throws RemoteException;
	public void recieveInfectedUserToken(Infection infection) throws FileNotFoundException, NoSuchAlgorithmException, RemoteException, InvalidKeyException, SignatureException;


	public void sendCapsule(Capsule capsule) throws FileNotFoundException,  RemoteException;
	public void testConnection(String text) throws RemoteException;
	public void sendAck(Acknowledge ack) throws RemoteException, FileNotFoundException;
}
