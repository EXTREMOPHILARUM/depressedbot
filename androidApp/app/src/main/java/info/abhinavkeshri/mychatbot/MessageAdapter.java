package info.abhinavkeshri.mychatbot;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import info.abhinavkeshri.mychatbot.LocalDatabase.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private List<Message> messages = new ArrayList<>();

    public MessageAdapter(List<Message> myList){
        messages = myList;
    }
    public MessageAdapter(){

    }
    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout, parent, false);
        return new MessageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message currentNote = messages.get(position);
        holder.textViewMessage.setText(currentNote.getMessage());
        holder.textViewDate.setText(currentNote.getReceived_on());
        if(currentNote.server){
            holder.textViewMessage.setTextColor(Color.RED);
            //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.ALIGN_PARENT_END);
            //holder.relativeLayout.setHorizontalGravity(RelativeLayout.ALIGN_RIGHT);
            holder.relativeLayout.setGravity(Gravity.LEFT);
            //holder.textViewMessage.setLayoutParams(params);
            holder.parentLinearLayout.setGravity(Gravity.LEFT);

            return;
        }
        holder.textViewMessage.setTextColor(Color.BLUE);
        //holder.relativeLayout.setHorizontalGravity(RelativeLayout.ALIGN_LEFT);
        holder.relativeLayout.setGravity(Gravity.RIGHT);
        holder.parentLinearLayout.setGravity(Gravity.RIGHT);
        //switcher = 1;


    }

    @Override
    public int getItemCount() {

        return messages.size();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;
        private TextView textViewDate;
        private LinearLayout relativeLayout;
        private LinearLayout parentLinearLayout;


        public MessageHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.text_view_message);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            relativeLayout = itemView.findViewById(R.id.messageRelativeLayout);
            parentLinearLayout = itemView.findViewById(R.id.messageLinearLayout);
        }
    }
}
