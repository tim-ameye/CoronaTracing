package Visitor;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.List;
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

import matchingServer.MatchingServiceInterface;
import mixingProxy.Acknowledge;
import mixingProxy.Capsule;
import mixingProxy.MixingProxyInterface;
import mixingProxy.Response;
import registrar.RegistrarInterface;
import registrar.Token;
import registrar.TokenList;

public class VisitorClient extends UnicastRemoteObject implements VisitorInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2631581918599321298L;
	private Visitor visitor;
	private guiVisitor ui;
	RegistrarInterface registerServer = null;
	MixingProxyInterface mixingProxyServer = null;
	MatchingServiceInterface matchingservice = null;
	Registry myRegistry = null;
	Registry mixingProxyRegistry = null;
	Registry matchingRegistry = null;
	private Token token;
	private ArrayList<Visit> visits = new ArrayList<>();
	private String qrCode;
	private String[] currentToken;
	private PublicKey mixingPubKey;
	private PublicKey matchingPubKey;

	public VisitorClient() throws RemoteException, NotBoundException {
		myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		mixingProxyRegistry = LocateRegistry.getRegistry("localhost", 55546);
		matchingRegistry = LocateRegistry.getRegistry("localhost", 55547);

		matchingservice = (MatchingServiceInterface) matchingRegistry.lookup("MatchingService");
		registerServer = (RegistrarInterface) myRegistry.lookup("Registrar");
		mixingProxyServer = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxy");
		Certificate cert = null;
		try {
			KeyStore keystore = KeyStore.getInstance("JKS");
			char[] password = "AVB6589klp".toCharArray();
			FileInputStream fis = new FileInputStream("files\\keystore.jks");
			keystore.load(fis, password);
			cert = keystore.getCertificate("mixingProxy");
			matchingPubKey = keystore.getCertificate("matchingservice").getPublicKey();
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
	//TODO deze methode moet op regelmatige tijdstippen opgeroept worden
	public void getInfectedLogs() throws RemoteException, NoSuchAlgorithmException, FileNotFoundException {
		TokenList temp = matchingservice.getCriticalRecordsOfToday(visitor.getPublicKey());
		TokenList decrypted = temp.decryt(visitor.getPrivateKey());
		List<String> criticalRecords = new ArrayList<>();
		
		criticalRecords = decrypted.getTokens();
		// now we have an array list of all the tokens which were infected
		// check if this users was in one of these infected logs
		

		for (int i = 0; i < criticalRecords.size(); i++) {
			String current = criticalRecords.get(i);
			String[] currentSplitted = current.split("_");
			String currentHashString = currentSplitted[0];
			String visitInstant = currentSplitted[1];
			
			//check if this currenthash equals one of our visits
			for (int j = 0; j < visits.size(); j++) {
				if (visits.get(j).getCateringFacilityToken().equals(currentHashString)) {
					// we already were at the same cateringfacility :o
					if (visits.get(j).getBeginTime().toString().equals(visitInstant)) {
						//damn we were there at the same time interval 
						System.out.println("[WARNING] You were in a catering facility at the same time of an infected person!");
						System.out.println("[WARNING] sending acknowledge to to Mixing proxy so the Matching service will now we were informed!");
						SendAcknowledge(visits.get(j).getCateringFacilityToken(), visits.get(j).getBeginTime(), visits.get(j).getUserTokenSigned());
					}
				}
				
			}
			
		}
		
	}
	

	private void SendAcknowledge(String cfToken, Instant instant, String userToken) throws NoSuchAlgorithmException, RemoteException, FileNotFoundException {
		// TODO send necessary information to the mixing proxy, so the mixing proxy can forward it back to the matching service!

		Acknowledge ack = new Acknowledge(userToken, cfToken, instant.toString());
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		SecretKey sessionKey = keyGenerator.generateKey();

		// encrypt our acknowledge with the public key of matchingservice and a session key
		ack.encrypt(sessionKey, matchingPubKey);
		
		// send this encrypted acknowledge to our mixingproxy
		mixingProxyServer.sendAndRecieveAcknowledge(ack);
		
		
		
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
		try {
			addVisitToLog(visit);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//TODO testen
	public void addVisitToLog(Visit visit) throws IOException {
		FileWriter fileWriter = new FileWriter("files\\Visitor_logs.txt", true); // Set true for append mode
		PrintWriter printWriter = new PrintWriter(fileWriter);
		String stringToAppend = visit.getBeginTime()+"_"+visit.getRandomNumber() + "_"
				+ visit.getUserTokenSigned() + "_" + visit.getUserTokenUnsigned()
				+ "_" + visit.getCateringFacilityToken();
		printWriter.println(stringToAppend);
		printWriter.close();
	}

}
