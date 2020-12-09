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
import java.sql.Date;
import java.time.Instant;
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

public class VisitorClient extends UnicastRemoteObject implements VisitorInterface{
	private Visitor visitor;
	private guiVisitor ui;
	RegistrarInterface registerServer = null;
	MixingProxyInterface mixingProxyServer = null;
	Registry myRegistry = null;
	private ArrayList<byte[]> tokens = new ArrayList<>();

	
	public VisitorClient() throws RemoteException, NotBoundException {
		myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		registerServer = (RegistrarInterface) myRegistry.lookup("Registrar"); 
		mixingProxyServer = (MixingProxyInterface) myRegistry.lookup("MixingProxy");
		this.visitor = null;
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
	
	public void getTokens() throws RemoteException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		ArrayList<byte[]> ans = registerServer.getTokensVisitor(visitor.getPhoneNumber(), visitor.getPublicKey());
		Cipher cipherKey = Cipher.getInstance("RSA");
		cipherKey.init(Cipher.DECRYPT_MODE, visitor.getPrivateKey());
		SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(ans.get(0)), "AES");
		Cipher cipherToken = Cipher.getInstance("AES");
		cipherToken.init(Cipher.DECRYPT_MODE, sessionKey);
		for(int i = 1; i < ans.size(); i++) {
			byte[] decrypt = cipherToken.doFinal(ans.get(i));
			tokens.add(decrypt);
		}
	
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

	public void makeCapsule(String text) {
		String[] arguments = text.split("_");
		Date date = new Date(System.currentTimeMillis());
		Instant day = date.toInstant(); //TODO afronden op half uur
		Capsule capsule = new Capsule(day, tokens.remove(0), arguments[2]);
		
	}

}
