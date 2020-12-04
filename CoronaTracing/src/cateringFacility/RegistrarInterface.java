package cateringFacility;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;
import java.util.Map;

import Visitor.VisitorInterface;

public interface RegistrarInterface extends Remote {

	public boolean registerCateringFacility(CateringInterface cf) throws RemoteException; 	//TODO CF-interface opvragen
	public boolean loginCF(CateringInterface cf) throws RemoteException;
	public Map<Instant, byte[]> getHashes(String busNumber, String phoNumber) throws RemoteException;
	
	
	public boolean registerVisitor(VisitorInterface v) throws RemoteException;				//TODO User-interface opvragen
	public boolean loginVisitor() throws RemoteException;
}
