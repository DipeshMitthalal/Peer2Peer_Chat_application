package de.uniba.wiai.lspi.chord.service;

import java.io.Serializable;

import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.data.ID;

public interface ReceiveCallback {
	public void receiveMessage(Serializable message);

	public void murderDeadPeople(Entry message);
	
}
