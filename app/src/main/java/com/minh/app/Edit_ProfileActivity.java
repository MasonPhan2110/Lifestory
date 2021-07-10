package com.minh.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Model.User;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_ProfileActivity extends AppCompatActivity {

    Intent intent;
    String userid;
    CircleImageView profile_image;
    TextView edit_btn_profile,edit_btn_cover,edit_btn_bio,edit_btn_details,bio_btn,livein,from,workplace,education;
    ImageView coverphoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_image = findViewById(R.id.profile_image);
        edit_btn_profile = findViewById(R.id.edit_btn_profile);
        edit_btn_cover = findViewById(R.id.edit_btn_cover);
        edit_btn_bio = findViewById(R.id.edit_btn_bio);
        edit_btn_details = findViewById(R.id.edit_btn_details);
        coverphoto = findViewById(R.id.coverphoto);
        bio_btn = findViewById(R.id.bio_btn);
        livein = findViewById(R.id.livein);
        from = findViewById(R.id.from);
        workplace = findViewById(R.id.workplace);
        education = findViewById(R.id.education);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        intent = getIntent();
        userid =intent.getStringExtra("userid");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageURL().equals("")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
                if(user.getBackgorund_imgURL().equals("default")){
                    coverphoto.setImageResource(R.mipmap.ic_launcher_round);
                }
                else{
                    Glide.with(getApplicationContext()).load(user.getBackgorund_imgURL()).into(coverphoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        edit_btn_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Edit_detailActivity.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid)
                .child("Profile");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> hashMap = (HashMap<String, Object>) snapshot.getValue();
                if (hashMap != null) {
                    if(!hashMap.get("Bio").equals("")){
                        bio_btn.setText(hashMap.get("Bio").toString());
                    }else{
                        bio_btn.setHint("Descripe yourself...");
                    }
                }else{
                    bio_btn.setHint("Descripe yourself...");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        livein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Edit_detailActivity.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Edit_detailActivity.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
        workplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Edit_detailActivity.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Edit_detailActivity.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
        bio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Edit_bioActivity.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });

        edit_btn_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Edit_bioActivity.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
    }
}