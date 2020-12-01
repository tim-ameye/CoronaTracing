package registrar;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;

import Visitor.VisitorInterface;
import cateringFacility.CateringInterface;
import cateringFacility.RegistrarInterface;

public class Registrar extends UnicastRemoteObject implements RegistrarInterface{

	private Database db;
	private SecureRandom secureRandom;
	private KeyGenerator keyGenerator;
	private SecretKeyFactory secretKeyFactory;
	private MessageDigest messageDigest;
	
	protected Registrar(Database db) throws RemoteException {
		super();
		this.db = db;
		this.secureRandom = new SecureRandom();
		try {
			this.messageDigest = MessageDigest.getInstance("SHA-256");
			this.secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			this.keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256, secureRandom);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public boolean registerCateringFacility(CateringInterface cf) throws RemoteException {
		Logger logger = Logger.getLogger("Registrar"); 
		logger.info("[REGISTRAR] trying to register a Catering Facility");
		
		String business = cf.getBusinessNumber();
		String name = cf.getName();
		String address = cf.getAdress();
		String phoneNumber = cf.getPhoneNumber();

		CateringFacility catering = db.findCateringFacility(business, phoneNumber);
		if(catering != null) {
			logger.info("The catering facility has already been registered!");
			cf.alreadyRegistered();
			return false;
		} else {
			CateringFacility cateringFacility = new CateringFacility(business, name, address, phoneNumber);
			cateringFacility.setCateringInt(cf);
			db.addCateringFacility(cateringFacility);
			logger.info("The catering facility has been added to the list!");
			cateringFacility.generateSecretKey(keyGenerator);
			try {
				cateringFacility.generateHashes(10, secretKeyFactory, secureRandom, messageDigest);
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
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
