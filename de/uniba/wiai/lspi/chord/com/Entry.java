/***************************************************************************
 *                                                                         *
 *                                Entry.java                               *
 *                            -------------------                          *
 *   date                 : 28.02.2005                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *                          karsten.loesing@uni-bamberg.de                 *
 *                                                                         *
 *                                                                         *
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   A copy of the license can be found in the license.txt file supplied   *
 *   with this software or at: http://www.gnu.org/copyleft/gpl.html        *
 *                                                                         *
 ***************************************************************************/

package de.uniba.wiai.lspi.chord.com;

import java.io.Serializable;

import de.uniba.wiai.lspi.chord.data.ID;

/**
 * @author karsten
 * @version 1.0.5
 */
public final class Entry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3473253817147038992L;
	/**
	 * The timeStamp of this entry
	 * 
	 */
	private long timeStamp;
	/**
	 * The id of this entry. 
	 */
	private ID id;

	/**
	 * The stored value. 
	 * 
	 */
	private Serializable value;

	/**
	 * @param id1
	 * @param value1
	 */
	public Entry(ID id1, Serializable value1) {
		this.id = id1;
		this.value = value1;
		this.timeStamp = System.currentTimeMillis();
	}
	public Entry(ID id1, Serializable value1, long timestamp1){
		this.id = id1;
		this.value = value1;
		this.timeStamp = timestamp1;
	}
	/**
	 * @return Returns the timeStamp
	 */
	public final long getTimeStamp() {
		return this.timeStamp;
	}
	public final void setTimeStamp(long ts){
		timeStamp = ts;
	}
	/**
	 * @return Returns the id.
	 */
	public ID getId() {
		return this.id;
	}

	/**
	 * @return Returns the value.
	 */
	public Serializable getValue() {
		return this.value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entry other = (Entry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	public boolean equalsWithTimeStamp(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entry other = (Entry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Entry [id=" + id + ", value="
				+ value + "]";
	}

	
}
