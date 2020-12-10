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
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

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
	private Certificate certificate;
	private KeyFactory keyFactory;
	private KeyStore keyStore;
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
			this.keyFactory = KeyFactory.getInstance("RSA");

			keyGenerator.init(256, secureRandom);

			this.keyStore = KeyStore.getInstance("JKS");
			char[] password = "AVB6589klp".toCharArray();
			FileInputStream fis = new FileInputStream(path);
			keyStore.load(fis, password);

			privateKey = (PrivateKey) keyStore.getKey("registrar", password);

			certificate = keyStore.getCertificate("registrar");
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
	public Map<Instant, byte[]> getHashesCatering(String busNumber, String phoNumber, PublicKey publicKey)
			throws RemoteException {
		CateringFacility cf = db.findCateringFacility(busNumber, phoNumber);
		Map<Instant, byte[]> encodedHashes = new HashMap<>();
		SecretKey sessionKey = keyGenerator.generateKey();
		Cipher encryptText;
		try {
			encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			Cipher encryptSession = Cipher.getInstance("RSA");
			encryptSession.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedSessionKey = encryptSession.doFinal(sessionKey.getEncoded());
			encodedHashes.put(Instant.EPOCH, encryptedSessionKey);
			if (cf.getHashFromToday() == null) {
				cf.generateHashes(10, secretKeyFactory, secureRandom, messageDigest);
			}
			for (Map.Entry<Instant, byte[]> entry : cf.getHashFromToday().entrySet()) {
				byte[] encoded = encryptText.doFinal(entry.getValue());
				encodedHashes.put(entry.getKey(), encoded);
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return encodedHashes;
	}

	@Override
	public boolean registerVisitor(VisitorInterface v) throws RemoteException {
		Logger logger = Logger.getLogger("Registrar");
		logger.info("[REGISTRAR] trying to register a visitor");

		String firstName = v.getFirstName();
		String lastName = v.getLastName();
		String phoneNumber = v.getPhoneNumber();

		User user = db.findUser(phoneNumber);
		if (user != null) {
			logger.info("The visitor has already been registered!");
			v.alreadyRegistered();
			return false;
		} else {
			user = new User(firstName, lastName, phoneNumber);
			user.setVisitorInt(v);
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
	public boolean loginVisitor() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Token getTokensVisitor(String phoNumber, PublicKey publicKey) throws RemoteException {
		User user = db.findUser(phoNumber);
		user.setPublicKey(publicKey);
		SecretKey sessionKey = keyGenerator.generateKey();
		Token token = null;
		try {
			token = user.getTokensToday();
			if(token == null) {
				user.generateTokenSet(secureRandom, signature);
				token = user.getTokensToday();
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Token encrypted = token.encrypt(sessionKey, publicKey);
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
}
