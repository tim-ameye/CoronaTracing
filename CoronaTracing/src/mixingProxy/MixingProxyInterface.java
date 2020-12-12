package mixingProxy;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface MixingProxyInterface extends Remote {
	public Response registerVisit(Capsule capsule, PublicKey publicKey) throws RemoteException;
	
	public void sendAndRecieveAcknowledge(Acknowledge ack) throws RemoteException, FileNotFoundException;
	
}
