package entity;

import java.io.Serializable;

public class OfflineMsg implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7339790381606658956L;
	private String offmsg;
	private final long timeStamp;
	public OfflineMsg(String msg){
		offmsg = msg;
		timeStamp = System.currentTimeMillis();
	}
	
	public String getOfflineMsg(){
		return offmsg;
	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((offmsg == null) ? 0 : offmsg.hashCode());
		result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
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
		OfflineMsg other = (OfflineMsg) obj;
		if (offmsg == null) {
			if (other.offmsg != null)
				return false;
		} else if (!offmsg.equals(other.offmsg))
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		return true;
	}

	
	
}
