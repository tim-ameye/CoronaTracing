package cateringFacility;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.google.zxing.WriterException;

public interface CateringInterface extends Remote {
	public void testConnection(String s) throws RemoteException;
	public void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException, RemoteException;
}
