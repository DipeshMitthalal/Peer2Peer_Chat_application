package chordMessage;

import entity.ConfInvitation;

public class InvitationMessage extends ConfMessage{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6855240497696036981L;
	private ConfInvitation inv;
	public InvitationMessage(String username, String roomname,ConfInvitation invitation) {
		super(username, roomname);
		this.inv = invitation;
	}
	public ConfInvitation getInvitation(){
		return inv;
	}

}
