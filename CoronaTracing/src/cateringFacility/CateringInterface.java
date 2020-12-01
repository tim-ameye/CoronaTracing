package cateringFacility;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CateringInterface extends Remote {
	public void testConnection(String s) throws RemoteException;
}
