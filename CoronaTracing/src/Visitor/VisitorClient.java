package Visitor;

import java.awt.EventQueue;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Date;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import mixingProxy.Capsule;
import mixingProxy.MixingProxyInterface;
import registrar.RegistrarInterface;
import registrar.Token;

public class VisitorClient extends UnicastRemoteObject implements VisitorInterface{
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

	
	public VisitorClient() throws RemoteException, NotBoundException {
		myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		mixingProxyRegistry = LocateRegistry.getRegistry("localhost",55546);
		registerServer = (RegistrarInterface) myRegistry.lookup("Registrar"); 
		mixingProxyServer = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxy");
		this.visitor = null;
		//voor testen
		Visit visit1 = new Visit(13,"mj","cmlkjmku","YAY");
		Date date = new Date(System.currentTimeMillis());
		Instant day = roundTime(date);
		visit1.setBeginTime(day);
		visit1.setBusinessNumber("12345");
		visit1.setEndTime(day);
		Visit visit2 = new Visit(19,"kmjmjmjt","mkjmkljmku","joj");
		visit2.setBeginTime(day);
		visit2.setEndTime(day);
		visit2.setBusinessNumber("6789");
		
		visits.add(visit1);
		visits.add(visit2);
	}
	
	public Visitor getVisitor() throws RemoteException {
		return this.visitor;
	}
	
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}
	
	public void testConnection(String s) throws RemoteException{
		//TODO
	}
	
	public String getFirstName() throws RemoteException{
		return this.visitor.getFirstName();
	}
	
	public String getLastName() throws RemoteException{
		return this.visitor.getLastName();
	}
	
	public String getPhoneNumber() throws RemoteException{
		return this.visitor.getPhoneNumber();
	}
	
	public void alreadyRegistered() throws RemoteException{
		//TODO
	}
	
	public void setGUI(guiVisitor t) throws RemoteException{
		this.ui = t;
	}
	
	public guiVisitor getGUI() throws RemoteException{
		return this.ui;
	}
	
	public void updateUI(Vector v) throws RemoteException{
		//TODO
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

	public void getTokens() throws RemoteException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
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
		/*
		System.out.println("Please enter first name : ");
		String firstName = sc.nextLine();
		System.out.println("Please enter last name : ");
		String lastName = sc.nextLine();
		System.out.println("Please enter phoneNumber: ");
		String phoneNumber = sc.nextLine();
		
		try {
			Visitor visitor = new Visitor(firstName, lastName, phoneNumber);
			visitor.testConnection("Test from visitorside");
			
			Registry myRegistry = LocateRegistry.getRegistry("localhost", 55545);
			server = (RegistrarInterface) myRegistry.lookup("Registrar"); 
			
			server.registerVisitor(visitor);
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}*/
	}

	public Capsule makeCapsule(String text) {
		this.qrCode = text;
		String[] arguments = text.split("_");
		Date date = new Date(System.currentTimeMillis());
		Instant day = roundTime(date);
		currentToken = token.getUnusedToken();
		Capsule capsule = new Capsule(day, currentToken[1], currentToken[2], arguments[2]);
		return capsule;
	}

	public boolean sendCapsule(Capsule capsule) {
		try {
			boolean b = mixingProxyServer.registerVisit(capsule);
			return b;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		return false;
		
	}
	
	public Instant roundTime(Date date) {
		Instant minutes = date.toInstant().truncatedTo(ChronoUnit.MINUTES);
		Instant hours = date.toInstant().truncatedTo(ChronoUnit.HOURS);
		if (minutes.toEpochMilli()-hours.toEpochMilli() > 1.8e+6) hours.plus(30,ChronoUnit.MINUTES);
		return hours;
	}
	
	public void addVisit() {//TODO controleren
		String[] arguments = qrCode.split("_");
		Visit visit = new Visit(Integer.parseInt(arguments[0]), currentToken[0], currentToken[1], arguments[2]); //randomnummmber, unique identifier, hash catering
		visit.setBusinessNumber(arguments[1]);
		Date date = new Date(System.currentTimeMillis());
		Instant day = roundTime(date);
		visit.setBeginTime(day);
		visits.add(visit);
	}

}
