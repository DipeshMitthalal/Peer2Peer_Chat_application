package key;

public class UserKeyOff implements de.uniba.wiai.lspi.chord.service.Key {

    
    
    private String username;

    public UserKeyOff(String username) {
        this.username = username;
    }
    
    public byte[] getBytes() {
        return (username+"_Off").getBytes();
    }

   
   
}

