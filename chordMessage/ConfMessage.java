package chordMessage;

import java.io.Serializable;

public abstract class ConfMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8097047330412923341L;

	private String username;
	private String roomname;
	public ConfMessage(String username, String roomname){
		this.username = username;
		this.roomname = roomname;
	}
	
	public String getOwner(){
		return username;
	}
	public String getRoomName(){
		return roomname;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((roomname == null) ? 0 : roomname.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		ConfMessage other = (ConfMessage) obj;
		if (roomname == null) {
			if (other.roomname != null)
				return false;
		} else if (!roomname.equals(other.roomname))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
}
