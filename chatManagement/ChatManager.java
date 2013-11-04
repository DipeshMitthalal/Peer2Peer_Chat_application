package chatManagement;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Set;

import conference.Conference;
import conference.ConferenceManager;
import consoleManagement.ConsoleManager;

import monitor.Monitor;
import networking.TCPManager;
import networking.TCPconnection;
import chordMessage.EventMessage.MessageType;
import chordMessage.MessageParser;
import chordMessage.MonitorRequestMsg;
import chordring.ChordManager;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import entity.ConfInvitation;
import entity.ConferenceID;
import entity.User;
import entity.UserID;
import key.*;

public class ChatManager {
	private ChordManager chordManager;
	private TCPManager tcpManager;
	private ConferenceManager confManager;
	private User self=null;
	private boolean login=false;
	private String argHost;
	private String argPort;
	private ConsoleManager console;
	private static Monitor monitor;
	Thread listeningThread = null;
	private List<String> pendingInvitation =null; 
	private static String currentTalking=null;
	private static MessageParser messenger =null;
	private static double NumberOfMessage;
	private static long startTime = 0;
	public ChatManager(String host, String port, ConsoleManager consoleManager) throws UnknownHostException, MalformedURLException, ServiceException {
		chordManager = new ChordManager();
		tcpManager = new TCPManager(consoleManager);
		argHost=host;
		argPort=port;
		
		this.console = consoleManager;
		if  (argHost==null) createDHT(argPort);
		else joinDHT(argHost, argPort);
		monitor = new Monitor();
		messenger = new MessageParser(monitor);
		chordManager.setReceiveCallback(messenger);
	}
	
	public static String getCurrentTalking(){
		return currentTalking;
	}
	public static void setCurrentTalking(String username){
		currentTalking = username;
	}
	public void setLogin(){
		this.login=true;
	}
	public boolean isLogin(){
		return login;
	}
	public void setLogout(){
		this.login=false;
	}
	public void createDHT(String port) throws MalformedURLException, ServiceException{
		chordManager.create(tcpManager.getHost(), port);
		
	}
	public void joinDHT(String host, String port) throws MalformedURLException, ServiceException {
		chordManager.join(tcpManager.getHost(), tcpManager.getChordPort(), host, port);
		
	}
	public void leaveDHT(){
		chordManager.quit();
	}
	public void register(String username, String password,String fullname) throws ServiceException{
		if(!chordManager.existingUser(username)){
			chordManager.addUser(new User(username,password,fullname));
			System.out.println("Register successfully "+ username +" did.");
		}
		else {
			System.out.println("Choose a different name, you must");
		}
		
	}
	public void stop(TCPconnection connection) throws Exception{
		TCPManager.stopChat(connection);
	}
	public User isCallable(String friendname){
		try {
			if((chordManager.existingUser(friendname))&&!(friendname.equals(self.getUsername()))){
				if (chordManager.isFriend(self, friendname)){
					User friend = chordManager.isOnline(friendname);
					if (friend!=null){
						String address = friend.getAddress()+":"+Integer.toString(friend.getPort());
						if (tcpManager.available(address,friendname)){
							return friend;
						}
						else {
							ChordManager.removeAddr(friendname);	
//							System.out.println("[ConfChat] Your friend is not available now");
							return null;
						}
					}
					else {
//						System.out.println("[ConfChat] Not online, your friend is");
						return null;
					}
				}
				else {
//					System.out.println("[ConfChat] One does not simply chat with one who is not a FRIEND");
					return null;
				}
			}
			else {
				System.out.println("[ConfChat] Username again, you check.");
				return null;
			}
		} catch (ServiceException e) {
		}
		return null;
	}
	public boolean call(String friendname) throws ServiceException, UnknownHostException, IOException {
		if((chordManager.existingUser(friendname))&&!(friendname.equals(self.getUsername()))&&(!tcpManager.isInConversation(friendname))){
			if (chordManager.isFriend(self, friendname)){
				User friend = chordManager.isOnline(friendname);
				if (friend!=null){
					String address = friend.getAddress()+":"+Integer.toString(friend.getPort());
					if (tcpManager.available(address,friendname)){
						TCPManager.startChat(friend,self,tcpManager);
						TCPManager.forceNoCurrentTalking();
						switchtalk(friendname, null);
						
					}else {
						ChordManager.removeAddr(friendname);	
						System.out.println("[ConfChat] Your friend is not available now");
						return false;
					}
					
				}
				else {
					System.out.println("[ConfChat] Not online, your friend is");
					return false;
				}
			}
			else {
				System.out.println("[ConfChat] One does not simply chat with one who is not a FRIEND");
				return true;
			}
			
		}else {
			System.out.println("[ConfChat] Username again, you check.");
			return true;
		}
		return true;
	}
	public boolean isLogIn(String username){
		boolean isLogin = false;
		Set<Serializable> addr = chordManager.getAddr(username);
		for (Serializable a : addr) {
			if(tcpManager.available((String)a, username)){
				isLogin = true;
			}
		}
		return isLogin;
	}
	public void login(String username, String password) throws ServiceException{
		if (isLogIn(username)){
			System.out.println("[ConfChat] Other people is already logging in.");
		} else {
			tcpManager.getNewTCPport();
			self = new User(username,password,tcpManager.getHost(),tcpManager.getPort());
			if (chordManager.login(self)){
				self.setFullname(chordManager.getFullname(username));
				self.setID(ChordManager.getRingID());
				setLogin();
				chordManager.Offline(self);
				confManager = new ConferenceManager(console);
				messenger.setConfManager(confManager);
				pendingInvitation = new ArrayList<String>();
				listeningThread = new Thread(tcpManager);
				listeningThread.start();
				startTime = System.currentTimeMillis();
				NumberOfMessage = 0;
			} else {
				self = null;
			}
		}
		
		
	}
	public static void increaseNoM(){
		NumberOfMessage++;
	}
	public String getMyInfo(){
		String info = null;
		if (self!=null) {
			info = self.getUsername() + ":" + self.getFullname() + " : " + tcpManager.getHost() + ":" + tcpManager.getPort();			
		}
		return info;
	}
	public String getMyName(){
		if (self!=null){
			return self.getUsername();	
		}
		else {
			return "[ConfChat] Not log in, you are";
		}
	}

