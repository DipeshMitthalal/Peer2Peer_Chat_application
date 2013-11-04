package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import chatManagement.ChatManager;
import chordring.ChordManager;

import consoleManagement.ConsoleManager;

import entity.User;

public class TCPManager implements Runnable{
	String host = null;
	int chordPort = 0;
	int port = 0;
//	Thread listeningThread;
	private ServerSocket serversocket;
	Random randomGenerator = new Random(new Date().getTime());
	static ConsoleManager consoleManager;
	private final static Map<String, TCPconnection> runningConnections = new HashMap<String,TCPconnection>();
    static TCPconnection talkingConnection = null;
    
	
	public TCPManager(ConsoleManager consoleManager) throws UnknownHostException {
		TCPManager.consoleManager = consoleManager;
		host = InetAddress.getLocalHost().getHostAddress();
		chordPort=randomGenerator.nextInt(2000) + 5000;
		port = randomGenerator.nextInt(2000) + 17000;
	}
	public int getChordPort(){
		return chordPort;
	}
	public String getHost(){
		return host;
	}
	public int getPort(){
		return port;
	}
	public static void addRunningConnections(String username, TCPconnection connection){
		synchronized (runningConnections) {
			runningConnections.put(username, connection);
		}
	}
	public static void removeRunningConnections(String username){
		synchronized (runningConnections) {
			if(username!=null)
			runningConnections.remove(username);
		}
	}
	public void getNewTCPport(){
		port = randomGenerator.nextInt(2000) + 7000;
	}
	public static void switchTalkingConnection(String username){
		if ((runningConnections!=null)&&(runningConnections.containsKey(username))){
			if (talkingConnection!=null){
				talkingConnection.deleteObserver(consoleManager);	
			}
			talkingConnection = runningConnections.get(username);
//			System.out.println("[ConfChat] Currently talking to "+talkingConnection.getUsername());
			talkingConnection.addObserver(consoleManager);
			talkingConnection.objserverChanged();
		}
		else {
			System.out.println("[ConfChat] No such conversation");
		}
		
	}
	public boolean isCurrentTalk(String username){
		try{
		if ((talkingConnection!=null)&&(talkingConnection.getUsername().equals(username))&&(talkingConnection.getSocket().isConnected()))
			return true;
		} catch (NullPointerException e){
			
		}
		return false;
	}
	public String isCurrentTalkingAnyone(){
		if (talkingConnection!=null) return talkingConnection.getUsername();
		else return null;
	}
	
	public static void forceNewTalkingConnection() throws Exception{
		
		if (!runningConnections.isEmpty()){
			for (String username : runningConnections.keySet()){
				if (username!=null) {
					switchTalkingConnection(username);
					break;
				}
			}
		}
		else {
			talkingConnection=null;
		}
		
	}
	public boolean isInConversation(String username){
		if (runningConnections.containsKey(username)){
			return true;
		}
		else return false;
	}
	public static void forceNoCurrentTalking(){
		if (talkingConnection!=null) {
			talkingConnection.deleteObservers();
		}
		talkingConnection=null;
		
	}
	public static void startChat(User to,User from,TCPManager tcpManager) throws UnknownHostException, IOException{
		TCPconnection newConnection = new TCPconnection(to.getAddress(), to.getPort());
		newConnection.setTcpManager(tcpManager);
		newConnection.setUsername(to.getUsername());
		newConnection.sendMessage(from.getUsername());
		newConnection.sendMessage("");
		System.out.println("[ConfChat] Start new conversation with "+to.getUsername());
		newConnection.setConversationFlag(true);
		Thread connectionThread = new Thread(newConnection);
		connectionThread.start();
		addRunningConnections(to.getUsername(),newConnection);
		talkingConnection = newConnection;
		talkingConnection.addObserver(consoleManager);
		talkingConnection.objserverChanged();
		ChatManager.setCurrentTalking(to.getUsername());
		
		
	}
	public static void stopChat(TCPconnection connection) throws Exception {
		removeRunningConnections(connection.getUsername());
		forceNewTalkingConnection();
	}
		
	public static void sendMessage(String msg){
		 if(talkingConnection!=null)
		talkingConnection.sendMessage(msg);
	}
	@Override
	public void run() {
		try {
			serversocket = new ServerSocket(port);
			System.out.println("[ConfChat]on port "+port + " system starts listening ");
		
		while (true) {
			Socket client = serversocket.accept();
			TCPconnection newConnection = new TCPconnection(client,this);
			String friendname = newConnection.getFirstMsg();
			newConnection.setUsername(friendname);
			Thread connectionThread = new Thread(newConnection);
			connectionThread.start();
			if (!friendname.equals("null")) {
			addRunningConnections(friendname, newConnection);
			if ((ChatManager.getCurrentTalking()==null)) 
				{
					System.out.println("No talking now");
					switchTalkingConnection(friendname);
					ChatManager.setCurrentTalking(friendname);
				}
			}
		}
		} catch (IOException e) {
//			System.err.println(">>Could not listen on port " + port);
//			System.exit(-1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	public void closeAll() {
		if (runningConnections.size()!=0){
			for (String user : runningConnections.keySet()){
				runningConnections.get(user).sendMessage("#bye");
				runningConnections.get(user).stop();
			}
			runningConnections.clear();
		}
			try {
				serversocket.close();
			} catch (IOException e) {
			} catch (NullPointerException e){
				
			}
		
		
	}
	public void listConnection() {
		// TODO Auto-generated method stub
		System.out.println("Currently talking to "+runningConnections.size() +" friends");
		if (runningConnections.size()!=0){
			for (String username : runningConnections.keySet()){
				if (talkingConnection!=null){
					if (username.equals(talkingConnection.getUsername())){
					System.out.println(username+ " (active)");	
					}
				else {
					System.out.println(username);
				}
				}
				else {
					System.out.println(username);
				}
				
			}	
		}
		
	}
	public void sendNotification(User friend, User self,String msg) {
		// TODO Auto-generated method stub
		TCPconnection newConnection;
		newConnection = new TCPconnection(friend.getAddress(), friend.getPort());
		newConnection.sendMessage("null");
		newConnection.sendMessage(msg);
		newConnection.sendMessage("#bye");
	}
	
	public boolean available(String addr,String username){
		TCPconnection newConnection;
		Socket socket =null;
		boolean available=false;
		String[] a = addr.split(":");
		try {
			socket = new Socket(a[0],Integer.valueOf(a[1]));
			newConnection = new TCPconnection(socket,this);
			newConnection.sendMessage("null");
			newConnection.sendMessage("ping");
			if(newConnection.getFirstMsg().equals("pong")){
				available=true;
			}
			newConnection.sendMessage("#bye");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("[ConfChat] This cannot happen! Call Developer maybe!");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
		if (!available) {
			ChordManager.removeAddr(username);
		}
		return available;
	}
	
	public void removeAddr(String username) {
		// TODO Auto-generated method stub
		consoleManager.removeAddr(username);
	}
	public void setInvitation(String obj) {
		// TODO Auto-generated method stub
		consoleManager.setInvitation(obj);
	}
	
}
