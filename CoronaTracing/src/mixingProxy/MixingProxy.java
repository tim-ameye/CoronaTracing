package mixingProxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MixingProxy extends UnicastRemoteObject implements MixingProxyInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8870169243620905534L;
	private Queue<Capsule> queue;
	private PrivateKey privateKey;
	private Certificate certificate;
	private ArrayList<String> signedTokensToday;

	private KeyStore keyStore;
	private final static String path = "files\\keystore.jks";

	public MixingProxy() throws RemoteException {
		queue = new LinkedList<>();
		signedTokensToday = new ArrayList<>();
		try {
			this.keyStore = KeyStore.getInstance("JKS");
			char[] password = "AVB6589klp".toCharArray();
			FileInputStream fis;
			fis = new FileInputStream(path);
			keyStore.load(fis, password);
			privateKey = (PrivateKey) keyStore.getKey("mixingproxy", password);
			certificate = keyStore.getCertificate("mixingproxy");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String registerVisit(Capsule capsule) throws RemoteException {
		
		// 1: validate of the user token
		// byte[] userToken = capsule.getUsertoken();
		try {
			Cipher cipherText = Cipher.getInstance("RSA");
			cipherText.init(Cipher.ENCRYPT_MODE, privateKey);
			
			Certificate certificate = keyStore.getCertificate("registrar");
			PublicKey publicKey = certificate.getPublicKey();
			Signature sig = Signature.getInstance("SHA512withRSA");
			sig.initVerify(publicKey);
			sig.update(capsule.getUnsignedBytes());
			boolean b = sig.verify(capsule.getSignedBytes());
			if (!b)
				return Base64.getEncoder().encodeToString(cipherText.doFinal("Token not valid".getBytes()));
			
			// 2: userToken is voor de huidige dag
			Instant today = new Date(System.currentTimeMillis()).toInstant().truncatedTo(ChronoUnit.DAYS);
			byte[] unsigned = capsule.getUnsignedBytes();
			byte[] dateBytes = new byte[unsigned.length - 64];
			for (int i = 64; i < unsigned.length; i++) {
				dateBytes[i - 64] = unsigned[i];
			}
			Instant day = Instant.parse(new String(dateBytes));
			if (!day.equals(today)) {
				System.out.println("A token is used at a wrong day!");
				return Base64.getEncoder().encodeToString(cipherText.doFinal("Wrong day".getBytes()));
			}
			// 3: werd nog niet eerder gebruikt
			if(signedTokensToday.contains(capsule.getUserTokenSigned())){
				System.out.println("A token has tried to be used twice!");
				return Base64.getEncoder().encodeToString(cipherText.doFinal("Token already used".getBytes()));
			}
			
			// 4: alle 3 voldaan, sign qrToken en stuur antwoord terug. niet-voldaan: return
			// false
			signedTokensToday.add(capsule.getUserTokenSigned());
			return Base64.getEncoder().encodeToString(cipherText.doFinal("Accepted".getBytes()));
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void setGui(GuiMixingProxy gmp) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public GuiMixingProxy getGuiMixingProxy() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
