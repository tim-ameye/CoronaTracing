package registrar;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

import Visitor.VisitorInterface;
import cateringFacility.CateringFacility;
import cateringFacility.CateringInterface;
import cateringFacility.RegistrarInterface;

public class Registrar extends UnicastRemoteObject implements RegistrarInterface{

	private Database db;
	
	
	protected Registrar(Database db) throws RemoteException {
		super();
		this.db = db;
	}


	public boolean registerCateringFacility(CateringInterface cf) throws RemoteException {
		Logger logger = Logger.getLogger("Registrar"); 
		logger.info("[REGISTRAR] trying to register a Catering Facility");
		//TODO writing CF to database
		cf.testConnection("Hello CateringInterface, registrar here");
		
		return false;
	}


	@Override
	public boolean loginCF() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean registerVisitor(VisitorInterface v) throws RemoteException {
		Logger logger = Logger.getLogger("Registrar"); 
		logger.info("[REGISTRAR] trying to register a visitor");
		//TODO writing CF to database
		v.testConnection("Hello visitor, registrar here");
		
		return false;
	}


	@Override
	public boolean loginVisitor() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}




	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//			RMI-Methods
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	
	
}
