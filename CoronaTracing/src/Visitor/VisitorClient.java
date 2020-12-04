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
import java.util.ArrayList;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import registrar.RegistrarInterface;

public class VisitorClient extends UnicastRemoteObject implements VisitorInterface{
	private Visitor visitor;
	private guiVisitor ui;
	RegistrarInterface server = null;
	Registry myRegistry = null;
	private ArrayList<byte[]> tokens;

	
	public VisitorClient() throws RemoteException, NotBoundException {
		myRegistry = LocateRegistry.getRegistry("localhost", 55545);
		server = (RegistrarInterface) myRegistry.lookup("Registrar"); 
		this.visitor = null;
	}
	
	public Visitor getVisitor() throws RemoteException {
		return this.visitor;
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
		return server.registerVisitor(v);
	}
	
	public void getTokens() throws RemoteException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		ArrayList<byte[]> ans = server.getTokensVisitor(visitor.getPhoneNumber(), visitor.getPublicKey());
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.PRIVATE_KEY, visitor.getPrivateKey());
		for(byte[] b:ans) {
			byte[] decrypt = cipher.doFinal(b);
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

}
