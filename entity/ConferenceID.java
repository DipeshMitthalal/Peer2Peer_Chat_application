package entity;

import java.io.Serializable;

public class ConferenceID implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -157581283380992692L;
	private String owner;
	private String roomname;
	public ConferenceID(String o, String r){
		owner = o;
		roomname = r;
	}
	public String getOwner(){
		return owner;
	}
	public String getRoomName(){
		return roomname;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result
				+ ((roomname == null) ? 0 : roomname.hashCode());
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
		ConferenceID other = (ConferenceID) obj;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (roomname == null) {
			if (other.roomname != null)
				return false;
		} else if (!roomname.equals(other.roomname))
			return false;
		return true;
	}
	
}
