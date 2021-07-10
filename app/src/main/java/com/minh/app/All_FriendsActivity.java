package com.minh.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class All_FriendsActivity extends AppCompatActivity {
    Intent intent;
    String userid;
    LinearLayout linear;
    EditText search;
    List<User> mUsers;

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
        setContentView(R.layout.activity_all__friends);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linear = findViewById(R.id.linear);
        search = findViewById(R.id.search);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        intent =  getIntent();
        userid = intent.getStringExtra("userid");
        mUsers = new ArrayList<>();
        linear = findViewById(R.id.linear);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                getSupportActionBar().setTitle(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seeAllFr(userid);
    }

    private void seeAllFr(String userid) {
        final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid)
                .child("friend")
                .child("acc");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                if(map!=null) {
                    final List<String> list_fr = new ArrayList<>(map.keySet());
                    for(int i =0; i<list_fr.size();i++){
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(list_fr.get(i));
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final User user = snapshot.getValue(User.class);
                                final String id = user.getId();
                                View view = layoutInflater.inflate(R.layout.user_item,linear,false);
                                CircleImageView profile_image;
                                TextView username,lastmessage;

                                profile_image = view.findViewById(R.id.profile_image);
                                username = view.findViewById(R.id.username);
                                lastmessage = view.findViewById(R.id.lastmessage);
                                username.setTextSize(17);
                                profile_image.getLayoutParams().height = 150;
                                profile_image.getLayoutParams().width = 150;
                                lastmessage.setVisibility(View.GONE);
                                username.setText(user.getUsername());
                                if(user.getImageURL().equals("default")){
                                    profile_image.setImageResource(R.mipmap.ic_launcher);
                                }else{
                                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                                }
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
                                        intent1.putExtra("userid", id);
                                        startActivity(intent1);
                                    }
                                });
                                linear.addView(view);
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