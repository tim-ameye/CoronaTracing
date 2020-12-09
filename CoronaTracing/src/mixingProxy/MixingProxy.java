package mixingProxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.*;

public class MixingProxy extends UnicastRemoteObject implements MixingProxyInterface {
	private Queue<Capsule> queue;
	private PrivateKey privateKey;
	private Certificate certificate;
	
	private KeyStore keyStore;
	private final static String path = "files\\keystore.jks";
	
	public MixingProxy() throws RemoteException  {
		queue = new LinkedList<>();
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
	public boolean registerVisit(Capsule capsule) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
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
