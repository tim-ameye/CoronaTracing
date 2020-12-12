package registrar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import Visitor.Visitor;
import Visitor.VisitorInterface;
import cateringFacility.CateringInterface;

public class Registrar extends UnicastRemoteObject implements RegistrarInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4987611293471268813L;
	private Database db;
	private SecureRandom secureRandom;
	private KeyGenerator keyGenerator;
	private SecretKeyFactory secretKeyFactory;
	private MessageDigest messageDigest;
	private Signature signature;
	private PrivateKey privateKey;
	private KeyStore keyStore;
	private PublicKey matchingServicePubKey;
	private PublicKey registrarPubKey;
	private final static String path = "files\\keystore.jks";

	protected Registrar(Database db) throws RemoteException {
		super();
		this.db = db;
		this.secureRandom = new SecureRandom();
		try {
			this.messageDigest = MessageDigest.getInstance("SHA-256");
			this.secretKeyFactory = SecretKeyFactory.getInstance("pbkdf2withhmacsha256");
			this.keyGenerator = KeyGenerator.getInstance("AES");
			this.signature = Signature.getInstance("SHA512withRSA");
			KeyFactory.getInstance("RSA");

			

			this.keyStore = KeyStore.getInstance("JKS");
			char[] password = "AVB6589klp".toCharArray();
			FileInputStream fis = new FileInputStream(path);
			keyStore.load(fis, password);

			privateKey = (PrivateKey) keyStore.getKey("registrar", password);

			keyStore.getCertificate("registrar");
			matchingServicePubKey = keyStore.getCertificate("matchingservice").getPublicKey();
			registrarPubKey = keyStore.getCertificate("registrar").getPublicKey();
			signature.initSign(privateKey);

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
		} catch (KeyStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean registerCateringFacility(CateringInterface cf) throws RemoteException {
		Logger logger = Logger.getLogger("Registrar");
		logger.info("[REGISTRAR] Trying to register a Catering Facility");

		String business = cf.getBusinessNumber();
		String name = cf.getName();
		String address = cf.getAdress();
		String phoneNumber = cf.getPhoneNumber();

		CateringFacility catering = db.findCateringFacility(business, phoneNumber);
		if (catering != null) {
			logger.info("[REGISTRAR] The catering facility has already been registered!");
			cf.alreadyRegistered();
			return false;
		} else {
			CateringFacility cateringFacility = new CateringFacility(business, name, address, phoneNumber);
			cateringFacility.setCateringInt(cf);
			db.addCateringFacility(cateringFacility);
			logger.info("[REGISTRAR] The catering facility has been added to the list!");
			try {
				cateringFacility.generateSecretKey(keyGenerator, keyStore);
				cateringFacility.generateHashes(10, secretKeyFactory, secureRandom, messageDigest);
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("[REGISTRAR] The secret key and first period of hashes has been calculated for:"
					+ cateringFacility.getBusinessNumber());
			return true;
		}
	}

	@Override
	public boolean loginCF(CateringInterface cf) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Hash getHashesCatering(String busNumber, String phoNumber, PublicKey publicKey)
			throws RemoteException {
		CateringFacility cf = db.findCateringFacility(busNumber, phoNumber);
		SecretKey sessionKey = keyGenerator.generateKey();
		Hash hash = cf.getHashFromToday();
		try {
			if(hash == null) {
				cf.generateHashes(10, secretKeyFactory, secureRandom, messageDigest);
				hash = cf.getHashFromToday();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hash encodedHashes = hash.encrypt(sessionKey, publicKey);
		return encodedHashes;
	}

	@Override
	public boolean registerVisitor(Visitor v) throws RemoteException {
		Logger logger = Logger.getLogger("Registrar");
		logger.info("[REGISTRAR] trying to register a visitor");

		Visitor visitor = v.decrypt(privateKey);
		
		String firstName = visitor.getFirstName();
		String lastName = visitor.getLastName();
		String phoneNumber = visitor.getPhoneNumber();

		User user = db.findUser(phoneNumber);
		if (user != null) {
			logger.info("The visitor has already been registered!");
			return false;
		} else {
			user = new User(firstName, lastName, phoneNumber);
			db.addVisitor(user);
			logger.info("The visitor has been added to the registrar!");
			try {
				user.generateTokenSet(secureRandom, signature);
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
	public boolean loginVisitor(Visitor v) throws RemoteException {
		Visitor visitor = v.decrypt(privateKey);
		User user = db.findUser(visitor.getPhoneNumber());
		if(user == null) {
			return false;
		}
		return true;
	}

	@Override
	public Token getTokensVisitor(Visitor v, PublicKey pubKey) throws RemoteException {
		Visitor visitor = v.decrypt(privateKey);
		User user = db.findUser(visitor.getPhoneNumber());
		user.setPublicKey(visitor.getPublicKey());
		Token encrypted = null;
		Token token = null;
		try {
			SecretKey sessionKey = KeyGenerator.getInstance("AES").generateKey();
			token = user.getTokensToday();
			if(token == null) {
				user.generateTokenSet(secureRandom, signature);
				token = user.getTokensToday();
			}
			encrypted = token.encrypt(sessionKey, pubKey);
			//Token decrypted = encrypted.decrypt(privateKey);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return encrypted;
	}

	@Override
	public void notifyVisitors(ArrayList<String> infectedTokens) throws RemoteException {
		ArrayList<User> infectedUsers = new ArrayList<>();
		for(String infected: infectedTokens) {
			User infectedUser = db.findUserWithToken(infected);
			if(infectedUser == null) System.out.println("No user with one of the infected tokens has been found.");
			else if(!infectedUsers.contains(infectedUser)) {
				infectedUsers.add(infectedUser);
			}
		}
		for(User user: infectedUsers) {
			System.out.println(user.getName() + " needs to be contacted his/her phonenumber is " + user.getPhoneNumber() + ".");
		}
	}

	@Override
	public void notifyFacility(ArrayList<byte[]> infectedHahses) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TokenList getCfHashesFromToday() {
		TokenList tokenList = new TokenList();
		for(CateringFacility cf: db.getCateringFacilities()) {
			String pseudo = cf.nymToday();
			if(pseudo == null) {
				try {
					cf.generateHashes(10, secretKeyFactory, secureRandom, messageDigest);
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pseudo = cf.nymToday();
			}
			tokenList.add(pseudo);
		}
		tokenList.shuffle();
		SecretKey sessionKey = keyGenerator.generateKey();
		TokenList encrypted = tokenList.encrypt(sessionKey, matchingServicePubKey);
		return encrypted;
	}

	@Override
	public void InformUsers(TokenList encrypted) throws RemoteException {
		TokenList decrypted = encrypted.decryt(privateKey);
		ArrayList<String> possibleInfected = decrypted.getTokens(); 
		notifyVisitors(possibleInfected);
		
	}

}
