package registrar;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

import Visitor.VisitorInterface;
import cateringFacility.CateringInterface;

public interface RegistrarInterface extends Remote {

	public boolean registerCateringFacility(CateringInterface cf) throws RemoteException; 	//TODO CF-interface opvragen
	public boolean loginCF(CateringInterface cf) throws RemoteException;
	public Map<Instant, byte[]> getHashesCatering(String busNumber, String phoNumber, PublicKey publicKey) throws RemoteException;
	
	
	public boolean registerVisitor(VisitorInterface v) throws RemoteException;				//TODO User-interface opvragen
	public boolean loginVisitor() throws RemoteException;
	public ArrayList<byte[]> getTokensVisitor(String phoNumber, PublicKey publicKey) throws RemoteException;
	
	public void notifyVisitors(ArrayList<byte[]> infectedTokens) throws RemoteException;
	public void notifyFacility(ArrayList<byte[]> infectedHahses) throws RemoteException;
}
