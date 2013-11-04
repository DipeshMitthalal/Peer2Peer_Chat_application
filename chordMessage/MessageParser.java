package chordMessage;
import java.io.Serializable;

import monitor.Monitor;

import chordring.ChordManager;

import conference.Conference;
import conference.ConferenceManager;

import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.ReceiveCallback;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import entity.ConferenceID;
import entity.UserID;


public class MessageParser implements ReceiveCallback{
	private ConferenceManager confManager;
	private Monitor monitor;
	public MessageParser(Monitor monitor){
		this.monitor = monitor;
		confManager=null;
	}
	public void setConfManager(ConferenceManager c){
		this.confManager = c;
	}
	@Override
	public void receiveMessage(Serializable message) {
		
		// TODO Auto-generated method stub
		if (message instanceof MonitorMessage) {
//			System.out.println("Got a msg "+((MonitorMessage)message).getMessageType());
			monitor.receive((MonitorMessage)message);
		}

		else if ((message instanceof EventMessage)||(message instanceof TextMessage)){
			ConfMessage confM = (ConfMessage)message;
			ConferenceID confid = new ConferenceID(confM.getOwner(), confM.getRoomName());
			for (Conference c : confManager.getConfList()) {
				if (c.getConfID().equals(confid)){
					c.receive(message);
				}
			}
			
		}
		
	}
	@Override
	public void murderDeadPeople(Entry message) {
		ConfMessage confmsg = (ConfMessage)message.getValue();
		ConferenceID confid = new ConferenceID(confmsg.getOwner(),confmsg.getRoomName());
		UserID[] userlist = null;
		try {
			userlist = ChordManager.getRoomParticipants(confid.getOwner(), confid.getRoomName());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		UserID victim = null;
		if (userlist!=null){
			for (int i = 0; i<userlist.length;i++){
				if (userlist[i].getID().equals(message.getId())){
					victim = userlist[i];
				}
			}
			ChordManager.cleanRoom(confid, victim);
			for (int i=0; i<userlist.length;i++){
				if (!userlist[i].equals(victim)){
					try {
						ChordManager.sendConfMessageInFingerTable(userlist[i], new EventMessage(confid.getOwner(),confid.getRoomName(),victim,EventMessage.MessageType.User_LEAVE));
					} catch (CommunicationException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}	
				}
				

				
			}
		}
		
  	}

}
