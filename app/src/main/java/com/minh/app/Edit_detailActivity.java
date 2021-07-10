package com.minh.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Edit_detailActivity extends AppCompatActivity {

    TextView add_work,add_education,add_current_city,add_home_town,add_relationship;
    Intent intent;
    String userid;
    String school, currentcity, hometown;
    Button save_btn;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                school = data.getStringExtra("education");
                add_education.setText("Studies at "+school);
            }
        }
        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                currentcity = data.getStringExtra("city");
                add_current_city.setText("Lives in "+currentcity);
            }
        }
        if(requestCode == 3){
            if(resultCode == Activity.RESULT_OK){
                hometown = data.getStringExtra("hometown");
                add_home_town.setText("Froms "+hometown);
            }
        }
    }
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
        setContentView(R.layout.activity_edit_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        save_btn = findViewById(R.id.save_btn);
        intent = getIntent();
        userid = intent.getStringExtra("userid");

        add_work = findViewById(R.id.add_work);
        add_education = findViewById(R.id.add_education);
        add_current_city=findViewById(R.id.add_current_city);
        add_home_town = findViewById(R.id.add_home_town);
        add_relationship= findViewById(R.id.add_relationship);

        add_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), AddActivity.class);
                intent1.putExtra("case", "add work");
                intent1.putExtra("userid",userid);
                startActivity(intent1);
            }
        });
        add_education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), AddActivity.class);
                intent1.putExtra("case", "add edu");
                intent1.putExtra("userid",userid);
                startActivityForResult(intent1,1);
            }
        });
        add_current_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), AddActivity.class);
                intent1.putExtra("case", "add current city");
                intent1.putExtra("userid",userid);
                startActivityForResult(intent1,2);
            }
        });
        add_home_town.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), AddActivity.class);
                intent1.putExtra("case", "add hometown");
                intent1.putExtra("userid",userid);
                startActivityForResult(intent1,3);
            }
        });
        add_relationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), AddActivity.class);
                intent1.putExtra("case", "add relationship");
                intent1.putExtra("userid",userid);
                startActivity(intent1);
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(userid)
                        .child("Profile");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Education", add_education.getText().toString());
                hashMap.put("Current City", add_current_city.getText().toString());
                hashMap.put("Hometown", add_home_town.getText().toString());
                reference.updateChildren(hashMap);
                finish();
            }
        });
    }
}