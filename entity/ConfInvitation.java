package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.uniba.wiai.lspi.chord.data.ID;

public class ConfInvitation implements Serializable {

	private static final long serialVersionUID = 547652386833115553L;
	
	private List<UserID> participantsID = new ArrayList<UserID>();
	private ConferenceID confID;

	public ConfInvitation(ConferenceID confid, List<UserID> list){
		this.confID = confid;
		participantsID = list;
	}
	
	public ConferenceID getConfID(){
		return confID;
	}
	public List<UserID> getList(){
		return participantsID;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((confID == null) ? 0 : confID.hashCode());
		result = prime * result
				+ ((participantsID == null) ? 0 : participantsID.hashCode());
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
		ConfInvitation other = (ConfInvitation) obj;
		if (confID == null) {
			if (other.confID != null)
				return false;
		} else if (!confID.equals(other.confID))
			return false;
		if (participantsID == null) {
			if (other.participantsID != null)
				return false;
		} else if (!participantsID.equals(other.participantsID))
			return false;
		return true;
	}
	
}
