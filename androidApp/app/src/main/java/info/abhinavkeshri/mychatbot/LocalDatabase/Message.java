package info.abhinavkeshri.mychatbot.LocalDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "messages_table")
public class Message {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String message;
    public String received_on;
    public boolean server;

    public Message(String message, String received_on, boolean server){
        this.message = message;
        this.received_on = received_on;
        this.server = server;
    }
    public int getId(){
        return id;
    }
    public void setId(int i){
        this.id = i;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        message = message;
    }

    public String getReceived_on() {
        return received_on;
    }

    public void setReceived_on(String received_on) {
        this.received_on = received_on;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }
}
