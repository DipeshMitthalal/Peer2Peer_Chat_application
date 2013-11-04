package key;

public class UserKey implements de.uniba.wiai.lspi.chord.service.Key {

    
    
    private String username;

    public UserKey(String username) {
        this.username = username;
    }
    
    public byte[] getBytes() {
        return (username+"_Word").getBytes();
    }

   
   
}

