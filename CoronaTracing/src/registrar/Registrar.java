package registrar;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Registrar extends UnicastRemoteObject implements RegistrarInterface{

	protected Registrar() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

}
