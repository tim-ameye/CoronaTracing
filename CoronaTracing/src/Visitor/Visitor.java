package Visitor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Visitor  extends UnicastRemoteObject implements VisitorInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1915407624181236202L;
	
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private KeyPair keyPair;
	
	public Visitor(String firstName, String lastName, String phoneNumber) throws RemoteException, NoSuchAlgorithmException {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public PublicKey getPublicKey() {
		return keyPair.getPublic();
	}
	
	public PrivateKey getPrivateKey() {
		return keyPair.getPrivate();
	}

	@Override
	public void testConnection(String s) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alreadyRegistered() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGUI(guiVisitor t) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public guiVisitor getGUI() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
