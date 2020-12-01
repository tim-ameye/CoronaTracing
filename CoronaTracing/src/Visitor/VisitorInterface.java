package Visitor;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VisitorInterface extends Remote {
	public void testConnection(String s) throws RemoteException;

}
