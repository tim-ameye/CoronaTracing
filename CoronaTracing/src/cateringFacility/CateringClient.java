package cateringFacility;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
		Map<Instant, byte[]> hashes = new HashMap<>();
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
			
			
			Map<Instant, byte[]> ans = server.getHashesCatering(cateringFacility.getBusinessNumber(), cateringFacility.getPhoneNumber(), cateringFacility.getPublic());
			Cipher cipherKey;
			try {
				cipherKey = Cipher.getInstance("RSA");
				cipherKey.init(Cipher.DECRYPT_MODE, cateringFacility.getPrivate());
				SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(ans.get(Instant.EPOCH)), "AES");
				Cipher cipherToken = Cipher.getInstance("AES");
				cipherToken.init(Cipher.DECRYPT_MODE, sessionKey);
				for(Map.Entry<Instant, byte[]> entry : ans.entrySet()) {
					if(!entry.getKey().equals(Instant.EPOCH)) {
						byte[] decrypt = cipherToken.doFinal(entry.getValue());
						hashes.put(entry.getKey(), decrypt);
					}
				}
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cateringFacility.setHashes(hashes);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		Date date = new Date(System.currentTimeMillis());
		Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		
		byte[] currentToken = cateringFacility.getCurrentToken();
		String tokenAsString = Base64.getEncoder().encodeToString(currentToken);
		
		// writing the tokes as a string to a qr code located at QRCodes/Brn+Buisnissnummer+Date.png
		try {
			
			System.out.println("[SYSTEM] Trying to make the QR code with token " + tokenAsString);
			cateringFacility.generateQRCodeImage(tokenAsString, 200 , 200, "./QRCodes/"+businessNumber+"/Bnr"+businessNumber +"D"+ currentDay.toString().substring(0, 10) +".png");
			System.out.println("[SYSTEM] qr code should be found at: ./QRCodes/Bnr"+businessNumber + "D" + currentDay.toString().substring(0, 10) +".png");
			
		} catch (WriterException e) {
			System.out.println("[SYSTEM] Could not generate QR Code, WriterException :: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("[SYSTEM] Could not generate QR Code, IOException :: " + e.getMessage());
		}
		



		sc.close();
	}

}
