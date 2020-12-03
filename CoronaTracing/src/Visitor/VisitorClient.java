package Visitor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.Vector;

import cateringFacility.CateringFacility;
import cateringFacility.RegistrarInterface;

public class VisitorClient extends UnicastRemoteObject implements VisitorInterface{
	private Visitor visitor;
	private guiVisitor ui;
	
	public VisitorClient(Visitor visitor) throws RemoteException {
		this.visitor = visitor;
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
	
	/*public static void main(String[] args) {
		RegistrarInterface server;
		Scanner sc = new Scanner(System.in);
		
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
		}
	}*/

}
