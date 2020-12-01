package Visitor;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrarInterface extends Remote {
	public boolean registerVisitor(VisitorInterface vi) throws RemoteException;
	public boolean loginV() throws RemoteException;

	

}