	public void logout() throws ServiceException, CommunicationException {
		// TODO Auto-generated method stub
		listeningThread.interrupt();
		chordManager.logout(self);
		
		tcpManager.closeAll();
		if (confManager != null){
			confManager.stop(self.getUserID());
		}
		confManager = null;
		self=null;
		setLogout();
	}
	public void exit() {
		/*
		 * exit functions : kill all opening sockets. 
		 */
	}

	
	public void sendMessage(String msg) {
		// TODO Auto-generated method stub
		if ((currentTalking!=null)&&(currentTalking.contains(":"))){ // talking in conference
			try {
				confManager.sendConferenceMessageInFingerTable(self.getUserID(), "["+self.getUsername()+"] "+msg);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				System.out.println("[Conference _ Error] Problems in connection");
//				try {
//					confManager.removeConf(self);
//				} catch (ServiceException e2) {
//					// TODO Auto-generated catch block
////					e2.printStackTrace();
//				} catch (CommunicationException e1) {
//					// TODO Auto-generated catch block
////					e.printStackTrace();
//				}
				currentTalking=ConferenceManager.forceNewTalkingConf();
				if(currentTalking==null) {try {
					TCPManager.forceNewTalkingConnection();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
				}
					currentTalking = tcpManager.isCurrentTalkingAnyone();}
			}
			increaseNoM();
		}
		else if (currentTalking!=null){ //talking in conversation
			TCPManager.sendMessage("["+self.getUsername()+"] "+msg);
			increaseNoM();
		}
		
	}
	public void switchtalk(String username, String roomname){
		if (roomname == null){
			TCPManager.switchTalkingConnection(username);
			currentTalking = username;	
			confManager.forceNoTalkingConf();
		}
		else {
			if (confManager.isExistingConf(username, roomname)){
			currentTalking = username+":"+roomname;
			ConferenceManager.setTalkingConference(console, username, roomname);
			TCPManager.forceNoCurrentTalking();
			}
			else {
				System.out.println("[Conference] No such conference");
			}
		}
		
	}


	public void closeAll() throws ServiceException, NullPointerException, CommunicationException {
		// TODO Auto-generated method stub
		if (confManager!=null) {
		confManager.stop(self.getUserID());
		}
		tcpManager.closeAll();
		if (self!=null){
		chordManager.logout(self);
		}
		chordManager.quit();
		
	}


	public void listConnection() {
		// TODO Auto-generated method stub
		tcpManager.listConnection();
	}


	public void search(String name) {
		// TODO Auto-generated method stub
		
		chordManager.search(name);
		
	}


