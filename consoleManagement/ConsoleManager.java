package consoleManagement;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;

import conference.Conference;
import networking.TCPconnection;
import chatManagement.ChatManager;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.service.ServiceException;
public class ConsoleManager implements Observer{
	
	private ChatManager chatManager;
	private TCPconnection currentTalking = null;
	private String OFFLINE_MODE=null;
	public ConsoleManager(String host, String port) throws UnknownHostException, MalformedURLException, ServiceException{
		chatManager = new ChatManager(host, port,this);
		
	}
	
	public void sendMessage(String message) throws Exception{
		if (currentTalking!=null){
			currentTalking.sendMessage(message);
		}
		else {
			throw new Exception("Any conversation in you not");
		}
	}
	public void removeAddr(String username) {
		chatManager.removeAddr(username);
	}
	public void printHelpMenu(){
		synchronized(System.out) {
			System.out.println("[HELP] #signup username password full name _ sign up a new user");
			System.out.println("[HELP] #login username password _ login");
			System.out.println("[HELP] #list _ list current conversations");
			System.out.println("[HELP] #add username _ add friend");
			System.out.println("[HELP] #call username _ start conversation with friend");
			System.out.println("[HELP] #talk username _ change coversation to friend");
			System.out.println("[HELP] #talk owner roomname _ change coversation to chat room");
			System.out.println("[HELP] #friend _ see list of your friend");
			System.out.println("[HELP] #request _ see list of your incoming request");
			System.out.println("[HELP] #accept username _ accept a requets from username");
			System.out.println("[HELP] #accept all _ accept all requests");
			System.out.println("[HELP] #off _ see all offline message(s)");
			System.out.println("[HELP] #whoami _ your information");
			System.out.println("[HELP] #create roomname _ create new chat room");
			System.out.println("[HELP] #invite owner roomname username _ invite user to chat room");
			System.out.println("[HELP] #join owner roomname _ join the room");
			System.out.println("[HELP] #room _ list the room");
			System.out.println("[HELP] #logout _ logout");
			System.out.println("[HELP] #quit _ exit");
			System.out.println("[HELP] #monitor _ Start monitoring the Network");
		}
	}
	public void printMyName(){
		String myName = chatManager.getMyName();
		
		if (myName.equals("tom")) {
			System.out.println("I'm tom, the Bitch");
		}
		else if (myName.equals("zen")){
			System.out.println("I'm zen, the Awesome");
		}
		else if (myName.equals("qiqi")){
			System.out.println("Irete ii ka?");
		}
		else if (myName.equals("orc")){
			System.out.println("I'm Orcun, and I have to fly to UK");
		}
		else if (myName.equals("xiao")){
			System.out.println("I'm the Cute Monster! Hahaha");
		}
		else if (myName.equals("prad")){
			System.out.println("Is there anyone out there who cares about me? [crying]");
		}
		else if (myName.equals("dip")){
			System.out.println("You are not going to eat me, right? You carnivore!!!!");
		}
		else {
			System.out.println(myName);	
		}
		
	}
	
	public void printMyInfo(){
		System.out.println(chatManager.getMyInfo());
		
	}
	public void printToScreen(String message){
		synchronized(System.out){
			System.out.println("[CHAT] "+message);
			System.out.flush();
		}
	}
	
