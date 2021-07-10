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
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Adapter.CommentAdapter;
import com.minh.app.Adapter.ViewPostAdapter;
import com.minh.app.Model.Comment;
import com.minh.app.Model.Home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class viewPostActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Intent intent;
    List<Comment> mcomment;
    ViewPostAdapter viewPostAdapter;
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
        setContentView(R.layout.activity_view_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        mcomment = new ArrayList<>();
        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        intent= getIntent();
        final String PostID = intent.getStringExtra("idPost");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(PostID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Home home = snapshot.getValue(Home.class);
                getSupportActionBar().setTitle(home.getUsername());
                home.setId(snapshot.getKey());
                readPost(home, PostID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPost(final Home home, String postID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comment")
                .child(postID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mcomment.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    final Comment comment = dataSnapshot.getValue(Comment.class);
                    assert comment != null;
                    mcomment.add(comment);
                }
                Collections.reverse(mcomment);
                viewPostAdapter = new ViewPostAdapter(home,getApplicationContext(), mcomment);
                recyclerView.setAdapter(viewPostAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}