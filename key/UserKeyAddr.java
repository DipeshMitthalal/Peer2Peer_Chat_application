package key;

public class UserKeyAddr implements de.uniba.wiai.lspi.chord.service.Key {

    
    
    private String username;

    public UserKeyAddr(String username) {
        this.username = username;
    }
    
    public byte[] getBytes() {
        return (username+"_IP").getBytes();
    }

   
   
}

