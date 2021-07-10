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
import com.minh.app.Model.Group;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Your_GroupActivity extends AppCompatActivity {
    LinearLayout layout_grp_manage,other_grp;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();;

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
        setContentView(R.layout.activity_your__group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        layout_grp_manage = findViewById(R.id.layout_grp_manage);
        other_grp = findViewById(R.id.other_grp);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(fuser.getUid())
                .child("Group");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> all_grp = (Map<String, String>) snapshot.getValue();
                List<String> manage_grp = new ArrayList<>();
                List<String> other_grp = new ArrayList<>();
                if(all_grp!=null){
                    List<String> grp_list = new ArrayList<>(all_grp.keySet());
                    List<String> role = new ArrayList<>(all_grp.values());
                    for(int i=0;i<all_grp.size();i++){
                        if(role.get(i).equals("admin")){
                            manage_grp.add(grp_list.get(i));
                        }else{
                            other_grp.add(grp_list.get(i));
                        }
                    }
                    for(int i = 0;i<manage_grp.size();i++){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group")
                                .child(manage_grp.get(i));
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Group grp = snapshot.getValue(Group.class);
                                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grp_item,layout_grp_manage,false);
                                ImageView grp_ava;
                                TextView group_name;

                                grp_ava = view.findViewById(R.id.grp_ava);
                                group_name = view.findViewById(R.id.name_gr);

                                if(grp.getBackGroundImg().equals("default")){
                                    grp_ava.setImageResource(R.mipmap.ic_launcher);
                                }else{
                                    Glide.with(getApplicationContext()).load(grp.getBackGroundImg()).into(grp_ava);
                                }
                                group_name.setText(grp.getNameGroup());
                                layout_grp_manage.addView(view);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    for(int i = 0;i<other_grp.size();i++){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group")
                                .child(other_grp.get(i));
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final Group grp = snapshot.getValue(Group.class);
                                final String id = grp.getGroupID();
                                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.grp_item,layout_grp_manage,false);
                                ImageView grp_ava;
                                TextView group_name;

                                grp_ava = view.findViewById(R.id.grp_ava);
                                group_name = view.findViewById(R.id.name_gr);

                                if(grp.getBackGroundImg().equals("default")){
                                    grp_ava.setImageResource(R.mipmap.ic_launcher);
                                }else{
                                    Glide.with(getApplicationContext()).load(grp.getBackGroundImg()).into(grp_ava);
                                }
                                group_name.setText(grp.getNameGroup());
                                layout_grp_manage.addView(view);
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), Group_ViewActivity.class);
                                        intent.putExtra("GroupID",id);
                                        startActivity(intent);
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