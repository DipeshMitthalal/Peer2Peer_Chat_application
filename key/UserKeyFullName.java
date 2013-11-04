package key;

public class UserKeyFullName implements de.uniba.wiai.lspi.chord.service.Key {

    
    
    private String username;

    public UserKeyFullName(String username) {
        this.username = username;
    }
    
    public byte[] getBytes() {
        return (username+"_FullName").getBytes();
    }

   
   
}

