package conference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import chordMessage.ConfMessage;
import chordMessage.EventMessage;
import chordMessage.InvitationMessage;
import chordMessage.TextMessage;
import chordring.ChordManager;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.service.ServiceException;

import entity.ConfInvitation;
import entity.ConferenceID;
import entity.UserID;

public class Conference extends Observable{
	private List<UserID> participantsID = new ArrayList<UserID>();
	private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();
	
	private ConferenceID confID;
	private ChordManager chordManager;
	
	public Conference(ConferenceID confID,ChordManager cM){
		this.confID = confID;
		this.chordManager = cM;
		System.out.println("[Conference] New chat room is created");
	}
	public ConferenceID getConfID(){
		return confID;
	}
	public BlockingQueue<String> getMessageQueue(){
		return messageQueue;
	}
	public void addMySelf(UserID self){
		participantsID.add(self);
	}
	public void updateParticipants(UserID[] part) {
        this.participantsID.clear();
        for ( UserID id : part ) {
            participantsID.add(id);
        }
    }
//	public void removeParticipant(UserID newcomer){
//		participantsID.remove(newcomer);
//	}
	public List<UserID> getParticipants(){
		return participantsID;
	}
	
	public void receive(Serializable msg) {
		
		if (msg instanceof EventMessage){
			EventMessage event = (EventMessage)msg;
			if (event.getMessageType().equals(EventMessage.MessageType.User_JOIN)){
				participantsID.add(event.getUserID());
				messageQueue.add("["+event.getRoomName()+"] "+event.getUserID().getUsername()+" joined the conference.");
			}
			else if (event.getMessageType().equals(EventMessage.MessageType.User_LEAVE)){
				participantsID.remove(event.getUserID());
				messageQueue.add("["+event.getRoomName()+"] "+event.getUserID().getUsername()+" left the conference.");
			}
		}
		else {
			TextMessage message = (TextMessage)msg;
			messageQueue.add("["+message.getRoomName()+"] "+message.getMessage());
		}
		setChanged();
		notifyObservers();
	}
	
	public void sendConfMessage(UserID selfID, String msg) throws CommunicationException{
		for (UserID userid : participantsID){
			if (!userid.equals(selfID)){
				chordManager.sendConfMessage(userid,new TextMessage(confID.getOwner(),confID.getRoomName(),msg));
			}
		}
	}
	public void sendConfMessageInFingerTable(UserID selfID, String msg) throws CommunicationException{
		for (UserID userid : participantsID){
			if (!userid.equals(selfID)){
				ChordManager.sendConfMessageInFingerTable(userid,new TextMessage(confID.getOwner(),confID.getRoomName(),msg));
			}
		}
	}
//	public void sendInvitation(UserID id,ConfInvitation invitation){
//		chordManager.sendConfMessage(id, new InvitationMessage(confID.getOwner(),confID.getRoomName(),invitation));
//	}
	public void annouceNewcomer(UserID newcomerid) throws CommunicationException{
		for (UserID userid : participantsID){ 
			if (!userid.equals(newcomerid)){
				chordManager.sendConfMessage(userid, new EventMessage(confID.getOwner(),confID.getRoomName(),newcomerid,EventMessage.MessageType.User_JOIN));	
			}
		}
	}
	public void annouceLeaver(UserID id) throws ServiceException, CommunicationException{
		if(participantsID.size()==1){
			chordManager.deleteRoom(confID.getOwner(),confID.getRoomName());
		}else {
			for (UserID userid : participantsID){
				if (!userid.equals(id)){
				chordManager.sendConfMessage(userid, new EventMessage(confID.getOwner(),confID.getRoomName(),id,EventMessage.MessageType.User_LEAVE));
				}
			}	
		}
		
	}
	public void annouceNewcomerInFingerTable(UserID newcomerid) throws CommunicationException{
		for (UserID userid : participantsID){ 
			if (!userid.equals(newcomerid)){
				ChordManager.sendConfMessageInFingerTable(userid, new EventMessage(confID.getOwner(),confID.getRoomName(),newcomerid,EventMessage.MessageType.User_JOIN));	
			}
		}
	}
	public void annouceLeaverInFingerTable(UserID id) throws ServiceException, CommunicationException{
		if(participantsID.size()==1){
			chordManager.deleteRoom(confID.getOwner(),confID.getRoomName());
		}else {
			for (UserID userid : participantsID){
				if (!userid.equals(id)){
				ChordManager.sendConfMessageInFingerTable(userid, new EventMessage(confID.getOwner(),confID.getRoomName(),id,EventMessage.MessageType.User_LEAVE));
				}
			}	
		}
		
	}
	public void objserverChanged() {
        setChanged();
        notifyObservers();
    }
	
	public boolean existingUser(UserID userid){
		if(participantsID.contains(userid)){
			return true;
		}
		else {
			return false;
		}
	}
}
