package mixingProxy;

import java.io.*;
import java.rmi.registry.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import java.util.logging.*;

public class Server {

	public static void main(String[] args) {
		Logger logger = Logger.getLogger("MixingProxy"); 
		logger.info("MixingProxy server has started");
		
		try {
			Registry registry = LocateRegistry.createRegistry(55546);
			MixingProxy mixingProxy  = new MixingProxy();
			registry.rebind("MixingProxy", mixingProxy);
			Scanner sc = new Scanner(System.in);
			String input = "";
			long currentTime = System.currentTimeMillis();
			long lastTime = System.currentTimeMillis();
			while(!input.equals("Stop")) {
				if(sc.hasNext()) {
					input = sc.nextLine();
				}
				if(currentTime > lastTime + 18000000) { //ieder halfuur
					mixingProxy.sendCapsules();
				}
			}
			registry.unbind("MixingProxy");
			sc.close();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "System has failed to start: "+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}


