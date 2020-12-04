package Visitor;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface VisitorInterface extends Remote {
	
	public void testConnection(String s) throws RemoteException;
	public String getFirstName() throws RemoteException;
	public String getLastName() throws RemoteException;
	public String getPhoneNumber() throws RemoteException;
	public void alreadyRegistered() throws RemoteException;
	//public void tell(String s) throws RemoteException;
	public void setGUI(guiVisitor t) throws RemoteException;
	public guiVisitor getGUI() throws RemoteException;
	 
	

}
