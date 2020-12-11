package matchingServer;
import java.awt.Toolkit;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple demo that uses java.util.Timer to schedule a task to execute once 5
 * seconds have passed.
 */

public class Countdown {
  Toolkit toolkit;

  Timer timer;
  MatchingService mf;
  String cfToken;
  Instant instant;
 
  public Countdown(int seconds, MatchingService mf, String cfToken, Instant instant) {
    timer = new Timer();
    this.mf = mf;
    this.cfToken = cfToken;
    this.instant = instant;
    
    timer.schedule(new RemindTask(), seconds * 1000);
  }

 

class RemindTask extends TimerTask {
    public void run() {
    		
    	StartContactingRegistrar(mf, cfToken, instant);
    	
    	//timer.cancel(); //Not necessary because we call System.exit
      System.exit(0); //Stops the AWT thread (and everything else)
    }
  }

	public void StartContactingRegistrar(MatchingService mf, String cfToken, Instant instant) {
		//TODO
	}
}
