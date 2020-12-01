package cateringFacility;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CateringFacility  extends UnicastRemoteObject implements CateringInterface {
	private int businessNumber;
	private String name;
	private String adress;
	private String phoneNumber;
	
	public CateringFacility(int businessNumber, String name, String adress, String phoneNumber) throws RemoteException{
		this.businessNumber = businessNumber;
		this.name = name;
		this.adress = adress;
		this.phoneNumber = phoneNumber;
	}
	
	@Override
	public void testConnection(String s) throws RemoteException {
		System.out.println("test: "+s);
		
	}
	
	
	public int getBusinessNumber() {
		return businessNumber;
	}
	public void setBusinessNumber(int businessNumber) {
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

	
	
	
	
}
