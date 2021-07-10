package com.minh.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Adapter.CommentAdapter;
import com.minh.app.Model.Comment;
import com.minh.app.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText comment_send;
    ImageButton btn_send;
    FirebaseUser fuser;
    Intent intent;
    List<Comment> mcomment;
    CommentAdapter commentAdapter;
    String Username, profile_img;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        final String postID = intent.getStringExtra("homeid");

        mcomment = new ArrayList<>();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        comment_send = findViewById(R.id.comment_send);
        btn_send = findViewById(R.id.btn_send);

        readcomment(postID);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Username = user.getUsername();
                profile_img = user.getImageURL();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmt = comment_send.getText().toString();
                if(!cmt.equals("")){
                    sendcomment(fuser.getUid(), cmt, postID);
                }else{
                    Toast.makeText(CommentActivity.this, "You cant send empty comment", Toast.LENGTH_SHORT).show();
                }
                comment_send.setText("");
            }
        });

    }

    private void readcomment( String postid) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comment")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mcomment.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    final Comment comment = dataSnapshot.getValue(Comment.class);
                    assert comment != null;
                    mcomment.add(comment);
                }
                Collections.reverse(mcomment);
                commentAdapter = new CommentAdapter(getApplicationContext(), mcomment);
                recyclerView.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendcomment(final String uid, String cmt, String postID) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comment")
                .child(postID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Creater", uid);
        hashMap.put("Content", cmt);
        hashMap.put("username", Username);
        hashMap.put("profile_img", profile_img);
        reference.push().updateChildren(hashMap);

    }
}