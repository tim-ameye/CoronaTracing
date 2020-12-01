package cateringFacility;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.google.zxing.WriterException;

public interface CateringInterface extends Remote {
	public void testConnection(String s) throws RemoteException;
	public String getBusinessNumber() throws RemoteException;
	public String getName() throws RemoteException;
	public String getAdress() throws RemoteException;
	public String getPhoneNumber() throws RemoteException;
	public void alreadyRegistered() throws RemoteException;
	public void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException, RemoteException;
}
