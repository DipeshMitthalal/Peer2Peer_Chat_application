package key;

public class UserKeyFL implements de.uniba.wiai.lspi.chord.service.Key {

    
    
    private String username;

    public UserKeyFL(String username) {
        this.username = username;
    }
    
    public byte[] getBytes() {
        return (username+"_FriendList").getBytes();
    }

   
   
}

