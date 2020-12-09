package cateringFacility;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
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
		Map<Instant, byte[]> hashes;
		try {
			cateringFacility = new CateringFacility(businessNumber, name, adress, phoneNumber);
			cateringFacility.testConnection("Test from cateringside");

			Registry myRegistry = LocateRegistry.getRegistry("localhost", 55545);
			server = (RegistrarInterface) myRegistry.lookup("Registrar"); 

			if (server.registerCateringFacility(cateringFacility)) {
				System.out.println("[REGISTRAR] We registered the new cateringfacility!");
			}else {
				System.out.println("[REGISTRAR] You are already registrated to the registrar, please try to log in.");
				server.loginCF(cateringFacility);
			}
			
			hashes = server.getHashesCatering(businessNumber, phoneNumber);
			
			cateringFacility.setHashes(hashes);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
		byte[] currentToken = cateringFacility.getCurrentToken();
		String tokenAsString = currentToken.toString();
		
		
		try {
			cateringFacility.generateQRCodeImage(tokenAsString, 350, 350, "./QR.png");
		} catch (WriterException e) {
			System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
		}




		sc.close();
	}

}
