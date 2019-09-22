package info.abhinavkeshri.mychatbot.LocalDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Delete
    void delete(Message message);

    @Query("SELECT * FROM messages_table")
    LiveData<List<Message>> getAllMessages();

}
