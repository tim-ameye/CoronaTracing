package Visitor;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface VisitorInterface extends Remote {
	public void testConnection(String s) throws RemoteException;
	//public void tell(String s) throws RemoteException;
	//public void setGUI(UserInterface t) throws RemoteException;
	//public UserInterface getGUI() throws RemoteException;
	//public void updateUI(Vector v) throws RemoteException;
	 
	

}
