package cateringFacility;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Main {
/*
 * private int businessNumber;
	private String name;
	private String adress;
	private String phoneNumber;

 */
	
	 
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ServerInterface server;
		
		System.out.println("Please enter business number: ");
		int businessNumber = sc.nextInt();
		System.out.println("Please enter business name: ");
		String name = sc.nextLine();
		System.out.println("Please enter business adress: ");
		String adress = sc.nextLine();
		System.out.println("Please enter business phoneNumber: ");
		String phoneNumber = sc.nextLine();
		
		try {
			CateringFacility cateringFacility = new CateringFacility(businessNumber, name, adress, phoneNumber);
			cateringFacility.testConnection("Test from cateringside");
			
			Registry myRegistry = LocateRegistry.getRegistry("localhost", 55545);
			server = (ServerInterface) myRegistry.lookup("server"); 
			
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
		
		
		

	}

}
