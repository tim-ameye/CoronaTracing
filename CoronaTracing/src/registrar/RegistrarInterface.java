package registrar;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Visitor.VisitorInterface;
import cateringFacility.CateringInterface;

public interface RegistrarInterface extends Remote {

	public boolean registerCateringFacility(CateringInterface cf) throws RemoteException; 	//TODO CF-interface opvragen
	public boolean loginCF(CateringInterface cf) throws RemoteException;
	public Hash getHashesCatering(String busNumber, String phoNumber, PublicKey publicKey) throws RemoteException;
	
	
	public boolean registerVisitor(VisitorInterface v) throws RemoteException;				//TODO User-interface opvragen
	public boolean loginVisitor() throws RemoteException;
	public Token getTokensVisitor(String phoNumber, PublicKey publicKey) throws RemoteException;
	
	public void notifyVisitors(ArrayList<String> infectedTokens) throws RemoteException;
	public void notifyFacility(ArrayList<byte[]> infectedHahses) throws RemoteException;
	public TokenList getCfHashesFromToday() throws RemoteException;
	public void InformUsers(TokenList encrypted);
}
