package chordMessage;

import entity.UserID;

public class EventMessage extends ConfMessage{

		public enum MessageType {User_JOIN,User_LEAVE};
		private UserID userId;
		private MessageType type;
	public EventMessage(String ownername, String roomname, UserID userID,MessageType type){
		super(ownername,roomname);
		this.userId = userID;
		this.type = type;
	}
	public UserID getUserID(){
		return userId;
	}
	public MessageType getMessageType(){
		return type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventMessage other = (EventMessage) obj;
		if (type != other.type)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	
}
