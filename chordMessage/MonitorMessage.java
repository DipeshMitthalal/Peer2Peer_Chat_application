package chordMessage;

import java.io.Serializable;

import de.uniba.wiai.lspi.chord.data.ID;

public class MonitorMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2332135725026421306L;

	public enum MessageType {Monitor_REQUEST,Monitor_REPLY,Monitor_ACK,Monitor_FAULT};
	private ID origin;
	private ID source;
	private MessageType type;
	
	public MonitorMessage(ID o,ID s, MessageType t){
		this.origin = o;
		this.source = s;
		this.type = t;
	}
	public ID getOrigin(){
		return origin;
	}
	public ID getSource(){
		return source;
	}
	public MessageType getMessageType(){
		return type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MonitorMessage other = (MonitorMessage) obj;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	
}
