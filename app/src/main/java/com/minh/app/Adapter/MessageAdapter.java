package com.minh.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minh.app.Model.Chat;
import com.minh.app.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mcontext;
    private List<Chat> mChat;
    private String imageURL;
    FirebaseUser fuser;

    public MessageAdapter(Context mcontext, List<Chat> mChat, String imageURL){
        this.mChat = mChat;
        this.mcontext = mcontext;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        if (imageURL.equals("default")){
            holder.profile_img.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mcontext).load(imageURL).into(holder.profile_img);
        }

//        if (position == mChat.size())
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_img;
        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_mess);
            profile_img = itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}
