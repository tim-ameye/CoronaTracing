package mixingProxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import matchingServer.MatchingServiceInterface;
import registrar.RegistrarInterface;

public class MixingProxy extends UnicastRemoteObject implements MixingProxyInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8870169243620905534L;
	private Queue<Capsule> queue;
	private PrivateKey privateKey;
	private Certificate certificate;
	private PublicKey matchingServicePubKey;
	private ArrayList<String> signedTokensToday;
	private SecureRandom random;
	private MatchingServiceInterface matchingServer = null;
	private Registry matchingRegistry = null;

	private KeyStore keyStore;
	private final static String path = "files\\keystore.jks";

	public MixingProxy() throws RemoteException {
		queue = new LinkedList<>();
		signedTokensToday = new ArrayList<>();
		random = new SecureRandom();
		try {
			matchingRegistry = LocateRegistry.getRegistry("localhost", 55547);
			matchingServer = (MatchingServiceInterface) matchingRegistry.lookup("MatchingService");
			this.keyStore = KeyStore.getInstance("JKS");
			char[] password = "AVB6589klp".toCharArray();
			FileInputStream fis;
			fis = new FileInputStream(path);
			keyStore.load(fis, password);
			privateKey = (PrivateKey) keyStore.getKey("mixingproxy", password);
			certificate = keyStore.getCertificate("mixingproxy");
			matchingServicePubKey = keyStore.getCertificate("matchingservice").getPublicKey();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Response registerVisit(Capsule encrypted, PublicKey publicKeyUser) throws RemoteException {
		Response response = new Response();
		SecretKey sessionKey = null;
		Capsule capsule = encrypted.Decrypt(privateKey);
		// 1: validate of the user token
		// byte[] userToken = capsule.getUsertoken();
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256, random);
			sessionKey = keyGenerator.generateKey();
			Certificate certificate = keyStore.getCertificate("registrar");
			PublicKey publicKey = certificate.getPublicKey();
			Signature sig = Signature.getInstance("SHA512withRSA");
			sig.initVerify(publicKey);
			sig.update(capsule.getUnsignedBytes());
			boolean b = sig.verify(capsule.getSignedBytes());
			if (!b) {
				response.setMessage("Token not valid");
				return response.encrypt(sessionKey, publicKeyUser);
			}

			// 2: userToken is voor de huidige dag
			Instant today = new Date(System.currentTimeMillis()).toInstant().truncatedTo(ChronoUnit.DAYS);
			byte[] unsigned = capsule.getUnsignedBytes();
			byte[] dateBytes = new byte[unsigned.length - 64];
			for (int i = 64; i < unsigned.length; i++) {
				dateBytes[i - 64] = unsigned[i];
			}
			Instant day = Instant.parse(new String(dateBytes));
			if (!day.equals(today)) {
				System.out.println("A token is used at a wrong day!");
				response.setMessage("Wrong day");
				return response.encrypt(sessionKey, publicKeyUser);
			}
			// 3: werd nog niet eerder gebruikt
			if (signedTokensToday.contains(capsule.getUserTokenSigned())) {
				System.out.println("A token has tried to be used twice!");
				response.setMessage("Token already used");
				return response.encrypt(sessionKey, publicKeyUser);
			}

			// 4: alle 3 voldaan, sign qrToken en stuur antwoord terug. niet-voldaan: return
			// false
			signedTokensToday.add(capsule.getUserTokenSigned());
			queue.add(capsule);
			response.setMessage("Accepted");
			return response.encrypt(sessionKey, publicKeyUser);
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setMessage("");
		return response.encrypt(sessionKey, publicKeyUser);
	}

	public void sendCapsules() {
		try {
			List<Capsule> shuffle = new ArrayList<>(queue);
			Collections.shuffle(shuffle, random);
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256, random);
			SecretKey sessionKey = keyGenerator.generateKey();
			for(Capsule capsule: shuffle) {
				capsule.encrypt(sessionKey, matchingServicePubKey);
				matchingServer.sendCapsule(capsule);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setGui(GuiMixingProxy gmp) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public GuiMixingProxy getGuiMixingProxy() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
