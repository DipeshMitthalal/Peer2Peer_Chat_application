package chordMessage;

import de.uniba.wiai.lspi.chord.data.ID;

public class MonitorRequestMsg extends MonitorMessage{
	private ID limit;
	public MonitorRequestMsg(ID o,ID s, ID l,MessageType t) {
		super(o,s, t);
		this.limit = l;
	}
	public ID getLimit(){
		return limit;
	}
}