	public void intepretCommand(String command) throws ServiceException, IOException, CommunicationException{
		String[] splitCommand = command.split(" ");
		
		/*
		 * Offline message to friend
		 */
		if (OFFLINE_MODE!=null) {
			if (!command.equals("#bye")){
				chatManager.sendOfflineMessage(OFFLINE_MODE,command);	
			}
			else {
				OFFLINE_MODE = null;
			}
		}
		else {
		/*
		 * Sign up
		 * 
		 */
		if (splitCommand[0].equals("#signup")){
			if(!chatManager.isLogin()){
				try {
					String username = splitCommand[1];
					String password = splitCommand[2];
					String fullname = splitCommand[3];
						if (splitCommand.length>4) {
							for (int i = 4;i<splitCommand.length;i++){
								fullname = fullname + " " +splitCommand[i];
							}
						}	
					chatManager.register(username,password,fullname);
					
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Syntax: #signup username password fullname");
				} 
			}
			else {
				System.out.println("Log out, you must!");
			}
		}
		/*
		 * Login
		 */
		else if (splitCommand[0].equals("#login")){
			if(!chatManager.isLogin()){
				try {
					String username = splitCommand[1];
					String password = splitCommand[2];
					chatManager.login(username, password);
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Syntax: #login username password");
				}
			}
			else {
				System.out.println("Log out, you must!");
			}
		}
		/*
		 * who am i?
		 */
		else if (splitCommand[0].equals("#whoami")){
			printMyName();
			printMyInfo();
		}
		/*
		 * search friend
		 */
		else if (splitCommand[0].equals("#search")){
			try{
			chatManager.search(splitCommand[1]);
			} catch(IndexOutOfBoundsException e){
				System.out.println("Syntax: #search name");
			}
		}
		/*
		 * adding friend
		 */
		else if (splitCommand[0].equals("#add")){
			if (chatManager.isLogin()){
				try {
					chatManager.addfriend(splitCommand[1]);
				} catch(IndexOutOfBoundsException e) {
					System.out.println("Syntax: #add username");
				}
			}else {
				System.out.println("Log in first, you must");
			}
		}
		/*
		 * listing friend request
		 */
		else if (splitCommand[0].equals("#request")){
			if(chatManager.isLogin()){
				chatManager.viewRequest();
			}else {
				System.out.println("Log in first, you must");
			}
			
		}
		/*
		 * friend list
		 */
		else if (splitCommand[0].equals("#friend")){
			if(chatManager.isLogin()){
				chatManager.listFriend();
			}else {
				System.out.println("Log in first, you must");
			}
			
		}
		
		/*
		 * accept a request
		 */
		else if (splitCommand[0].contains("#accept")){
			if(chatManager.isLogin()){
				if (splitCommand[0].equals("#acceptall")){
					chatManager.acceptall();
				}
				else {
					try{
						chatManager.accept(splitCommand[1]);
					}catch (IndexOutOfBoundsException e){
						System.out.println("Syntax: #accept username");
					}
				}
				
				
			}else {
				System.out.println("Log in first, you must");
			}
		}
		/*
		 * create a new room
		 */
		else if (splitCommand[0].equals("#create")){
			if(chatManager.isLogin()){
				try{
					chatManager.createRoom(splitCommand[1]);
				}catch (IndexOutOfBoundsException e){
					System.out.println("Syntax: #create room name");
				}
			}else {
				System.out.println("Log in first, you must");
			}
		}
//		/*
//		 * list the room
//		 */
//		else if (splitCommand[0].equals("#room")){
//			if(chatManager.isLogin()){
//					chatManager.ListRoom();
//					
//			}else {
//				System.out.println("Log in first, you must");
//			}
//		}
		else if (splitCommand[0].equals("#join")){
			if(chatManager.isLogin()){
				try{
					chatManager.joinConfInFingerTable(splitCommand[1],splitCommand[2]);
				}catch (IndexOutOfBoundsException e){
					System.out.println("Syntax: #join owner room");
				}
			}else {
				System.out.println("Log in first, you must");
			}
		}
		else if (splitCommand[0].equals("#invite")){
			if(chatManager.isLogin()){
				try{
					chatManager.inviteConf(splitCommand[1],splitCommand[2],splitCommand[3]);
				}catch (IndexOutOfBoundsException e){
					System.out.println("Syntax: #invite owner room username");
				}
			}else {
				System.out.println("Log in first, you must");
			}
		}
		/*
		 * retrieve offline message
		 */
		else if (splitCommand[0].equals("#off")){
			if(chatManager.isLogin()){
				chatManager.offLineMessage();
			}else {
				System.out.println("Log in first, you must");
			}
		}
		/*
		 *  switch talk
		 */
		else if (splitCommand[0].equals("#talk")){
			if (chatManager.isLogin()){
				try {
					chatManager.switchtalk(splitCommand[1],splitCommand[2]);
					
					} catch (IndexOutOfBoundsException e){
					try {
						chatManager.switchtalk(splitCommand[1], null);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						System.out.println("[ConfChat] #syntax: #talk username");
					}	
					} catch (Exception e) {
						// TODO Auto-generated catch block
//						System.out.println("[ConfChat] #syntax: #talk username");
					}
			}
			else {
				System.out.println("Log in first, you must");
			}
		}
		/*
		 * list currently talking people
		 */
		else if (splitCommand[0].equals("#list")){
			if (chatManager.isLogin()){
				chatManager.listConnection();	
				chatManager.ListRoom();
			}
			else {
				System.out.println("Log in first, you must");
			}
		}
		/*
		 * talk to whom?
		 */
		else if (splitCommand[0].equals("#who")){
			if (chatManager.isLogin()){
				chatManager.talkToWho();	
			}
			else {
				System.out.println("Log in first, you must");
			}
		
		}
		/*
		 * log out
		 */
		else if (splitCommand[0].equals("#logout")){
			if(chatManager.isLogin()){
				chatManager.logout();
				System.out.println("[ConfChat] Successfully log out, you did.");
			}
			else {
				System.out.println("Been out already, you have");
			}
		}
		/*
		 * call someone (start chatting)
		 */
		else if (splitCommand[0].equals("#call")) {
			if(chatManager.isLogin()){
				try {
				if(!chatManager.call(splitCommand[1])){
					System.out.println("[Confchat] Start typing offline message to your friend. End with #bye");
					OFFLINE_MODE=splitCommand[1];
				}
				} catch (IndexOutOfBoundsException e){
					
				}
			}
			else {
				System.out.println("Log in first, you must");
			}
		}
		/*
		 * monitor
		 */
		else if (splitCommand[0].equals("#monitor")){
			chatManager.monitor();
		}
		/*
		 * bye
		 */
		else if(splitCommand[0].equals("#bye")){
			if(chatManager.isLogin()){
				try {
					chatManager.byebye();
				} catch (Exception e) {
					// TODO Auto-generated catch block
				
				}
			}
			else {
				System.out.println("Log in first, you must");
			}
		}
		/*
		 * quit
		 */
		else if (splitCommand[0].equals("#quit")){
			if(chatManager.isLogin()){
				chatManager.logout();
				System.out.println("[ConfChat] Successfully log out, you did.");
			}
			chatManager.closeAll();
			System.exit(0);
		}
		else if (splitCommand[0].equals("#help")){
			printHelpMenu();
		}
		else if (splitCommand[0].equals("#print")){
			chatManager.print();
		}
		else {
			if(chatManager.isLogin()){
				chatManager.sendMessage(command);	
			}
			
		}
		}
	}

	@Override
	public void update(Observable o, Object obj) {
		if (o instanceof TCPconnection){
			BlockingQueue<String> messageQueue = ((TCPconnection)o).getMessageQueue();
			while (!messageQueue.isEmpty()){
				synchronized(System.out){
					try {
						String msg = messageQueue.take();
						if (msg.equals("[CHAT] Conversation closed.")){
							chatManager.stop((TCPconnection) o);
						}
						System.out.println(msg);
					} catch (InterruptedException e) {
						System.out.println("Print to screen I failed.");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.flush();
				}
			}
		}
		if (o instanceof Conference){
			BlockingQueue<String> messageQueue = ((Conference)o).getMessageQueue();
			while (!messageQueue.isEmpty()){
				synchronized(System.out){
					try {
						String msg = messageQueue.take();
						if (msg.equals("[Conf] Conversation closed.")){
							chatManager.stop((TCPconnection) o);
						}
						System.out.println(msg);
					} catch (InterruptedException e) {
						System.out.println("Print to screen I failed.");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.flush();
				}
			}
		}
		
	}

	public void setInvitation(String obj) {
		// TODO Auto-generated method stub
		chatManager.setInvitation(obj);
	}

}
