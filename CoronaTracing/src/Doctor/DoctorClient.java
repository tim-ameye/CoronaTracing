package Doctor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
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
	private Infection infection = null;
	private ArrayList<Instant> infectionDays = null;
	private MatchingServiceInterface server;
	private PublicKey matchingServicePubKey;
	
	public DoctorClient() {
		infection = new Infection();
		infectionDays = new ArrayList<>();
	}
	
	public Infection getInfection() {
		return infection;
	}
	public void setInfection(Infection infection) {
		this.infection = infection;
	}

	public ArrayList<Instant> getInfectionDays() {
		return infectionDays;
	}

	public void setInfectionDays(ArrayList<Instant> infectionDays) {
		this.infectionDays = infectionDays;
	}

	public MatchingServiceInterface getServer() {
		return server;
	}

	public void setServer(MatchingServiceInterface server) {
		this.server = server;
	}

	public PublicKey getMatchingServicePubKey() {
		return matchingServicePubKey;
	}

	public void setMatchingServicePubKey(PublicKey matchingServicePubKey) {
		this.matchingServicePubKey = matchingServicePubKey;
	}

	public static void main(String[] args) throws NotBoundException, NoSuchAlgorithmException, IOException {
		Scanner sc = new Scanner(System.in);
		
		DoctorClient dc = new DoctorClient();
		
		Registry myRegistry = LocateRegistry.getRegistry("localhost", 55547);
		dc.setServer((MatchingServiceInterface) myRegistry.lookup("MatchingService"));
		Doctor doctor = new Doctor();
		
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("JKS");
			char[] password = "AVB6589klp".toCharArray();
			FileInputStream fis = new FileInputStream("files\\keystore.jks");
			keyStore.load(fis, password);
			
			dc.setMatchingServicePubKey(keyStore.getCertificate("matchingservice").getPublicKey());
			doctor.setPrivateKey((PrivateKey) keyStore.getKey("doctor", password));
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		System.out.println("Please enter phone number of the patient");
		String phoneNumber = sc.nextLine();
		System.out.println("Please enter all days on which the infected person visited a cateringFacility (Format: 2018-12-30T09:00:00Z)");
		boolean stop = false;
		while(!stop) {
			String line= sc.nextLine();
			if(line.equals("Stop")) stop = true;
			else {
				Instant day = Instant.parse(line);
				dc.addInfectionDays(day);
			}
		}
		
		System.out.println("Thanks Doctor, we will send this to the server!");
		Infection encryptedInfection = dc.addVisitsToInfection(phoneNumber,doctor);
		dc.sendInfection(encryptedInfection);
	}
	
	public void addInfectionDays(Instant day) {
		infectionDays.add(day);
	}
	
	public Infection addVisitsToInfection(String phoneNumber, Doctor doctor) throws IOException {
		infection = new Infection();
		infection.signSignature(doctor);
		for(Instant day : infectionDays) {
			Scanner sc = new Scanner(new File("files\\VisitorLogs\\"+phoneNumber+".txt"));
			while(sc.hasNextLine()) {
				String[] info = sc.nextLine().split("_");
				Instant instant = Instant.parse(info[0]).truncatedTo(ChronoUnit.DAYS);
				if(instant.equals(day.truncatedTo(ChronoUnit.DAYS))) {
					Visit visit = new Visit(Integer.parseInt(info[1]),info[2],info[3],info[5]);
					visit.setBeginTime(instant);
					infection.add(visit);
				}
			}
		}
		SecretKey sessionKey;
		try {
			sessionKey = KeyGenerator.getInstance("AES").generateKey();
			infection = infection.encrypt(sessionKey, matchingServicePubKey);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return infection;
	}
	
	public boolean sendInfection(Infection infection) {
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
