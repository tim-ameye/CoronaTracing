package registrar;

import java.rmi.RemoteException;

public interface RegistrarInterface {

	public boolean registerCateringFacility() throws RemoteException; 	//TODO CF-interface opvragen
	public boolean loginCF() throws RemoteException;
	
	
	public boolean registerUser() throws RemoteException;				//TODO User-interface opvragen
	public boolean loginUser() throws RemoteException;
}
