package Visitor;

import java.awt.EventQueue;
import java.io.File;
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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import matchingServer.MatchingServiceInterface;
import mixingProxy.Acknowledge;
import mixingProxy.Capsule;
import mixingProxy.MixingProxyInterface;
import mixingProxy.Response;
import registrar.RegistrarInterface;
import registrar.Token;
import registrar.TokenList;

public class VisitorClient {
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
	private PublicKey registrarPubKey;
	private RegisteringVisits rv;

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
			registrarPubKey = keystore.getCertificate("registrar").getPublicKey();
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

	public boolean register(Visitor v) throws RemoteException, NoSuchAlgorithmException {
		this.visitor = v;
		SecretKey sessionKey = KeyGenerator.getInstance("AES").generateKey();
		Visitor encrypt = v.encrypt(sessionKey, registrarPubKey);
		Response ans = registerServer.registerVisitor(encrypt, visitor.getPublicKey()).decrypt(visitor.getPrivateKey());
		if(ans.getMessage().equals("registered")) return true;
		else return false;
	}
	
	public boolean login(Visitor v) throws RemoteException, NoSuchAlgorithmException {
		this.visitor = v;
		SecretKey sessionKey = KeyGenerator.getInstance("AES").generateKey();
		Visitor encrypt = v.encrypt(sessionKey, registrarPubKey);
		Response ans = registerServer.loginVisitor(encrypt, visitor.getPublicKey()).decrypt(visitor.getPrivateKey());
		if(ans.getMessage().equals("User found")) return true;
		else return false;
	}

	public ArrayList<Visit> getVisits() {
		return visits;
	}

	public void setVisits(ArrayList<Visit> visits) {
		this.visits = visits;
	}

	public void getTokens() throws RemoteException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException {
		SecretKey sessionKey = KeyGenerator.getInstance("AES").generateKey();
		Visitor encrypted = visitor.encrypt(sessionKey, registrarPubKey);
		Token ans = registerServer.getTokensVisitor(encrypted, visitor.getPublicKey());
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
			capsule = new Capsule(day, currentToken[0], currentToken[1], arguments[3]);
			capsule = capsule.encrypt(sessionKey, mixingPubKey);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return capsule;
	}

	public class RegisteringVisits extends Thread {
		
		Capsule capsule;
		
		public RegisteringVisits(String text) {
			capsule = makeCapsule(text);
		}
		private volatile boolean exit = false;

		public void run() {
			while(!exit) {
				long current = System.currentTimeMillis();
				long start = System.currentTimeMillis();
				if(current > start +1800000) {
					start = System.currentTimeMillis();
					sendCapsule(capsule,"");
					exit = true;
				}
				current = System.currentTimeMillis();
			}
		}
		
		public void stopThread() {
			exit = true;
		}
			
	}
	
	
	public boolean sendCapsule(Capsule capsule, String text) {
		try {
			Response responseEncrypted = mixingProxyServer.registerVisit(capsule, visitor.getPublicKey());
			String response = responseEncrypted.decrypt(visitor.getPrivateKey()).getMessage();
			System.out.println(response);
			if (response.equals("Accepted")) {
				rv = new RegisteringVisits(qrCode);
				rv.start();
				return true;
			}
			else if (response.equals("Token already used")) {
				//TODO Fix the error, the capsule of which we want to get data is encrypted so we can't get data from it
				currentToken = token.getUnusedToken();
				if (text.equals("")) {
					System.out.println("Yeah this is not okay ~victor");
				}
				capsule = makeCapsule(text);
				//capsule = new Capsule(capsule.getCurrentTimeInterval(), currentToken[0], currentToken[1],
				//		capsule.getQrToken());
				if (sendCapsule(capsule, text)) 
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
				if (sendCapsule(capsule, text))
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
				if (visits.get(j).getBusinessNumber().equals(currentHashString)) {
					// we already were at the same cateringfacility :o
					if (visits.get(j).getBeginTime().toString().equals(visitInstant)) {
						//damn we were there at the same time interval 
						System.out.println("[WARNING] You were in a catering facility at the same time of an infected person!");
						System.out.println("[WARNING] sending acknowledge to to Mixing proxy so the Matching service will now we were informed!");
						ui.informUser();
						sendAcknowledge(visits.get(j).getBusinessNumber(), visits.get(j).getBeginTime(), visits.get(j).getUserTokenSigned());
						
					}
				}
				
			}
			
		}
		
	}
	

	private void sendAcknowledge(String cfToken, Instant instant, String userToken) throws NoSuchAlgorithmException, RemoteException, FileNotFoundException {
		// TODO send necessary information to the mixing proxy, so the mixing proxy can forward it back to the matching service!

		Acknowledge ack = new Acknowledge(userToken, cfToken, instant.toString());
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		SecretKey sessionKey = keyGenerator.generateKey();

		// encrypt our acknowledge with the public key of matchingservice and a session key
		ack = ack.encrypt(sessionKey, matchingPubKey);
		
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

	public void addVisit() throws IOException {// TODO controleren
		String[] arguments = qrCode.split("_");
		Visit visit = new Visit(Integer.parseInt(arguments[0]), currentToken[0], currentToken[1], arguments[3]); // randomnummmber,
																													// unique
																													// identifier,
																													// hash
																													// catering
		visit.setBusinessNumber(arguments[1]);
		Date date = new Date(System.currentTimeMillis());
		Instant day = roundTime(date);
		visit.setBeginTime(day);
		visits.add(visit);
		addVisitToLog(visit);
	}

	//TODO testen
	public void addVisitToLog(Visit visit)  {
		File sigFile = new File("files\\VisitorLogs");
		if(!sigFile.exists()) {
			sigFile.mkdir();
		}
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter("files\\VisitorLogs\\"+visitor.getPhoneNumber()+".txt", true);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			String stringToAppend = visit.getBeginTime()+"_"+visit.getRandomNumber() + "_"
					+ visit.getUserTokenSigned() + "_" + visit.getUserTokenUnsigned() + "_" + visit.getBusinessNumber()
					+ "_" + visit.getCateringFacilityToken();
			printWriter.println(stringToAppend);
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} // Set true for append mode
		
	}

	public void getVisitsFromLogs() {
		
		File tmpDir = new File("files\\VisitorLogs\\");
		if(!tmpDir.exists()) {
			tmpDir.mkdir();
		}
		boolean exists = tmpDir.exists();
		
		if (exists) {
			//this file is already here, so this is a user that already exists
			Scanner sc = null;
			try {
				sc = new Scanner(new File("files\\VisitorLogs\\"+visitor.getPhoneNumber()+".txt"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (sc.hasNextLine()) {
				String line =  sc.nextLine();
				String[] splittedLine = line.split("_");
				String beginTime = splittedLine[0];
				String randomNumber = splittedLine[1];
				String userTokenSigned = splittedLine[2];
				String userTokenUnsigned = splittedLine[3];
				String cfToken = splittedLine[4];
				String businessNumber = splittedLine[5];
						
				Visit temp = new Visit(Integer.parseInt(randomNumber), userTokenSigned, userTokenUnsigned, cfToken);
				temp.setBeginTime(Instant.parse(beginTime));
				temp.setBusinessNumber(businessNumber);
				visits.add(temp);				
			}
		}else {
			// file does not exist yet
			return;
		}

		
	}

	public void stopRv() {
		rv.stopThread();
		
	}

}