package Visitor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

public class Visitor extends UnicastRemoteObject implements VisitorInterface{
	private String firstName;
	private String lastName;
	private String phoneNumber;
	
	
	public Visitor(String firstName, String lastName, String phoneNumber) throws RemoteException {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
	}
	
	@Override
	public void testConnection(String s) throws RemoteException{
		System.out.println("test "+s);
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

	@Override
	public void alreadyRegistered() throws RemoteException {
		System.out.println("Already registered, login to continue.");
		
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

	@Override
	public void updateUI(Vector v) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
