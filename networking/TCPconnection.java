package networking;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.LinkedTransferQueue;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import entity.ConfInvitation;

public class TCPconnection extends Observable implements Runnable{
	private Socket socket;
	private String username=null;
	private PrintWriter out;
	private BufferedReader in;
	private boolean conversation=false;
	TCPManager tcpManager;
	BlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();
	
	public TCPconnection(Socket socket, BufferedReader in, PrintWriter out){
		this.socket = socket;
		this.out = out;
		this.in = in;
	}
	
	public TCPconnection(String host,int port){
		try {
		this.socket = new Socket(host,port);
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch(IOException e) {
			System.out.println("Oppsssss!");
		} 
	}
	
	public TCPconnection(Socket socket,TCPManager tcpManager){
		this.socket = socket;
		this.tcpManager = tcpManager;
		try {
			out = new PrintWriter(this.socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Opps!");
		}
		
	}
	public void setConversationFlag(boolean flag){
		conversation = flag;
	}
	public void setTcpManager(TCPManager tcpManager){
		this.tcpManager = tcpManager;
	}
	public PrintWriter getPrintWriter(){
		return out;
	}
	public BufferedReader getBufferedReader(){
		return in;
	}
	public Socket getSocket(){
		return socket;
	}
	public BlockingQueue<String> getMessageQueue(){
		return messageQueue;
	}
	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username = username;
	}
	public void sendMessage(String message){
		if (!socket.isClosed()&&socket.isConnected()){
			out.println(message);
			out.flush();	
		}
		else {
			System.out.println("[ConfChat] Cannot send");
		}
		
	}
	
	public String getFirstMsg(){
		try {
			return in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
		return null;
		
	}
	public void objserverChanged() {
        setChanged();
        notifyObservers();
    }
	public void stop() {
		try{
		out.close();
		in.close();
		socket.close();
		} catch(IOException e) {}
	}
	@Override
	public void run() {
		
		String message=null;
		try {
		while (true){
			
			message = in.readLine();
			
			if (message!= null){
				
					if(message.contains("friend_request_notification")){
						String[] msg = message.split(":");
						System.out.println("[ConfChat] Friend request from "+msg[1]);
					}
					else if (message.equalsIgnoreCase("ping")){
						sendMessage("pong");
					}
					else if (message.contains("friend_request_accept_notification")){
						String[] msg= message.split(":");
						System.out.println(msg[1] +" accepted your friend request");
					}
					else if (message.contains("!invitation")){
						String[] msg = message.split("_");
						System.out.println("[TCP] Receive invitation for room :"+msg[1] + " from "+msg[2]);
 						tcpManager.setInvitation(msg[1]);
 						
					}
					else if (message.contains("#bye")){
						sendMessage(message);
						stop();
//						System.out.println("[CHAT] Stop conversation");
						if (tcpManager.isCurrentTalk(username)){
						TCPManager.removeRunningConnections(username);
						TCPManager.forceNewTalkingConnection();
						}
						if (conversation) {	
							messageQueue.put("[CHAT] Conversation closed.");	
						}
						setChanged();
						notifyObservers();
					}
					else {
						if ((!conversation)&&!(username.equals("null"))){
							System.out.println("[ConfChat] Start new conversation with "+username);
							conversation = true;
						}
						messageQueue.put(message);
						setChanged();
						notifyObservers();
					}
				
			}
			else {
				//message == null;
				sendMessage("ping");
				String reply = in.readLine();
				if (reply==null) {
					stop();
//					System.out.println("[CHAT] Stop conversation");
					if (tcpManager.isCurrentTalk(username)){
					TCPManager.removeRunningConnections(username);
					TCPManager.forceNewTalkingConnection();
					tcpManager.removeAddr(username);
					}
					if (conversation) {	
						messageQueue.put("[CHAT] Connection sucks! Your friend died.");	
					}
					setChanged();
					notifyObservers();
				}
			}
		}
		} catch (IOException e) {
			// TODO nothing
		} 
		catch (InterruptedException e){
			// TODO: nothing
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
             
	}
    
}
