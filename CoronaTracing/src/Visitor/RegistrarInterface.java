package Visitor;
import java.rmi.Remote;
import java.rmi.RemoteException;

import cateringFacility.CateringInterface;

public interface RegistrarInterface extends Remote {
	public boolean registerCateringFacility(CateringInterface cf) throws RemoteException; 	//TODO CF-interface opvragen
	public boolean loginCF() throws RemoteException;
	
	
	public boolean registerVisitor(VisitorInterface v) throws RemoteException;				//TODO User-interface opvragen
	public boolean loginVisitor() throws RemoteException;

	

}
