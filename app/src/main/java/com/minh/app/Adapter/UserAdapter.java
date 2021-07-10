package com.minh.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Printer;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.MessageActivity;
import com.minh.app.Model.Chat;
import com.minh.app.Model.User;
import com.minh.app.ProfileActivity;
import com.minh.app.R;

import org.w3c.dom.Text;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> users;
    public int A=0;
    private boolean ischat;
    String thelastmsg;

    public UserAdapter(Context context, List<User> users, int A, boolean ischat){
        this.context = context;
        this.users = users;
        this.A = A;
        this.ischat = ischat;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
        }

        if (ischat) {
            lastMessage(user.getId(), holder.last_msg);
        }
        else{
            holder.last_msg.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("AAA", String.valueOf(A));
                if (A==0){
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("userid", user.getId());
                    context.startActivity(intent);
                }
                if (A==1){
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("userid", user.getId());
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;
        //private ImageView img_on;
        //private ImageView img_off;
        public TextView last_msg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            last_msg = itemView.findViewById(R.id.lastmessage);
        }
    }
    private void lastMessage(final String userid, final TextView last_msg){
        thelastmsg = "";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)
                    || chat.getReceiver().equals(userid)&&chat.getSender().equals(firebaseUser.getUid())){
                        thelastmsg= chat.getMessage();
                    }
                    if (!chat.isIsseen()&&chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)){
                        last_msg.setTypeface(null, Typeface.BOLD);
                    }
                    else{
                        last_msg.setTypeface(null, Typeface.NORMAL);
                    }
                }
                switch (thelastmsg){
                    case "":
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(thelastmsg);
                        break;
                }
                thelastmsg = "";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
