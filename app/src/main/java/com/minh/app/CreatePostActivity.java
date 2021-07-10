package com.minh.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.minh.app.Model.Home;
import com.minh.app.Model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.System.currentTimeMillis;

public class CreatePostActivity extends AppCompatActivity {
    FirebaseUser fuser;
    private  String Username, Profile_img;
    DatabaseReference reference;
    public CircleImageView profile_img;
    public TextView username, post_btn;
    Button addtopost, Checkin, Golive, Tag, Background,Camera,Photo;
    public EditText post;
    public int A,B;
    PopupWindow popupWindow;
    Calendar cal;
    String currenttime, mUri;
    private static final int IMAGE_REQUEST =1;
    StorageReference storageReference;
    private Uri imageuri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    ImageView img;
    Intent intent;
    String PosttoGrp;
    String PosttoUser;
    VideoView test;
    @Override
    public void onBackPressed() {
        if(B==1){
            popupWindow.dismiss();
            B=0;
        }else{
            super.onBackPressed();
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
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        intent = getIntent();
        if(intent.getStringExtra("PostToGrp")!=null){
            PosttoGrp = intent.getStringExtra("PostToGrp");
        }else{
            PosttoGrp = "default";
        }
        if(intent.getStringExtra("PostToUser")!=null){
            PosttoGrp = intent.getStringExtra("PostToUser");
        }else{
            PosttoUser = "default";
        }
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        img = findViewById(R.id.img);
        post_btn = findViewById(R.id.post_btn);
        profile_img = findViewById(R.id.profile_img);
        username = findViewById(R.id.username);
        post = findViewById(R.id.post);
        addtopost = findViewById(R.id.addpost);
//        test = findViewById(R.id.test);
//        test.setVideoPath("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
////        MediaController controller = new MediaController(this);
////        controller.setAnchorView(this.test);
////        controller.setMediaPlayer(this.test);
////        this.test.setMediaController(controller);
//        test.start();
//        test.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setVolume(0,0);
//            }
//        });

        addtopost.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_add_photo_alternate_24,0);
        addtopost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B=1;
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View menupopup = layoutInflater.inflate(R.layout.popupmenu, null);
                int width = RelativeLayout.LayoutParams.MATCH_PARENT;
                int heigh = RelativeLayout.LayoutParams.WRAP_CONTENT;
                //boolean focusable = true;
                popupWindow = new PopupWindow(menupopup, width,heigh,false);
                popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);

                Checkin=menupopup.findViewById(R.id.Checkin);
                Golive = menupopup.findViewById(R.id.Golive);
                Tag= menupopup.findViewById(R.id.Tag);
                Background = menupopup.findViewById(R.id.Background);
                Camera= menupopup.findViewById(R.id.Camera);
                Photo = menupopup.findViewById(R.id.Photo);


                Photo.setVisibility(View.VISIBLE);
                Checkin.setVisibility(View.VISIBLE);
                Golive.setVisibility(View.VISIBLE);
                Tag.setVisibility(View.VISIBLE);
                Background.setVisibility(View.VISIBLE);
                Camera.setVisibility(View.VISIBLE);

                Photo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_add_photo_alternate_24,0,0,0);
                Checkin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_location_on_24,0,0,0);
                Golive.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_videocam_24,0,0,0);
                Tag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_person_add_24,0,0,0);
                Background.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_text_format_24,0,0,0);
                Camera.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_photo_camera_24,0,0,0);

                Photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImage();
                    }
                });
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(B==1)
                popupWindow.dismiss();
             B=0;
            }
        });
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        Date currentLocaltime = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        currenttime = dateFormat.format(currentLocaltime);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                Username = user.getUsername();
                Profile_img = user.getImageURL();
                if(user.getImageURL().equals("default")){
                    profile_img.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                if (value.equals("")){
                    post_btn.setTypeface(null,Typeface.NORMAL);
                    post_btn.setTextColor(Color.parseColor("#E0E0E0"));
                    A=0;
                }
                else {
                    post_btn.setTypeface(null, Typeface.BOLD);
                    post_btn.setTextColor(Color.parseColor("#66B2FF"));
                    A=1;
                }
            }
        });
            post_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(A==1){
                    posting(fuser.getUid(), post.getText().toString(), Username);
                    A++;
                    }
                }
            });


    }

    private void posting(String uid, String content, String Username) {
        try {
            reference = FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Creater", uid);
            if(content !=null){
                hashMap.put("Content", content);
            }else{
                hashMap.put("Content", "");
            }
            if (mUri == null) {
                hashMap.put("Img", "default");
            }else{
                hashMap.put("Img", mUri);
            }
            hashMap.put("PostToGr", PosttoGrp);
            hashMap.put("PostToUser", PosttoUser);
            hashMap.put("imageURL", Profile_img);
            hashMap.put("username", Username);
            hashMap.put("TimePost", currenttime);
            hashMap.put("Isshare", "false");
            hashMap.put("PostShare","default");
            reference.child("Post").push().setValue(hashMap);
            Toast.makeText(CreatePostActivity.this, "Posting...", Toast.LENGTH_SHORT).show();
            if(PosttoGrp.equals("default")){
                Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(CreatePostActivity.this, Group_ViewActivity.class);
                intent.putExtra("GroupID", PosttoGrp);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(CreatePostActivity.this);
        pd.setMessage("Uploading...");
        pd.show();
        if (imageuri != null){
            final StorageReference fileReference = storageReference.child(currentTimeMillis()
                    +"."+getFileExtension(imageuri));
            uploadTask = fileReference.putFile(imageuri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        mUri = downloadUri.toString();
                        img.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) post.getLayoutParams();
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        post.setLayoutParams(lp);
                        Glide.with(getApplicationContext()).load(mUri).into(img);
                        pd.dismiss();
                        popupWindow.dismiss();
                        post_btn.setTypeface(null, Typeface.BOLD);
                        post_btn.setTextColor(Color.parseColor("#66B2FF"));
                        A=1;
                    }else {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data!= null && data.getData() !=null){
            imageuri = data.getData();
        }
        if (uploadTask != null && uploadTask.isInProgress()){
            Toast.makeText(getApplicationContext(), "Upload is in progress", Toast.LENGTH_SHORT).show();
        }else {
            uploadImage();
        }
    }
}