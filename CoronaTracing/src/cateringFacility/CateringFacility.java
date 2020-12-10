package cateringFacility;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.rmi.RemoteException;

import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
public class CateringFacility  extends UnicastRemoteObject implements CateringInterface {
	private String businessNumber;
	private String name;
	private String adress;
	private String phoneNumber;
	private Logger logger = Logger.getLogger("CateringFacility");
	
	private Map<Instant, byte[]> hashes;
	private byte[] currentToken;
	
	public CateringFacility(String businessNumber, String name, String adress, String phoneNumber) throws RemoteException{
		this.businessNumber = businessNumber;
		this.name = name;
		this.adress = adress;
		this.phoneNumber = phoneNumber;
		hashes = new HashMap<>();
		currentToken = null;
		}
	
	@Override
	public void testConnection(String s) throws RemoteException {
		System.out.println("[CONNECTION_TEST]: "+s);
		
	}
	
	
	public byte[] getCurrentToken() {
		Date date = new Date(System.currentTimeMillis());
		Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		
		if (hashes != null) {
			if (hashes.containsKey(currentDay)) {
				currentToken = hashes.get(currentDay);
				return currentToken;
			}
			System.out.println("[CATERINGFACILITY] could not find the current day "+ currentDay + " in our hashes map. " );
		}else {
			System.out.println("[CATERINGFACILITY] the hashes variable is null, fatal error.");
		}
		return null;
	}
	
	
	public BufferedImage createQR(String barcodeText)  throws Exception{
		 	EAN13Writer barcodeWriter = new EAN13Writer();
		    BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.EAN_13, 300, 150);
		 
		    return MatrixToImageWriter.toBufferedImage(bitMatrix);
	}
	
	public void generateQRCodeImage(String text, int width, int height, String filePath)throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
	
	
	public String getBusinessNumber() {
		return businessNumber;
	}
	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Map<Instant, byte[]> getHashes() {
		return hashes;
	}

	public void setHashes(Map<Instant, byte[]> hashes) {
		this.hashes = hashes;
	}

	@Override
	public void alreadyRegistered() throws RemoteException {
		logger.info("You are already registered to the registrar, please login to continue.");
		
	}



	
	
	
	
}
