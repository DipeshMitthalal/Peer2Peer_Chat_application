package entity;

import java.io.Serializable;

import de.uniba.wiai.lspi.chord.data.ID;

public class UserID implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6030154270195049999L;
	
	private String username;
	private ID id;
	
	public UserID (String username){
		this.username = username;
	}
	public String getUsername(){
		return username;
	}
	public void setUserID(ID id){
		this.id = id;
	}
	
	public ID getID(){
		return id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		UserID other = (UserID) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
}

