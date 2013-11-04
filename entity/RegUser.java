package entity;

import java.io.Serializable;

public class RegUser implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7997124087913588065L;
	private String fullName;
	public RegUser(String name){
		this.fullName = name;
	}
	public String getFullName(){
		return fullName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fullName == null) ? 0 : fullName.hashCode());
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
		RegUser other = (RegUser) obj;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		return true;
	}
	
	
}
