package chordMessage;

import de.uniba.wiai.lspi.chord.data.ID;

public class MonitorReplyMsg extends MonitorMessage{
	private int number_node;
	private MonitoringData data;
	public MonitorReplyMsg(ID o,ID s, MessageType t,MonitoringData d) {
		super(o,s, t);
		data = d;
		
		// TODO Auto-generated constructor stub
	}
	public MonitoringData getData(){
		return data;
	}
	
}

