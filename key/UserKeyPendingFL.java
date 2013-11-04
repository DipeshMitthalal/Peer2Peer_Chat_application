package key;

public class UserKeyPendingFL implements de.uniba.wiai.lspi.chord.service.Key {

    
    
    private String username;

    public UserKeyPendingFL(String username) {
        this.username = username;
    }
    
    public byte[] getBytes() {
        return (username+"_PendingFL").getBytes();
    }

   
   
}

