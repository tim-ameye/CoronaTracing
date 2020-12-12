package registrar;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

import Visitor.Visitor;
import cateringFacility.CateringFacility;
import mixingProxy.Response;

public interface RegistrarInterface extends Remote {

	public Response registerCateringFacility(CateringFacility cf, PublicKey pubKey) throws RemoteException; 	//TODO CF-interface opvragen
	public Response loginCF(CateringFacility cf, PublicKey pubKey) throws RemoteException;
	public Hash getHashesCatering(String busNumber, String phoNumber, PublicKey publicKey) throws RemoteException;
	
	
	public Response registerVisitor(Visitor v, PublicKey pubKey) throws RemoteException;				//TODO User-interface opvragen
	public Response loginVisitor(Visitor v, PublicKey pubKey) throws RemoteException;
	public Token getTokensVisitor(Visitor v, PublicKey pubKey) throws RemoteException;
	
	public void notifyVisitors(ArrayList<String> infectedTokens) throws RemoteException;
	public void notifyFacility(ArrayList<byte[]> infectedHahses) throws RemoteException;
	public TokenList getCfHashesFromToday() throws RemoteException;
	public void InformUsers(TokenList encrypted) throws RemoteException;
}
