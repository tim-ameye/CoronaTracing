package mixingProxy;

import java.rmi.Remote;
import java.rmi.RemoteException;

import cateringFacility.CateringInterface;

public interface MixingProxyInterface extends Remote {
	public boolean registerVisit(Capsule capsule) throws RemoteException;
	
	public void setGui(GuiMixingProxy gmp) throws RemoteException;
	public GuiMixingProxy getGuiMixingProxy() throws RemoteException;
}
