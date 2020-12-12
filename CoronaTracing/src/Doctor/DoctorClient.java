package Doctor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import Visitor.Visit;
import matchingServer.MatchingService;
import matchingServer.MatchingServiceInterface;
import registrar.RegistrarInterface;

public class DoctorClient {
	private static Infection infection = null;
	private static ArrayList<Instant> infectionDays = null;
	private static MatchingServiceInterface server;
	
	
	public static void main(String[] args) throws NotBoundException, NoSuchAlgorithmException, IOException {
		Scanner sc = new Scanner(System.in);
		
		Registry myRegistry = LocateRegistry.getRegistry("localhost", 55547);
		server = (MatchingServiceInterface) myRegistry.lookup("MatchingService");
		Doctor doctor = new Doctor();
		server.testConnection("Hello Doctor!");
		
		infectionDays = new ArrayList<>();
		System.out.println("Please enter phone number of the patient");
		int phoneNumber = Integer.parseInt(sc.nextLine());
		System.out.println("Please enter all days on which the infected person visited a cateringFacility (Format: 2018-12-30T09:00:00Z)");
		while(sc.hasNext()) {
			Instant day = Instant.parse(sc.nextLine());
			infectionDays.add(day);
		}
		
		System.out.println("Thanks Doctor, we will send this to the server!");
		Infection encryptedInfection = addVisitsToInfection(infectionDays, phoneNumber,doctor);
		sendInfection(encryptedInfection);
	}
	
	public static Infection addVisitsToInfection(ArrayList<Instant> days, int phoneNumber, Doctor doctor) throws IOException {
		infection = new Infection();
		infection.signSignature(doctor);
		for(Instant day : days) {
			BufferedReader br = new BufferedReader(new StringReader("files\\Visitor_logs\\"+phoneNumber+".txt"));
			String line = null;
			while((line=br.readLine()) != null) {
				String[] info = line.split("_");
				Instant instant = Instant.parse(info[0]).truncatedTo(ChronoUnit.DAYS);
				if(instant == day) {
					Visit visit = new Visit(Integer.parseInt(info[1]),info[2],info[3],info[4]);
					visit.setBeginTime(instant);
					infection.add(visit);
				}
			}
		}
		SecretKey sessionKey;
		try {
			sessionKey = KeyGenerator.getInstance("AES").generateKey();
			infection = infection.encrypt(sessionKey, doctor.getKeyPair().getPublic());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return infection;
	}
	
	public static boolean sendInfection(Infection infection) {
		try {
			server.recieveInfectedUserToken(infection);
		} catch (InvalidKeyException | FileNotFoundException | NoSuchAlgorithmException | RemoteException
				| SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