	public void addfriend(String friendname) {
		// TODO Auto-generated method stub
		try {
			if((chordManager.existingUser(friendname))&&!(friendname.equals(self.getUsername()))){
		if (!chordManager.isFriend(self,friendname)){
			User friend = chordManager.isOnline(friendname);
				if (friend == null){
					System.out.println("[ConfChat] Friend not online. Sent offline request");
				}
				else {
					String msg = "friend_request_notification:"+self.getUsername();
					tcpManager.sendNotification(friend, self, msg);
					System.out.println("[ConfChat] Request sent");
				}
				chordManager.sendOfflineFriendRequest(friendname,self);
		}
		else {
			System.out.println("[ConfChat] Already friend, you are");
		}
			}
			else {
				System.out.println("[ConfChat] Is it you, or someone not even existed?");
			}
		}catch (ServiceException e){
		}
	}


	public void viewRequest() {
		// TODO Auto-generated method stub
		chordManager.viewRequest(self);
	}

	public void acceptall() {
		// TODO Auto-generated method stub
		Set<Serializable> pendinglist = chordManager.pendingList(self);
		if (pendinglist.size()!=0){
			for (Serializable friend : pendinglist) {
				System.out.println((String)friend);
				accept((String)friend);
			}
			System.out.println("[ConfChat] Accepted "+pendinglist.size()+ " friend request(s)");	
		}
		else {
			System.out.println("[ConfChat] And no f*** was given that day");
		}
	}

	public void accept(String friendname) {
		// TODO Auto-generated method stub
		try {
			if(chordManager.acceptfriend(self, friendname)){
					User friend = chordManager.isOnline(friendname);
						if (friend == null){
							String msg = self.getUsername() + " accepted your request";
							sendOfflineMessage(friendname, msg);	
						}
						else {
							String msg = "friend_request_accept_notification:"+self.getUsername();
							tcpManager.sendNotification(friend, self, msg);
						}
						System.out.println("[ConfChat] Notified friend");
				}
				
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			
		}
	}


	public void listFriend() {
		// TODO Auto-generated method stub
		int i= 0;
		Set<Serializable> list = chordManager.viewFriend(self);
		if (list.size()!=0) System.out.println("[ConfChat] Friend list of "+self.getFullname());
		for (Serializable friend : list) {
			i++;
			boolean online = false;
			String friendname = (String)friend;
			Set<Serializable> addrlist = chordManager.getAddr(friendname);
			for (Serializable addr: addrlist){
				if (tcpManager.available((String)addr,friendname)){
					online = true;
				}
			}
			if (online){
				System.out.println(i+") "+friendname+" -- ONLINE");	
			} else {
				System.out.println(i+") "+friendname+" -- OFFLINE");
			}
			
			
		}
	}


	public void offLineMessage() {
		// TODO Auto-generated method stub
		chordManager.viewOfflineMessage(self);
	}


	public void sendOfflineMessage(String oFFLINE_MODE, String msg) {
		// TODO Auto-generated method stub
		chordManager.sendOfflineMessage(oFFLINE_MODE, msg);
	}


	public void removeAddr(String username) {
		// TODO Auto-generated method stub
		ChordManager.removeAddr(username);
	}


	


	public void ListRoom() {
		// TODO Auto-generated method stub
		confManager.listRoom();
		getInvitation();
	}


	public void inviteConf(String owner, String room, String username ) {
		if(!username.equals(self.getUsername())){
			if (confManager.isExistingConf(owner, room)){
				User friend = isCallable(username);
				if (friend!=null){
					String msg ="!invitation_"+ owner + ":"+room +"_"+self.getUsername();
					System.out.println("[Conference] Invitation sent to "+friend.getUsername());
					tcpManager.sendNotification(friend, self, msg);
				}
				else {
					System.out.println("[ConfChat] Just for some reasons, God doesn't like you to invite "+username);
				}
			}
			else {
				System.out.println("[Conference] Not in the room, you are");
			}
		}
		else {
			System.out.println("[ConfChat] You cannnot invite yourself");
		}
	}


	public void setInvitation(String msg) {
		// TODO Auto-generated method stub
		pendingInvitation.add(msg);
		System.out.println("[ChatManager] "+pendingInvitation.size()+ " invitation pending");
	}
	public void getInvitation(){
		if (pendingInvitation.size()!=0){
			for (String dummy : pendingInvitation) {
				System.out.println("[Pending Invitation] "+dummy);
			}			
		}
		else {
			System.out.println("[Pending Invitation] No pending invitation");
		}
		
	}
	public boolean isInvited(String owner, String roomname){
		String roomid = owner+":"+roomname;
		for (String dummy : pendingInvitation) {
			if (dummy.equals(roomid)) return true; 
		}
		return false;
	}
	public void createRoom(String roomname) throws ServiceException {
		boolean dup=false;
		for (Conference c : confManager.getConfList()){
			if (c.getConfID().equals(new ConferenceID(self.getUsername(), roomname))){
				dup = true;
				
			}
		}
		if (chordManager.isDupRoom(self.getUsername(),roomname)){
			dup = true;
		}
		if (!dup){
			// TODO Auto-generated method stub
			ConferenceID confid = new ConferenceID(self.getUsername(), roomname);
			Conference conf = new Conference(confid, chordManager);
			conf.addMySelf(self.getUserID());
			chordManager.createRoom(self,roomname);
			try {
				chordManager.viewRoom(self.getUsername(),roomname);
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
			}
			confManager.addConf(conf);
			TCPManager.forceNoCurrentTalking();
			ConferenceManager.setTalkingConference(console, self.getUsername(), roomname);
			currentTalking=self.getUsername()+":"+roomname;
		}
		else {
			System.out.println("[Conference] Cannot create 2 rooms w/  same name");
		}
	}

