package cateringFacility;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import com.google.zxing.WriterException;

import registrar.RegistrarInterface;

public class CateringClient {
/*
 * private int businessNumber;
	private String name;
	private String adress;
	private String phoneNumber;

 */

	

	
	public static void main(String[] args) {
		RegistrarInterface server;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Please enter business number: ");
		String businessNumber = sc.nextLine();
		System.out.println("Please enter business name: ");
		String name = sc.nextLine();
		System.out.println("Please enter business adress: ");
		String adress = sc.nextLine();
		System.out.println("Please enter business phoneNumber: ");
		String phoneNumber = sc.nextLine();
		CateringFacility cateringFacility = null;
		
		try {
			cateringFacility = new CateringFacility(businessNumber, name, adress, phoneNumber);
			cateringFacility.testConnection("Test from cateringside");
			
			Registry myRegistry = LocateRegistry.getRegistry("localhost", 55545);
			server = (RegistrarInterface) myRegistry.lookup("Registrar"); 
			
			server.registerCateringFacility(cateringFacility);
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
		 try {
	            cateringFacility.generateQRCodeImage("This is my first QR Code", 350, 350, "./QR.png");
	        } catch (WriterException e) {
	            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
	        } catch (IOException e) {
	            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
	        }
		
		
		

		 sc.close();
	}

}
