package entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.uniba.wiai.lspi.chord.data.ID;

public class User implements Serializable{
	
	private static final long serialVersionUID = -8179230275104095156L;
	
	private String username;
	private String fullname;
	private String password;
	private String address;
	private UserID userId;
	private int port;
	
	private Set<User> friendlist = new HashSet<User>(); 
	
	public User(String username,String password,String address,int port){ //for myself when login
		this.username=username;
		this.password=password;
		this.address=address;
		this.port=port;
		this.userId= new UserID(username);
//		this.login = login;
	}
	public User(String username,String address,int port){  //for friend
		this.username = username;
		this.address = address;
		this.port = port;
		this.userId= new UserID(username);
	}
	public User(String username, String password,String fullname){ //for registering
		this.username = username;
		this.fullname = fullname;
		this.password = password;
		this.userId= new UserID(username);
	}
	
	
	public void setID(ID id){
		userId.setUserID(id);
	}
	public UserID getUserID(){
		return userId;
	}
	public void addFriend(User friend){
		friendlist.add(friend);
	}
	public User getFriend(String username){
		for (User friend : friendlist){
			if(friend.getUsername().equalsIgnoreCase(username)) {
				return friend;
			}
		}
		return null;
	}
	public void updateFriend(User friend){
		for (User myfriend : friendlist){
			if (myfriend.getUsername().equals(friend.getUsername())){
				myfriend = friend;
			}
			break;
		}
	}
	public void updateFriendList(Set<Serializable> friendlist){
		friendlist.clear();
		for (Serializable friend : friendlist) {
			addFriend((User) friend);
		}
	}
	
	public Set<User> getFriendList(){
		return friendlist;
	}
	public String getUsername(){
		return username;
	}
	public String getPassword(){
		return password;
	}
	public String getAddress(){
		return address;
	}
	public void setAddress(String adr){
		this.address= adr;
	}
	public int getPort(){
		return port;
	}
	public void setPort(int port){
		this.port = port;
	}
	public String getFullname(){
		return fullname;
	}
	public void setFullname(String fullname){
		this.fullname = fullname;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result
				+ ((friendlist == null) ? 0 : friendlist.hashCode());
//		result = prime * result + (login ? 1231 : 1237);
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + port;
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
		User other = (User) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (friendlist == null) {
			if (other.friendlist != null)
				return false;
		} else if (!friendlist.equals(other.friendlist))
			return false;
//		if (login != other.login)
//			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (port != other.port)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	public boolean isFriend(String friendname) {
		// TODO Auto-generated method stub
		for (User friend : friendlist){
			if (friend.getUsername().equals(friendname)){
				return true;
			}
		}
		return false;
	}
	
	
}
