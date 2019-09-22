package info.abhinavkeshri.mychatbot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import info.abhinavkeshri.mychatbot.LocalDatabase.Message;
import info.abhinavkeshri.mychatbot.LocalDatabase.MessageViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int _id ;
    private MessageViewModel messageViewModel;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        /*
        SharedPreferences sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        _id = sp.getInt("_id", -1);
        if(_id == -1){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

         */
    }
    Button mSendBT;
    EditText mMessageET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        SharedPreferences sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        _id = sp.getInt("_id", -1);
        String userName = sp.getString("username", "def");
        if(_id == -1 && userName.equals("def")){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

         */

        //Toast.makeText(this, "current time = " + currentTime + " current date = "+ currentDate, Toast.LENGTH_LONG).show();
        mMessageET = findViewById(R.id.messageEditText);
        mSendBT = findViewById(R.id.sendButton);
        mSendBT.setOnClickListener(this);



        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        /*
        List<Message> myList = new ArrayList<>();
        myList.add(new Message("Hi there ", " 21-09-2019", true));
        myList.add(new Message("Good Evening", " 21-09-2019", true));
        myList.add(new Message("How are you doing", " 21-09-2019", true));

        final MessageAdapter adapter = new MessageAdapter(myList);
        */

        final MessageAdapter adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        messageViewModel.getAllMessages().observe(this, new Observer<List<Message>>(){
            @Override
            public void onChanged(@Nullable List<Message> messages){
                adapter.setMessages(messages);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    }
                });
            }
        });

        //messageViewModel.insert(new Message("This is the new message !", "21-09-2019", true));
        //messageViewModel.insert(new Message("Good Evening", "21-09-2019", true));
        //messageViewModel.insert(new Message("How are you doing ", "21-09-2019", true));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendButton:
                String text = mMessageET.getText().toString().trim();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date());
                if(!text.equals("")){
                    messageViewModel.insert(new Message(text, currentTime , false));
                }
                mMessageET.setText("");
                break;
        }
    }
}
