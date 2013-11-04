package monitor;

import java.util.Timer;
import java.util.TimerTask;

public class testtimer {
	static boolean print = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String msg = null;
		
		if (msg == null) 
			System.out.println("Started!"+msg);
		// TODO Auto-generated method stub
		int delay = 1000;
		  Timer timer = new Timer();
		  timer.schedule(new TimerTask(){
			  public void run(){
				  if(print)
			  System.out.println("This line is printed only once.");
				  System.exit(0);
			  }
			  },delay);
		  System.out.println("Now come to this line");
//		System.exit(0);  
	}

}
