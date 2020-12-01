package registrar;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Registrar extends UnicastRemoteObject implements RegistrarInterface{

	private Database db;
	
	
	protected Registrar(Database db) throws RemoteException {
		super();
		this.db = db;
	}


	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//			RMI-Methods
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	
	
}
