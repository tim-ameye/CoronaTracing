package mixingProxy;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

import cateringFacility.CateringInterface;

public interface MixingProxyInterface extends Remote {
	public Response registerVisit(Capsule capsule, PublicKey publicKey) throws RemoteException;
	
	public void sendAndRecieveAcknowledge(Acknowledge ack) throws RemoteException, FileNotFoundException;
	
	public void setGui(GuiMixingProxy gmp) throws RemoteException;
	public GuiMixingProxy getGuiMixingProxy() throws RemoteException;
}
