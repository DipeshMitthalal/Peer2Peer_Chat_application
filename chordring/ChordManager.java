package chordring;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;

import chordMessage.ConfMessage;
import chordMessage.TextMessage;


import key.*;
import entity.*;

import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ReceiveCallback;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class ChordManager {
	private static ChordImpl chord = null;
	public ChordManager() {
        PropertiesLoader.loadPropertyFile();
    }
	public void create(String host,String port) throws MalformedURLException, ServiceException{
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL localURL = null;
		localURL = new URL(protocol+"://"+host+":"+port+"/");
		chord = new ChordImpl();
		chord.create(localURL);
		System.out.println("Created DHT with URL: "+localURL.toString());
	}
	
	public void join(String host, int port,String bootstrapHost,String bootstrapPort) throws MalformedURLException, ServiceException {
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL localURL = null; 
		URL bootstrapURL = null;
		localURL = new URL(protocol+"://"+host+":"+Integer.toString(port)+"/");
		bootstrapURL = new URL(protocol+"://"+bootstrapHost+":"+bootstrapPort+"/");
		chord = new ChordImpl();
		chord.join(localURL,bootstrapURL);
		System.out.println("Joined DHT with URL: "+localURL.toString());
	}
	public void quit(){
		try {
			
			
			chord.leave();
			
		} catch (ConcurrentModificationException e) {
			
		}
	}
	
	public boolean existingUser(String username) throws ServiceException {
		UserKeyPassword key = new UserKeyPassword(username);
		if (chord.retrieve(key).toString().equals("[]")){
			return false;	
		}
		else return true;
	}
	
	public void logout(User self) throws ServiceException {
		UserKeyAddr key = new UserKeyAddr(self.getUsername());
		Set<Serializable> addr = chord.retrieve(key);
		for (Serializable a : addr){
		chord.remove(key,a);
		}
	}
	public void signup(String username, String password) throws ServiceException{
		if (!existingUser(username)){
			UserKeyPassword key = new UserKeyPassword(username);
			chord.insert(key,password);
			System.out.println(username +" registered successfully");
		}
		else {
			System.out.println("User existed! Another user name, you choose");
		}
	}
	public String getFullname(String username) throws ServiceException{
		UserKeyFullName key = new UserKeyFullName(username);
		String fullname = null;
		Set<Serializable> list = chord.retrieve(key);
		for (Serializable name : list) {
			fullname = ((RegUser)name).getFullName();
		}
		return fullname;
		
	}
	public boolean login(User self) throws ServiceException{
		UserKeyPassword key = new UserKeyPassword(self.getUsername());
		String password = "["+self.getPassword()+"]";
		if (password.equals(chord.retrieve(key).toString())){
			String contact = self.getAddress()+":"+Integer.toString(self.getPort());
//			System.out.println(contact);
			UserKeyAddr keyaddr = new UserKeyAddr(self.getUsername());
			Set<Serializable> oldContact = chord.retrieve(keyaddr);
			if (oldContact.size()!=0){
				for (Serializable o : oldContact) {
					String oc = (String)o;
					chord.remove(keyaddr, oc);	
				}	
			}			  
			chord.insert(keyaddr, contact);
			System.out.println("[ConfChat] Successfully log in, you did.");
			return true;
		}
		else {
			System.out.println("Wrong user name or password");
			return false;
		}
	}
	
	public void addfriend(User me, String friendname) throws ServiceException{   //I add friend as my friend
		if (!isFriend(me,friendname)){
			/*
			 * do something here dude.
			 */
		}
		else {
			System.out.println("Already friend you are");
		}
	}
	public boolean acceptfriend(User me, String friendname) throws ServiceException{
		
		UserKeyPendingFL key = new UserKeyPendingFL(me.getUsername());
		Set<Serializable> pendinglist = chord.retrieve(key);
		for (Serializable friend : pendinglist){
			if (friendname.equals(((String)friend))){
				chord.remove(key, friendname); //remove the request
				UserKeyFL key2 = new UserKeyFL(me.getUsername());
				chord.insert(key2, friendname);//insert in my friend list
				UserKeyFL key3 = new UserKeyFL(friendname);
				chord.insert(key3, me.getUsername()); //insert in his friend list
				System.out.println("[ConfChat] Add "+friendname+ " to your friend list");
				return true;
			}
		}
		
		return false;
	}
	public Set<Serializable> pendingList(User me) {
		UserKeyPendingFL key = new UserKeyPendingFL(me.getUsername());
		Set<Serializable> list=null;
		list = chord.retrieve(key);
		return list;
	}
	public boolean isFriend(User me,String friendname) throws ServiceException{ //is friendname a friend of me?
		UserKeyFL key = new UserKeyFL(me.getUsername());
		Set<Serializable> myfriendlist = chord.retrieve(key);
//		me.updateFriendList(myfriendlist);
		for (Serializable friend : myfriendlist){
			String name = ((String)friend);
//			System.out.println("[ChordManager] "+name);
			if (name.equalsIgnoreCase(friendname)){
				return true;
			}
		}
		return false;
	}
	public User isOnline(String friendname) throws ServiceException{
		UserKeyAddr key = new UserKeyAddr(friendname);
		String host = null;
		int port = 0;
		User friend = null;
		Set<Serializable> address = chord.retrieve(key);
			for(Serializable addr : address){
				String[] splitAddr = ((String)addr).replaceAll("[\\[\\]]", "").split(":");
				host = splitAddr[0];
				port = Integer.valueOf(splitAddr[1]);
				if (!host.equals("")&&(port!=0)){
					friend = new User(friendname,host,port);
					System.out.println(host + ":"+port);
				}
			}
		return friend;
	}
	public void addUser(User user) throws ServiceException{
		UserKeyFullName keyFullName = new UserKeyFullName(user.getUsername());
		UserKeyPassword keyPassword = new UserKeyPassword(user.getUsername());
		
		chord.insert(keyFullName, new RegUser(user.getFullname()));
		chord.insert(keyPassword, user.getPassword());
		String[] splitName = user.getFullname().split(" ");
		for (int i=0;i<splitName.length;i++){
			UserKey key = new UserKey(splitName[i].toLowerCase());
			String name = user.getUsername()+":"+user.getFullname();
			chord.insert(key, name);
		}
	}
	
	public void setReceiveCallback(ReceiveCallback callback) {
        ((ChordImpl)chord).setRecvCallBack(callback);
    }
	
	
	public Set<Serializable> getAddr(String username){
		UserKeyAddr keyaddr = new UserKeyAddr(username);
		Set<Serializable> addrlist = null;
		addrlist = chord.retrieve(keyaddr);
		return addrlist;
	}
	public void search(String name) {
		// TODO Auto-generated method stub
		
		String user=null;
		UserKey key = new UserKey(name.toLowerCase());
		Set<Serializable> list = chord.retrieve(key);
		if (list.size()!=0){
			System.out.println("Search result for "+name);
			int i=1;
			for (Serializable u : list){
				user = (String)u;
				System.out.println(i+ ") "+user);
				i++;
			}	
			
		}
		else {
			System.out.println("No match for "+name);
		}
	}
	public void sendOfflineFriendRequest(String friendname,User self) {
		// TODO Auto-generated method stub
		UserKeyPendingFL key = new UserKeyPendingFL(friendname);
		chord.insert(key, self.getUsername());
	}
	public void viewRequest(User self) {
		// TODO Auto-generated method stub
		String user=null;
		UserKeyPendingFL key = new UserKeyPendingFL(self.getUsername());
		Set<Serializable> list = chord.retrieve(key);
		if (list.size()!=0){
			System.out.println("Pending requests for "+self.getFullname());
			int i=1;
			for (Serializable u : list){
				user = (String)u;
				System.out.println(i+ ") "+user);
				i++;
			}	
			System.out.println("---------------------------");
		}
		else {
			System.out.println("No pending requests for "+self.getFullname());
		}
		
	}
	public Set<Serializable> viewFriend(User self) {
		// TODO Auto-generated method stub
		Set<Serializable> list = null;
		UserKeyFL key = new UserKeyFL(self.getUsername());
		list = chord.retrieve(key);
		return list;
		
	}
	public void viewOfflineMessage(User self) {
		// TODO Auto-generated method stub
		String msg=null;
		UserKeyOff key = new UserKeyOff(self.getUsername());
		Set<Serializable> list = chord.retrieve(key);
		if (list.size()!=0){
			System.out.println("Offline messages for "+self.getFullname());
			int i=1;
			for (Serializable u : list){
				msg = ((OfflineMsg)u).getOfflineMsg();
				System.out.println(i+ ") "+msg);
				chord.remove(key, u);
				i++;
			}	
			System.out.println("---------------------------");
			System.out.println("[ConfChat] Offline messages deleted.");
		}
		else {
			System.out.println("You are another Pradeeban, huh? ");
		}
	}
	
	public void sendOfflineMessage(String friendname, String msg){
		UserKeyOff key = new UserKeyOff(friendname);
		
		try {
			chord.insert(key, new OfflineMsg(msg));
		} catch (NullPointerException e){
			System.out.println("[Error] You should only see this message if you are the only one in the Network.");
		}
	}
	public void Offline(User self) {
		// TODO Auto-generated method stub
		UserKeyOff keyOff = new UserKeyOff(self.getUsername());
		UserKeyPendingFL keyPending = new UserKeyPendingFL(self.getUsername());
		Set<Serializable> result = chord.retrieve(keyOff);
		if (result.size()!=0){
			System.out.println("You have "+result.size()+" offline message(s)");
		}
		result = chord.retrieve(keyPending);
		if (result.size()!=0) {
			System.out.println("You have "+result.size()+" pending request(s)");	
		}
		
	}
	public static void removeAddr(String username) {
		UserKeyAddr key = new UserKeyAddr(username);
		Set<Serializable> addrlist = chord.retrieve(key);
		if (addrlist.size()!=0)
		for (Serializable addr : addrlist){
			chord.remove(key, addr);
		}
	}
	// originally sendMsg()
	public void sendConfMessage(UserID userid, ConfMessage confMessage) throws CommunicationException {
		// TODO Auto-generated method stub
		((ChordImpl)chord).sendMsgInFingerTableNoWait(userid.getID(), confMessage);
	}
	public static void sendConfMessageInFingerTable(UserID userid, ConfMessage confMessage) throws CommunicationException {
		// TODO Auto-generated method stub
		
			((ChordImpl)chord).sendMsgInFingerTableNoWait(userid.getID(), confMessage);
		
	}

//	public static void sendMessageInFingerTable(ID id, Serializable msg){
//		((ChordImpl)chord).sendMsgInFingerTable(id, msg);
//	}
	public static void sendMessageInFingerTable(ID id, Serializable msg) throws CommunicationException{
		((ChordImpl)chord).sendMsgInFingerTableNoWait(id, msg);
	}
//	public static void sendMsgUsableRoutingInFingerTable(ID id, Serializable msg, int backofflimit) throws CommunicationException{
//		((ChordImpl)chord).sendMsgRoutingUsableNodeInFingerTable(id, msg, backofflimit);
//	}
	public static ID getRingID(){
		
		return ((ChordImpl)chord).getID();
	}
	
	public void createRoom(User self, String roomname) {
		// TODO Auto-generated method stub
		RoomKey key = new RoomKey(self.getUsername(),roomname);
		chord.insert(key, self.getUserID());
		
		
	}
	public void viewRoom(String owner, String roomname) throws ServiceException {
		// TODO Auto-generated method stub
		RoomKey key = new RoomKey(owner,roomname);
		Set<Serializable> id = chord.retrieve(key);
		if (id.size()!=0)
		for (Serializable dummy : id ){
			System.out.println("["+owner+"]["+roomname+"] "+ (UserID)dummy);
		}
	}
	public static UserID[] getRoomParticipants(String owner,String roomname) throws ServiceException{
		RoomKey key = new RoomKey(owner,roomname);
		Set<Serializable> objs = chord.retrieve(key);
        return (UserID[]) objs.toArray(new UserID[0]);
		
	}
	public int numberPplInRoom(String owner, String roomname) throws ServiceException{
		RoomKey key = new RoomKey(owner, roomname);
		Set<Serializable> objs = chord.retrieve(key);
		return objs.size();
	}
	public void joinRoom(User self,String owner, String roomname) throws ServiceException{
		RoomKey key = new RoomKey(owner,roomname);
		chord.insert(key, self.getUserID());
	}
	public static void leaveRoom(User self, ConferenceID confID) throws ServiceException {
		// TODO Auto-generated method stub
		RoomKey key = new RoomKey(confID.getOwner(),confID.getRoomName());
		chord.remove(key, self.getUserID());
	}
	public boolean isDupRoom(String username, String roomname) throws ServiceException {
		RoomKey key = new RoomKey(username,roomname);
		if (chord.retrieve(key).size()!=0) {
			return true;
		}
		return false;
	}
	public void deleteRoom(String owner, String roomName) throws ServiceException {
		// TODO Auto-generated method stub
		RoomKey key = new RoomKey(owner,roomName);
		Set<Serializable> list = chord.retrieve(key);
		if(list.size()!=0){
			for (Serializable l : list){
				chord.remove(key, l);
			}
		}
	}
	public void print() {
		// TODO Auto-generated method stub
		System.out.println("[Chord _ ID]"+chord.getID());
//		System.out.println("[Chord _ Stored Reg User]"+getNumberRegUser());
//		System.out.println("[Chord _ Stored Offline Msg]"+getNumberOfflineMsg());
//		System.out.println("[Chord _ Stored Conf]"+getNumberRunningConf());
//		System.out.println("[Chord _ Entries]"+chord.printEntries());
//		System.out.println("[Chord _ References]"+chord.printReferences());
//		System.out.println("[Chord _ Predecessor]"+chord.printPredecessor());
		System.out.println("[Chord _ Successor]"+chord.printSuccessorList());
		System.out.println("[Chord _ FingerTable]"+chord.printFingerTable());
	}
	public static List<ID> getFingerTable(){
		Node[] Nodes = chord.getFingerTable();
		List<ID> ids = new ArrayList<ID>();
		for (Node n : Nodes){
			if ((n!=null)&&(n.getNodeID()!=null)&&(!ids.contains(n.getNodeID()))&&(!n.getNodeID().equals(chord.getID()))){
				ids.add(n.getNodeID());
			}
		}
		return ids;
	}
	public static int getNumberStoredEntries(){
		return chord.getEntriesSize();
	}
	public static int getNumberRegUser(){
		return chord.getNumReg("entity.RegUser");
	}
	public static int getNumberOfflineMsg(){
		return chord.getNumValues("entity.OfflineMsg");
	}
	public static int getNumberRunningConf(){
		return chord.getNumReg("entity.UserID");
	}
	public static void cleanRoom(ConferenceID confID, UserID victim) {
		RoomKey key = new RoomKey(confID.getOwner(),confID.getRoomName());
		chord.remove(key, victim);
	}
}
