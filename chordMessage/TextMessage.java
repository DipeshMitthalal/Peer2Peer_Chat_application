package chordMessage;

public class TextMessage extends ConfMessage{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5994841495373012189L;
	private String msg;
	
	public TextMessage(String ownername,String roomname,String msg){
		super(ownername,roomname);
		this.msg = msg;
	}

	public String getMessage(){
		return msg;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((msg == null) ? 0 : msg.hashCode());
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
		TextMessage other = (TextMessage) obj;
		if (msg == null) {
			if (other.msg != null)
				return false;
		} else if (!msg.equals(other.msg))
			return false;
		return true;
	}
	
	
}
