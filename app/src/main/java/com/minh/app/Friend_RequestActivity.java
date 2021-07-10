package com.minh.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.minh.app.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Friend_RequestActivity extends AppCompatActivity {
    LinearLayout mylayout;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
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
        setContentView(R.layout.activity_friend__request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        mylayout = findViewById(R.id.my_layout);
        readFriendrequest(fuser.getUid());
    }

    private void readFriendrequest(final String uid) {
        final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .child("friend")
                .child("request");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                if (map != null) {
                    final List<String> fr_Request = new ArrayList<>(map.keySet());
                    for (int i = 0; i < fr_Request.size(); i++) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(fr_Request.get(i));
                        final int finalI = i;
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                View view = layoutInflater.inflate(R.layout.item_fr_request, mylayout, false);
                                CircleImageView ava;
                                TextView name_user;
                                Button confirm, delete;
                                ava = view.findViewById(R.id.ava);
                                name_user = view.findViewById(R.id.name_user);
                                confirm = view.findViewById(R.id.confirm);
                                delete = view.findViewById(R.id.delete);
                                if (user.getImageURL().equals("default")) {
                                    ava.setImageResource(R.mipmap.ic_launcher);
                                } else {
                                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(ava);
                                }
                                name_user.setText(user.getUsername());
                                confirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                                                .child(uid)
                                                .child("friend")
                                                .child("acc");
                                        HashMap<String,Object> hashMap = new HashMap<>();
                                        hashMap.put(fr_Request.get(finalI),"true");
                                        reference1.updateChildren(hashMap);
                                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users")
                                                .child(fr_Request.get(finalI))
                                                .child("friend")
                                                .child("acc");
                                        HashMap<String, Object> hashMap1 = new HashMap<>();
                                        hashMap1.put(uid,"true");
                                        reference2.updateChildren(hashMap1);
                                    }
                                });
                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
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