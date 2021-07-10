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

public class Add_MemberActivity extends AppCompatActivity {

    Intent intent;
    EditText search_bar;
    TextView done;
    LinearLayout mylayout;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    List<User> mUser;
    String GroupID,NameGrp,From;
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
        setContentView(R.layout.activity_add__member);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Invite Member");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        intent = getIntent();
        From = intent.getStringExtra("from");
        GroupID = intent.getStringExtra("GroupID");
        NameGrp = intent.getStringExtra("NameGrp");
        search_bar = findViewById(R.id.search_bar);
        mylayout = findViewById(R.id.mylayout);
        done = findViewById(R.id.done);
        mUser = new ArrayList<>();
        list_fr(fuser.getUid());
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Group_ViewActivity.class);
                intent.putExtra("GroupID", GroupID);
                startActivity(intent);
                finish();
            }
        });
    }

    private void list_fr(String uid) {
        final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .child("friend");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
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
                                final View view = layoutInflater.inflate(R.layout.user_item,mylayout,false);
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
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Noti")
                                                .child(id);
                                        String key = databaseReference.push().getKey();
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("ID",key);
                                        hashMap.put("Sent", fuser.getUid());
                                        hashMap.put("Group",GroupID);
                                        hashMap.put("NameGrp",NameGrp);
                                        hashMap.put("Type","InviteGrp");
                                        hashMap.put("IsRead","False");
                                        hashMap.put("Reject","False");
                                        databaseReference.child(key).setValue(hashMap);
                                        view.setVisibility(View.GONE);
                                    }
                                });
                                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Group")
                                        .child(GroupID)
                                        .child("Member");
                                databaseReference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        HashMap<String, Object> hashMap = (HashMap<String, Object>) snapshot.getValue();
                                        if(!hashMap.containsKey(id)){
                                            mylayout.addView(view);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
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