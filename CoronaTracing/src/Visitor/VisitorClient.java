package Visitor;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import mixingProxy.Capsule;
import mixingProxy.MixingProxyInterface;
import mixingProxy.Response;
import registrar.RegistrarInterface;
import registrar.Token;

public class VisitorClient extends UnicastRemoteObject implements VisitorInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2631581918599321298L;
	private Visitor visitor;
	private guiVisitor ui;
	RegistrarInterface registerServer = null;
	MixingProxyInterface mixingProxyServer = null;
	Registry myRegistry = null;
	Registry mixingProxyRegistry = null;
	private Token token;
	private ArrayList<Visit> visits = new ArrayList<>();
	private String qrCode;
	private String[] currentToken;
	private PublicKey mixingPubKey;

	public VisitorClient() throws RemoteException, NotBoundException {
		myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		mixingProxyRegistry = LocateRegistry.getRegistry("localhost", 55546);
		registerServer = (RegistrarInterface) myRegistry.lookup("Registrar");
		mixingProxyServer = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxy");
		Certificate cert = null;
		try {
			KeyStore keystore = KeyStore.getInstance("JKS");
			char[] password = "AVB6589klp".toCharArray();
			FileInputStream fis = new FileInputStream("files\\keystore.jks");
			keystore.load(fis, password);
			cert = keystore.getCertificate("mixingProxy");
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mixingPubKey = cert.getPublicKey();
		
		
		this.visitor = null;

	}

	public Visitor getVisitor() throws RemoteException {
		return this.visitor;
	}

	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}

	public void testConnection(String s) throws RemoteException {
		// TODO
	}

	public String getFirstName() throws RemoteException {
		return this.visitor.getFirstName();
	}

	public String getLastName() throws RemoteException {
		return this.visitor.getLastName();
	}

	public String getPhoneNumber() throws RemoteException {
		return this.visitor.getPhoneNumber();
	}

	public void alreadyRegistered() throws RemoteException {
		// TODO
	}

	public void setGUI(guiVisitor t) throws RemoteException {
		this.ui = t;
	}

	public guiVisitor getGUI() throws RemoteException {
		return this.ui;
	}

	public void updateUI(Vector v) throws RemoteException {
		// TODO
	}

	public boolean register(Visitor v) throws RemoteException {
		this.visitor = v;
		return registerServer.registerVisitor(v);
	}

	public ArrayList<Visit> getVisits() {
		return visits;
	}

	public void setVisits(ArrayList<Visit> visits) {
		this.visits = visits;
	}

	public void getTokens() throws RemoteException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException {
		Token ans = registerServer.getTokensVisitor(visitor.getPhoneNumber(), visitor.getPublicKey());
		this.token = ans.decrypt(visitor.getPrivateKey());
	}

	public static void main(String[] args) throws RemoteException, NotBoundException {
		VisitorClient visitorClient = new VisitorClient();
		EventQueue.invokeLater(() -> {
			try {
				guiVisitor window = new guiVisitor(visitorClient);
				window.setVisible(true);

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public Capsule makeCapsule(String text) {
		this.qrCode = text;
		String[] arguments = text.split("_");
		Date date = new Date(System.currentTimeMillis());
		Instant day = roundTime(date);
		currentToken = token.getUnusedToken();
		Capsule capsule = null;
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			SecretKey sessionKey = keyGenerator.generateKey();
			capsule = new Capsule(day, currentToken[0], currentToken[1], arguments[2]);
			capsule = capsule.encrypt(sessionKey, mixingPubKey);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return capsule;
	}

	public boolean sendCapsule(Capsule capsule) {
		try {
			Response responseEncrypted = mixingProxyServer.registerVisit(capsule, visitor.getPublicKey());
			String response = responseEncrypted.decrypt(visitor.getPrivateKey()).getMessage();
			System.out.println(response);
			if (response.equals("Accepted"))
				return true;
			else if (response.equals("Token already used")) {
				currentToken = token.getUnusedToken();
				capsule = new Capsule(capsule.getCurrentTimeInterval(), currentToken[0], currentToken[1],
						capsule.getQrToken());
				if (sendCapsule(capsule))
					return true;
			} else if (response.equals("Token not valid")) {
				System.out.println("You have submitted an invalid token. Please do not try to hack the system!");
				return false;
			} else if (response.equals("Wrong day")) {
				System.out.println(
						"The token you submitted was for a wrong day. New tokens are being requested and send...");
				getTokens();
				currentToken = token.getUnusedToken();
				capsule = new Capsule(capsule.getCurrentTimeInterval(), currentToken[0], currentToken[1],
						capsule.getQrToken());
				if (sendCapsule(capsule))
					return true;
			} else if (response.equals("")) {
				System.out.println("An error occured.");
				return false;
			}
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
				| NoSuchPaddingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public Instant roundTime(Date date) {
		Instant minutes = date.toInstant().truncatedTo(ChronoUnit.MINUTES);
		Instant hours = date.toInstant().truncatedTo(ChronoUnit.HOURS);
		if (minutes.toEpochMilli() - hours.toEpochMilli() > 1.8e+6)
			hours.plus(30, ChronoUnit.MINUTES);
		return hours;
	}

	public void addVisit() {// TODO controleren
		String[] arguments = qrCode.split("_");
		Visit visit = new Visit(Integer.parseInt(arguments[0]), currentToken[0], currentToken[1], arguments[2]); // randomnummmber,
																													// unique
																													// identifier,
																													// hash
																													// catering
		visit.setBusinessNumber(arguments[1]);
		Date date = new Date(System.currentTimeMillis());
		Instant day = roundTime(date);
		visit.setBeginTime(day);
		visits.add(visit);
	}

}
