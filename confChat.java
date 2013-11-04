import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import consoleManagement.ConsoleManager;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.service.ServiceException;


public class confChat {


	public static void main(String[] args) throws IOException, ServiceException, CommunicationException {
		
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String host = null;
		String port = null;
		if (args.length < 2){
			port = args[0];
		}
		else if (args.length == 2){
			port = args[0];
			host = args[1];
		}
		else {
			System.out.println("To create new: no args");
			System.out.println("To join a DHT: IPaddr port");
		}
		ConsoleManager consoleManager = new ConsoleManager(host,port);
		System.out.println("[ConfChat] Ready to receive input");
		while(true){
			String consoleInput = stdIn.readLine();
			consoleManager.intepretCommand(consoleInput);
		}
	}

}
