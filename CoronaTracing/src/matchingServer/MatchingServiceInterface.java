package matchingServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

import mixingProxy.Capsule;


public interface MatchingServiceInterface extends Remote{

	public void sendCapsule(Capsule capsule) throws RemoteException;
}
