package conference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import chordMessage.ConfMessage;
import chordring.ChordManager;

import consoleManagement.ConsoleManager;

import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.ReceiveCallback;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import entity.ConferenceID;
import entity.User;
import entity.UserID;


public class ConferenceManager{
	private static List<Conference> ConfList = new ArrayList<Conference>();
	private static Conference talkingConference = null;
	private static ConsoleManager consoleManager;
	public ConferenceManager(ConsoleManager console){
		this.consoleManager = console;
	}
	public void addConf(Conference c){
		ConfList.add(c);
	}
	public void removeConf(User self) throws ServiceException, CommunicationException{
		if (talkingConference!=null) {
			talkingConference.deleteObservers();
			talkingConference.annouceLeaver(self.getUserID());
			ConfList.remove(talkingConference);
			ChordManager.leaveRoom(self,talkingConference.getConfID());
			talkingConference = null;
		}
	}
	
	public void clearUpConference(Conference conf){
		
	}
	public Conference isTalking(){
		return talkingConference;
	}
	public void forceNoTalkingConf(){
		if (talkingConference!=null){
			talkingConference.deleteObservers();
			talkingConference = null;
		}
	}
	public boolean inConference(String owner, String roomname){
		ConferenceID confid = new ConferenceID(owner, roomname);
		for (Conference id : ConfList) {
			if (id.getConfID().equals(confid)) {return true;}
		}
		return false;
	}
	public List<Conference> getConfList(){
		return ConfList;
	}
	public static void setTalkingConference(Observer o, String owner, String roomname){
		ConferenceID confid = new ConferenceID(owner, roomname);
		
		for (Conference conf : ConfList ){
			if (conf.getConfID().equals(confid)) {
				if (talkingConference!=null){
					talkingConference.deleteObservers();
				}
				talkingConference = conf;
				talkingConference.addObserver(consoleManager);
				talkingConference.objserverChanged();
			}
		}
	}
	public boolean isExistingConf(String owner, String roomname){
		ConferenceID confid = new ConferenceID(owner, roomname);
		for (Conference c : ConfList){
			if (c.getConfID().equals(confid)) return true;
		}
		return false;
	}
	public static String forceNewTalkingConf(){
		if (ConfList.size()!=0){
			String owner = ConfList.get(0).getConfID().getOwner();
			String roomname = ConfList.get(0).getConfID().getRoomName();
			setTalkingConference(consoleManager, owner, roomname);
			return owner+":"+roomname;
		} else {
			return null;	
		}
	}
	public void sendConferenceMessage(UserID selfID,String msg) throws CommunicationException{
		if (talkingConference!=null){
			talkingConference.sendConfMessage(selfID, msg);
		}
		else {
			System.out.println("[Conference] No talking conference");
		}
	}
	public void sendConferenceMessageInFingerTable(UserID selfID,String msg) throws CommunicationException{
		if (talkingConference!=null){
			talkingConference.sendConfMessageInFingerTable(selfID, msg);
		}
		else {
			System.out.println("[Conference] No talking conference");
		}
	}
	public UserID lookUpID(ID victimID){
		UserID victim = null;
		for (Conference c : ConfList){
			for (UserID userid : c.getParticipants()) {
				if (userid.getID().equals(victimID)){
					victim = userid;
					return victim;
				}
					
			}
		}
		return victim;
	}
	
	public boolean inThatConference(Conference c, UserID userid){
		if (c.existingUser(userid)){
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public void stop(UserID selfID) throws ServiceException, CommunicationException{
		if ((ConfList!=null)&&(ConfList.size()!=0)){
			for (Conference conf : ConfList) {
				conf.deleteObservers();
				conf.annouceLeaver(selfID);
			}
			ConfList.clear();
		}
		
	}
	public void listRoom() {
		// TODO Auto-generated method stub
		if (ConfList.size()!=0) {
			for (Conference conf : ConfList) {
				System.out.println("["+conf.getConfID().getOwner()+"]["+conf.getConfID().getRoomName()+"] :"+conf.getParticipants().size());
			}
		}else {
			System.out.println("[Conference] No conference available");
		}
	}
	
	
}