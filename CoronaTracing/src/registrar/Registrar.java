package registrar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
	private Signature signature;
	private KeyPairGenerator keyPairGen;
	private KeyPair keyPair;
	private KeyFactory keyFactory;
	
	protected Registrar(Database db) throws RemoteException {
		super();
		this.db = db;
		this.secureRandom = new SecureRandom();
		try {
			this.messageDigest = MessageDigest.getInstance("SHA-256");
			this.secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			this.keyGenerator = KeyGenerator.getInstance("AES");
			this.signature = Signature.getInstance("SHA512withRSA");
			this.keyPairGen = KeyPairGenerator.getInstance("RSA");
			this.keyFactory = KeyFactory.getInstance("RSA");
			
			keyPairGen.initialize(2048);
			keyGenerator.init(256, secureRandom);
			
			// Read public key
			File filePublicKey = new File("public.key");
			FileInputStream fis = new FileInputStream("public.key");
			byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
			fis.read(encodedPublicKey);
			fis.close();
			
			// Read private key
			File filePrivateKey = new File("private.key");
			fis = new FileInputStream("private.key");
			byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
			fis.read(encodedPrivateKey);
			fis.close();
			
			// See if the keys are generated if not, generate new keys
			if(encodedPublicKey.length == 0 || encodedPrivateKey.length == 0) {
				keyPair = keyPairGen.generateKeyPair();
				// Save public key
				X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
				FileOutputStream fos = new FileOutputStream("public.key");
				fos.write(x509EncodedKeySpec.getEncoded());
				fos.close();
				
				// Save private key
				PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
				fos = new FileOutputStream("private.key");
				fos.write(pkcs8EncodedKeySpec.getEncoded());
				fos.close();
			} else {
				try {
					// Store the key in the keypair
					X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encodedPublicKey);
					PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);
					
					PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
					PrivateKey privateKey = keyFactory.generatePrivate(priKeySpec);
					
					keyPair = new KeyPair(publicKey, privateKey);
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			signature.initSign(keyPair.getPrivate());
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
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
				e.printStackTrace();
			}
			logger.info("The secret key and first period of hashes has been calculated for:" + cateringFacility.getBusinessNumber());
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

		String firstName = v.getFirstName();
		String lastName = v.getLastName();
		String phoneNumber = v.getPhoneNumber();
		
		User user = db.findUser(phoneNumber);
		if(user != null) {
			logger.info("The visitor has already been registered!");
			v.alreadyRegistered();
			return false;
		} else {
			user = new User(firstName, lastName, phoneNumber);
			user.setVisitorInt(v);
			db.addVisitor(user);
			logger.info("The visitor has been added to the registrar!");
			try {
				user.generateTokenSet(48, secureRandom, signature);
				logger.info("A first set of tokens has been calculated.");
			} catch (SignatureException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
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
