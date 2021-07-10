package com.minh.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static java.lang.System.currentTimeMillis;

public class CreateActivity extends AppCompatActivity {
    EditText name_gr;
    TextView add_cover,privacy, text_public, text_private;
    Button create_grp;
    FirebaseUser fuser;
    StorageReference storageReference;
    private Uri imageuri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private static final int IMAGE_REQUEST =1;
    String mUri;
    ImageView Background_grp;

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
        setContentView(R.layout.activity_create);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        name_gr = findViewById(R.id.name_gr);
        add_cover = findViewById(R.id.add_cover);
        privacy = findViewById(R.id.privacy);
        create_grp = findViewById(R.id.create_grp);
        Background_grp = findViewById(R.id.grp_background);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View menupopup = layoutInflater.inflate(R.layout.privacy_menu, null);
                int width = RelativeLayout.LayoutParams.MATCH_PARENT;
                int heigh = RelativeLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(menupopup, width,heigh,focusable);
                popupWindow.showAtLocation(CreateActivity.this.findViewById(R.id.relativeayout), Gravity.BOTTOM,0,0);
                View container = (View) popupWindow.getContentView().getParent();
                Context context = popupWindow.getContentView().getContext();
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams wp = (WindowManager.LayoutParams) container.getLayoutParams();
                wp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                wp.dimAmount =0.2f;
                wm.updateViewLayout(container,wp);

                final RadioButton choose_public, choose_private;
                choose_private = menupopup.findViewById(R.id.choose_private);
                choose_public = menupopup.findViewById(R.id.choose_public);
                text_private = menupopup.findViewById(R.id.text_private);
                text_public = menupopup.findViewById(R.id.text_public);
                //onRadioButtonClicked(menupopup.findViewById(R.id.radio_gr));
                text_public.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choose_public.setChecked(true);
                        setText("Public");
                    }
                });
                text_private.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choose_private.setChecked(true);
                        setText("Private");
                    }
                });
            }
        });
        add_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        create_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group");
                String key = reference.push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("GroupID",key);
                hashMap.put("NameGroup", name_gr.getText().toString());
                hashMap.put("Privacy",privacy.getText().toString());
                hashMap.put("Creater", fuser.getUid());
                if (mUri == null) {
                    hashMap.put("BackGroundImg", "default");
                }else{
                    hashMap.put("BackGroundImg", mUri);
                }
                reference.child(key).setValue(hashMap);
                HashMap<String, Object> map = new HashMap<>();
                map.put(fuser.getUid(),"Admin");
                reference.child(key).child("Member").updateChildren(map);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(fuser.getUid())
                        .child("Group");
                HashMap<String, Object> map1 = new HashMap<>();
                map1.put(key,"Admin");
                databaseReference.updateChildren(map1);
                Intent intent = new Intent(getApplicationContext(), Add_MemberActivity.class);
                intent.putExtra("GroupID", key);
                intent.putExtra("NameGrp",name_gr.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }
    private void setText(String text) {
        privacy.setText(text);
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.choose_private:
                if(checked)
                    privacy.setText("Private");
                break;
            case R.id.choose_public:
                if(checked)
                    privacy.setText("Public");
                break;
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
        final ProgressDialog pd = new ProgressDialog(CreateActivity.this);
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
                        Background_grp.setVisibility(View.VISIBLE);
//                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) post.getLayoutParams();
//                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                        post.setLayoutParams(lp);
                        add_cover.setVisibility(View.GONE);
                        Glide.with(getApplicationContext()).load(mUri).into(Background_grp);
                        pd.dismiss();
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