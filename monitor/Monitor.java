package monitor;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import chatManagement.ChatManager;
import chordMessage.MonitorMessage;
import chordMessage.MonitorReplyMsg;
import chordMessage.MonitorMessage.MessageType;
import chordMessage.MonitorRequestMsg;
import chordMessage.MonitoringData;
import chordring.ChordManager;
import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.data.ID;

public class Monitor{
	private ChatManager manager;
	private MonitoringData data;
	int interval_count = 0;
	private long TIME_OUT = 5 * 1000;
	private boolean reply= false;
	static boolean error;
	static boolean fault = false;
	static int reply_count;
//	public Monitor(ChatManager manager){
//		this.manager = manager;
//	}
	public Monitor(){
		
	}
	Map<ID, Integer>requestCounter = new HashMap<ID,Integer>();
	Map<ID, ID>requestSource = new HashMap<ID,ID>();
	Map<ID, MonitoringData>requestData = new HashMap<ID,MonitoringData>();
	BlockingQueue<MonitorReplyMsg>msgQueue = new LinkedBlockingQueue<MonitorReplyMsg>();
	
	
	public MonitorMessage monitor(final MonitorRequestMsg msg){
		List<ID> fingers = ChordManager.getFingerTable();
		ID limit = msg.getLimit();
//		System.out.println("[Monitor] processing");
//		interval_count=0;
		error = true;
		
		for (int i = fingers.size()-1;i>=0;i--){
			if (fingers.get(i).isInInterval(ChordManager.getRingID(), limit)&&(!fingers.get(i).equals(limit))){
				if (i>=interval_count) {
					interval_count=i+1;
					requestCounter.put(msg.getOrigin(), interval_count);
				}
//				System.out.println("[Monitor] sending out to "+fingers.get(i).toString() +" : "+(interval_count-i)+"/"+interval_count);
				try {
					ChordManager.sendMessageInFingerTable(fingers.get(i), new MonitorRequestMsg(msg.getOrigin(), ChordManager.getRingID(), limit, MessageType.Monitor_REQUEST));
//					System.out.println("[Monitor] sent out ... "+(interval_count-i)+"/"+interval_count);
					
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					interval_count--;
					requestCounter.put(msg.getOrigin(),interval_count);
				}
				limit = fingers.get(i);
			}
			
		}
		if ((interval_count >0)) {
			try {
				ChordManager.sendMessageInFingerTable(msg.getSource(), new MonitorMessage(msg.getOrigin(), ChordManager.getRingID(), MessageType.Monitor_ACK));
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
//			reply= true;
			
			Timer timer = new Timer();
			 timer.schedule(new TimerTask(){
				  public void run(){
					  if(error){
						  System.out.println("[Monitor _ Fault] There was some error with the connections.");
						  if (ChordManager.getRingID().equals(msg.getOrigin())){
							  data = requestData.get(msg.getOrigin());
								data.removeReplicas();
								System.out.println("[Monitor _ Fault] Data might be corrupted");
								System.out.println(data.toString());
						  }else {
							  ID destination = msg.getSource();
							  data = requestData.get(msg.getSource());
							  try {
								ChordManager.sendMessageInFingerTable(destination, new MonitorReplyMsg(msg.getOrigin(), ChordManager.getRingID(), MessageType.Monitor_FAULT, data));
							} catch (CommunicationException e) {
								// TODO Auto-generated catch block
								System.out.println("[Monitor _ Fault] Cannot even send back. Is it 21.12.2012 already? ");
							} 
						  }
						  
					  }
				  }
				  },TIME_OUT);
		}
		
		if (interval_count==0){
//			System.out.println("[Monitor] The last stand");
			return new MonitorReplyMsg(msg.getOrigin(),msg.getSource(),MessageType.Monitor_REPLY,
													new MonitoringData(ChordManager.getNumberRegUser(), ChordManager.getNumberOfflineMsg(), ChordManager.getNumberRunningConf(), ChatManager.getMsgRate()));
		}
		return null;
		
	}
	
	public void receive(final MonitorMessage message) {
		// TODO Auto-generated method stub
//		System.out.println("[Monitor] Monitoring message of type "+message.getMessageType()+" received from"+message.getSource());
		if (message.getMessageType().equals(chordMessage.MonitorMessage.MessageType.Monitor_ACK)){
			if(!message.getSource().equals(ChordManager.getRingID())){
			error = false;
//			System.out.println("reset time out");
			}
			
		}
		if (message.getMessageType().equals(chordMessage.MonitorMessage.MessageType.Monitor_REQUEST)){
			List<ID> fingers = ChordManager.getFingerTable();
			int backofflimit = fingers.size();
			fault = false;
			if(message.getSource().equals(ChordManager.getRingID())){
				System.out.println("[Monitor] Processing ... ");
			}
			error = true;
			data = new MonitoringData(ChordManager.getNumberRegUser(), ChordManager.getNumberOfflineMsg(), ChordManager.getNumberRunningConf(), ChatManager.getMsgRate());
			requestData.put(message.getOrigin(), data);
			interval_count=0;
			reply=false;
//			System.out.println("[Monitor] That was a Request message.");
			requestSource.put(message.getOrigin(), message.getSource());
//			System.out.println("[Handling Request]...");
			MonitorMessage response = null;
			response = this.monitor((MonitorRequestMsg)message);
			/*
			 * Original one
			 */
//			if (response != null) {
//				error = false;
////				System.out.println("[Monitor] Returning value :"+((MonitorReplyMsg)response).getData().toString());
//				try {
//					ChordManager.sendMessageInFingerTable(message.getSource(), response);
//				} catch (CommunicationException e) {
//					// TODO Auto-generated catch block
//					System.out.println("[Monitor] Fail to send back message");
//					error =true;
//				}
////				System.out.println("Send 1");
//			}
			if (response != null) {
				error = false;
				try {
					ChordManager.sendMessageInFingerTable(message.getSource(), response);
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					System.out.println("[Monitor] Fail to send back message");
					error =true;
				}
			}
		}
		if ((message.getMessageType().equals(chordMessage.MonitorMessage.MessageType.Monitor_REPLY))||(message.getMessageType().equals(chordMessage.MonitorMessage.MessageType.Monitor_FAULT))){
			if (message.getMessageType().equals(chordMessage.MonitorMessage.MessageType.Monitor_REPLY)){
				fault=false;
			}
			else {
				fault = true;
			}
			synchronized (requestCounter) {
				reply_count = requestCounter.get(message.getOrigin());
				reply_count--;
				requestCounter.put(message.getOrigin(), reply_count);
				data = requestData.get(message.getOrigin());
				data.addData(((MonitorReplyMsg)message).getData());	
				requestData.put(message.getOrigin(), data);
			}
//			System.out.println("[Monitor] Receive Reply message!"+data.toString());
			if (reply_count==0){
				error = false;
//				System.out.println("[Monitor] Got final result.");
				if (ChordManager.getRingID().equals(message.getOrigin())){ //I'm the one who sent request
					if (!fault){
						System.out.println("[Monitor] Got final result.");
					}
					else{
						System.out.println("[Monitor _ Fault] Data might be corrupted.");
					}
					data = requestData.get(message.getOrigin());
					data.removeReplicas();
					System.out.println(data.toString());
				}
				else {
					ID destination = requestSource.get(message.getOrigin()); //sending back result to parent node
//					System.out.println("[Monitor] Sending back value to "+destination.toString());
					try {
						data = requestData.get(message.getOrigin());
						if (!fault){
							ChordManager.sendMessageInFingerTable(destination, new MonitorReplyMsg(message.getOrigin(), ChordManager.getRingID(), MessageType.Monitor_REPLY, data));	
						}
						else {
							ChordManager.sendMessageInFingerTable(destination, new MonitorReplyMsg(message.getOrigin(), ChordManager.getRingID(), MessageType.Monitor_FAULT, data));
						}
					} catch (CommunicationException e) {
						// TODO Auto-generated catch block
						
					}
					requestCounter.remove(message.getOrigin());
					requestSource.remove(message.getOrigin());
					requestData.remove(message.getOrigin());
				}
			}
		}
		
	}
	
	
	
}

