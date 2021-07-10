package com.minh.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ViewPictureActivity extends AppCompatActivity {

    Intent intent;
    ImageView imageView;
    String imglink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        window.getDecorView().setSystemUiVisibility(0);
        imageView = findViewById(R.id.img);
        intent = getIntent();
        imglink = intent.getStringExtra("imgurl");
        if(imglink.equals("default")){
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }else{
            Glide.with(getApplicationContext()).load(imglink).into(imageView);
        }
    }
}