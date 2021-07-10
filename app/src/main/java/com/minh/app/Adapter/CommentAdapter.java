package com.minh.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minh.app.Model.Comment;
import com.minh.app.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    Context mcontext;
    List<Comment> comments;
    private FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

    public CommentAdapter (Context mcontext, List<Comment> comments){
        this.mcontext = mcontext;
        this.comments = comments;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Comment comment;
        comment = comments.get(position);
        holder.username.setText(comment.getUsername());
        holder.comment_txt.setText(comment.getContent());
        if(comment.getProfile_img().equals("default")){
            holder.profile_img.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mcontext).load(comment.getProfile_img()).into(holder.profile_img);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username, comment_txt;
        public CircleImageView profile_img;
        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            comment_txt = itemView.findViewById(R.id.comment_txt);
            profile_img = itemView.findViewById(R.id.profile_image);
        }
    }
}