	public void joinConf(String owner, String roomname) throws ServiceException {
		String inv = owner+":"+roomname;
		if(pendingInvitation.contains(inv)){
			ConferenceID confid = new ConferenceID(owner, roomname);
			Conference conf = new Conference(confid, chordManager);
			chordManager.joinRoom(self, owner, roomname);
			confManager.addConf(conf);
			conf.updateParticipants(chordManager.getRoomParticipants(owner, roomname));
			try {
				conf.annouceNewcomer(self.getUserID());
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			 
			pendingInvitation.remove(inv);
			ConferenceManager.setTalkingConference(console, owner, roomname);
			currentTalking=inv;
		}
		
		else {
			System.out.println("[Conference] You are not invited");
		}
	}
	public void joinConfInFingerTable(String owner, String roomname) throws ServiceException {
		String inv = owner+":"+roomname;
		if(pendingInvitation.contains(inv)){
			ConferenceID confid = new ConferenceID(owner, roomname);
			Conference conf = new Conference(confid, chordManager);
			chordManager.joinRoom(self, owner, roomname);
			confManager.addConf(conf);
			conf.updateParticipants(chordManager.getRoomParticipants(owner, roomname));
			try {
				conf.annouceNewcomerInFingerTable(self.getUserID());
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				System.out.println("[Conference] Problems in connection");
				try {
					confManager.removeConf(self);
				} catch (CommunicationException e2) {
					// TODO Auto-generated catch block
//					e2.printStackTrace();
				}
				currentTalking=ConferenceManager.forceNewTalkingConf();
				if(currentTalking==null) {try {
					TCPManager.forceNewTalkingConnection();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
				}
					currentTalking = tcpManager.isCurrentTalkingAnyone();}
				
			}
			pendingInvitation.remove(inv);
			ConferenceManager.setTalkingConference(console, owner, roomname);
			currentTalking=inv;
		}
		
		else {
			System.out.println("[Conference] You are not invited");
		}
	}


	public void byebye() throws Exception {
		System.out.println("saying goodbye "+currentTalking+ "...");
		if (currentTalking.contains(":")){
			confManager.removeConf(self);
			currentTalking=ConferenceManager.forceNewTalkingConf();
			if(currentTalking==null) {TCPManager.forceNewTalkingConnection();
				currentTalking = tcpManager.isCurrentTalkingAnyone();}
		}else {
			TCPManager.sendMessage("["+self.getUsername()+"] #bye");
			TCPManager.removeRunningConnections(tcpManager.isCurrentTalkingAnyone());
			TCPManager.forceNewTalkingConnection();
			currentTalking = tcpManager.isCurrentTalkingAnyone();
			if (currentTalking==null)  {currentTalking=ConferenceManager.forceNewTalkingConf();}
		}
		if (currentTalking==null) System.out.println("[ConfChat] No more conversation");
		
	}


	public void print() {
		// TODO Auto-generated method stub
		chordManager.print();
	}

	public void monitor() {
		// TODO Auto-generated method stub
		ID myid= ChordManager.getRingID();
		monitor.receive(new MonitorRequestMsg(myid, myid, myid, chordMessage.MonitorMessage.MessageType.Monitor_REQUEST));
	}

	public static double getMsgRate(){
		if(startTime == 0){
			return 0;
		}
		else {
			long currentTime = System.currentTimeMillis();
			return (NumberOfMessage*1000)/(currentTime-startTime);
		}
	}

	public void talkToWho() {
		// TODO Auto-generated method stub
		if (currentTalking!=null) {
			System.out.println("From tcp:" + tcpManager.isCurrentTalkingAnyone());
			System.out.println("Currently talking to "+currentTalking);
			if (confManager.isTalking()!=null)
			System.out.println("From conf:" +confManager.isTalking().getConfID().getRoomName());
		}
		else {
			
		}
	}
	
	
	
}
