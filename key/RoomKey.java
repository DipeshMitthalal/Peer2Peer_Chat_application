package key;

public class RoomKey implements de.uniba.wiai.lspi.chord.service.Key {

    
    
    private String ownername;
    private String roomname;

    public RoomKey(String owner,String roomname) {
        this.ownername = owner;
        this.roomname = roomname;
    }
    
    public byte[] getBytes() {
        return (ownername+":"+roomname).getBytes();
    }

   
   
}

