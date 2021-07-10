package com.minh.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Model.Home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Save_PostActivity extends AppCompatActivity {
    ImageView image;
    TextView content, creater;
    LinearLayout mylayout;
    FirebaseUser fuser;
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
        setContentView(R.layout.activity_save__post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Save Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        mylayout= findViewById(R.id.mylayout);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        readSavepost(fuser.getUid());
    }

    private void readSavepost(String id) {
        final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Save Post").child(id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> save = (HashMap<String, Object>) snapshot.getValue();
                if(save!=null){
                    List<String> post = new ArrayList<>(save.keySet());
                    Log.e("AAA", "Nhat Minh " + post.get(0));
                    for(int i=0;i<post.size();i++){
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(post.get(i));
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Home home = snapshot.getValue(Home.class);
                                View view = layoutInflater.inflate(R.layout.item_savepost,mylayout,false);
                                ImageView image;
                                TextView content, creater;
                                image = view.findViewById(R.id.image);
                                image.setClipToOutline(true);
                                content = view.findViewById(R.id.content);
                                creater = view.findViewById(R.id.creater);
                                if (home.getImg().equals("default")){
                                    if(home.getImageURL().equals("default")){
                                        image.setImageResource(R.drawable.ic_launcher_background);
                                    }else{
                                        Glide.with(getApplicationContext()).load(home.getImageURL()).into(image);
                                    }
                                }else{
                                    Glide.with(getApplicationContext()).load(home.getImg()).into(image);
                                }
                                content.setText(home.getContent());
                                creater.setText(home.getUsername());
                                mylayout.addView(view);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}