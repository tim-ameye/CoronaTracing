package cateringFacility;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.rmi.RemoteException;

import java.rmi.server.UnicastRemoteObject;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;


import registrar.Hash;
public class CateringFacility implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7242604888549206510L;
	
	private String businessNumber;
	private String name;
	private String adress;
	private String phoneNumber;
	private KeyPair keyPair;
	private Hash hash;
	private String sessionKey;
	
	public CateringFacility() {
		
	}
	
	public CateringFacility(String businessNumber, String name, String adress, String phoneNumber) throws RemoteException{
		this.businessNumber = businessNumber;
		this.name = name;
		this.adress = adress;
		this.phoneNumber = phoneNumber;
		try {
			keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	public PublicKey getPublic() {
		return keyPair.getPublic();
	}
	
	public PrivateKey getPrivate() {
		return keyPair.getPrivate();
	}
	
	public void setHash(Hash hash) {
		this.hash = hash;
	}
	
	public String getCurrentToken() {
		Date date = new Date(System.currentTimeMillis());
		Instant currentDay = date.toInstant().truncatedTo(ChronoUnit.DAYS);
		
		if (hash != null) {
			return hash.getPseudonyms().get(currentDay);
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

	public Hash getHashes() {
		return hash;
	}

	public void setHashes(Hash hash) {
		this.hash = hash;
	}

	public String toString() {
		String catering = businessNumber + "_" + name + "_" + adress + "_" + phoneNumber;
		return catering;
	}
	public String toStringFileName() {
		return businessNumber + "_" + phoneNumber;
	}

	public CateringFacility encrypt(SecretKey sessionKey, PublicKey publicKey) {
		CateringFacility encrypted = new CateringFacility();
		try {
			Cipher encryptText = Cipher.getInstance("AES");
			encryptText.init(Cipher.ENCRYPT_MODE, sessionKey);
			byte[] busByte = encryptText.doFinal(businessNumber.getBytes());
			encrypted.businessNumber = Base64.getEncoder().encodeToString(busByte);
			byte[] nameByte = encryptText.doFinal(name.getBytes());
			encrypted.name = Base64.getEncoder().encodeToString(nameByte);
			byte[] streetByte = encryptText.doFinal(adress.getBytes());
			encrypted.adress = Base64.getEncoder().encodeToString(streetByte);
			byte[] phoneByte = encryptText.doFinal(phoneNumber.getBytes());
			encrypted.phoneNumber = Base64.getEncoder().encodeToString(phoneByte);
			Cipher encryptSession = Cipher.getInstance("RSA");
			encryptSession.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedSessionKey = encryptSession.doFinal(sessionKey.getEncoded());
			encrypted.sessionKey = Base64.getEncoder().encodeToString(encryptedSessionKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}
	
	public CateringFacility decrypt(PrivateKey privateKey) {
		CateringFacility decrypted = new CateringFacility();
		try {
			Cipher cipherKey = Cipher.getInstance("RSA");
			cipherKey.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] encryptedSessionKey = Base64.getDecoder().decode(this.sessionKey);
			SecretKey sessionKey = new SecretKeySpec(cipherKey.doFinal(encryptedSessionKey), "AES");
			Cipher cipherToken = Cipher.getInstance("AES");
			cipherToken.init(Cipher.DECRYPT_MODE, sessionKey); 
			byte[] busByte = cipherToken.doFinal(Base64.getDecoder().decode(businessNumber));
			decrypted.businessNumber = new String(busByte);
			byte[] nameByte = cipherToken.doFinal(Base64.getDecoder().decode(adress));
			decrypted.name = new String(nameByte);
			byte[] streetByte = cipherToken.doFinal(Base64.getDecoder().decode(name));
			decrypted.adress = new String(streetByte);
			byte[] phoneByte = cipherToken.doFinal(Base64.getDecoder().decode(phoneNumber));
			decrypted.phoneNumber = new String(phoneByte);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
	
	
}
