package cateringFacility;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import Visitor.VisitorInterface;

import java.rmi.RemoteException;

public interface RegistrarInterface extends Remote {

	public boolean registerCateringFacility(CateringInterface cf) throws RemoteException; 	//TODO CF-interface opvragen
	public boolean loginCF() throws RemoteException;
	
	
	public boolean registerVisitor(VisitorInterface v) throws RemoteException;				//TODO User-interface opvragen
	public boolean loginVisitor() throws RemoteException;
}
