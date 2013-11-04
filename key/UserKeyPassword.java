package key;

public class UserKeyPassword implements de.uniba.wiai.lspi.chord.service.Key {

    
    
    private String username;

    public UserKeyPassword(String username) {
        this.username = username;
    }
    
    public byte[] getBytes() {
        return (username+"_Pass").getBytes();
    }

   
   
}

