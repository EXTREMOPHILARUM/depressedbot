package info.abhinavkeshri.mychatbot.LocalDatabase;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MessageRepository {
    private MessageDao messageDao;
    private LiveData<List<Message>> allMessages;

    public MessageRepository(Application application){
        MessageDatabase database = MessageDatabase.getInstance(application);
        messageDao = database.messageDao();
        allMessages = messageDao.getAllMessages();
    }
    public LiveData<List<Message>> getAllMessages(){
        return allMessages;
    }

    public void insert(Message message){
        new InsertMessageAsyncTask(messageDao).execute(message);
    }
    public void delete(Message message){
        new DeleteMessageAsyncTask(messageDao).execute(message);
    }


    private static class InsertMessageAsyncTask extends AsyncTask<Message, Void, Void> {
        private MessageDao messageDao;
        private InsertMessageAsyncTask(MessageDao noteDao){
            this.messageDao = noteDao;
        }
        @Override
        protected Void doInBackground(Message... message){
            messageDao.insert(message[0]);
            return null;
        }
    }
    private static class DeleteMessageAsyncTask extends AsyncTask<Message, Void, Void>{
        private MessageDao messageDao;
        private DeleteMessageAsyncTask(MessageDao messageDao){
            this.messageDao = messageDao;
        }
        @Override
        protected Void doInBackground(Message... message){
            messageDao.delete(message[0]);
            return null;
        }
    }




}
