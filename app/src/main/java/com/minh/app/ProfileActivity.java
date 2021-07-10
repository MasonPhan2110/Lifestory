package com.minh.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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
import com.minh.app.Adapter.HomeAdapter;
import com.minh.app.Fragments.HomeFragment;
import com.minh.app.Model.Home;
import com.minh.app.Model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    Button btn_addfriend;
    Button message_btn, requested_btn, response_btn, fr_message_btn, fr_menu,edit_details,showpicture, changepicture,see_all_fr;
    ImageView background_img,profile_img_fr1,profile_img_fr2,profile_img_fr3,profile_img_fr4,profile_img_fr5,profile_img_fr6;
    CircleImageView profile_img;
    TextView username, livein,from,about,edu,fr1_name,fr2_name,fr3_name,fr4_name,fr5_name,fr6_name,text_count_fr;
    DatabaseReference reference;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storageReference;
    private static final int IMAGE_REQUEST =1;
    public int A;
    private Uri imageuri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    String userid;
    Intent intent, intent1;
    String id;
    LinearLayout mylayout,fr1,fr2,fr3,fr4,fr5,fr6;
    PopupWindow popupWindow;
    List<Home> mhome;
    CardView fr_list;
    SwipeRefreshLayout refresh;
    String currenttime;
    Calendar cal;
    HomeAdapter homeAdapter;
    RecyclerView recycle_view;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mhome = new ArrayList<>();
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        Date currentLocaltime = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        currenttime = dateFormat.format(currentLocaltime);

//        recycle_view = findViewById(R.id.recycle_view);
//        recycle_view.setHasFixedSize(true);
//        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recycle_view.setLayoutManager(linearLayoutManager);

        see_all_fr = findViewById(R.id.see_all_fr);
        livein = findViewById(R.id.livein);
        from = findViewById(R.id.from);
        about = findViewById(R.id.about);
        edit_details = findViewById(R.id.edit_details);
        requested_btn = findViewById(R.id.requested);
        background_img = findViewById(R.id.background_img);
        profile_img = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_addfriend = findViewById(R.id.add_fr);
        message_btn = findViewById(R.id.message_btn);
        response_btn = findViewById(R.id.response);
        fr_message_btn = findViewById(R.id.fr_message);
        fr_menu = findViewById(R.id.fr_menu);
        mylayout = (LinearLayout) findViewById(R.id.mylayout);
        edu = findViewById(R.id.edu);
        profile_img_fr1 = findViewById(R.id.profile_img_fr1);
        profile_img_fr2 = findViewById(R.id.profile_img_fr2);
        profile_img_fr3 = findViewById(R.id.profile_img_fr3);
        profile_img_fr4 = findViewById(R.id.profile_img_fr4);
        profile_img_fr5 = findViewById(R.id.profile_img_fr5);
        profile_img_fr6 = findViewById(R.id.profile_img_fr6);
        fr1_name = findViewById(R.id.fr1_name);
        fr2_name = findViewById(R.id.fr2_name);
        fr3_name = findViewById(R.id.fr3_name);
        fr4_name = findViewById(R.id.fr4_name);
        fr5_name = findViewById(R.id.fr5_name);
        fr6_name = findViewById(R.id.fr6_name);
        text_count_fr = findViewById(R.id.text_count_fr);
        fr1 = findViewById(R.id.fr1);
        fr2 = findViewById(R.id.fr2);
        fr3 = findViewById(R.id.fr3);
        fr4 = findViewById(R.id.fr4);
        fr5 = findViewById(R.id.fr5);
        fr6 = findViewById(R.id.fr6);
        fr_list = findViewById(R.id.fr_list);
        refresh = findViewById(R.id.refresh);



        see_all_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ProfileActivity.this, All_FriendsActivity.class);
                intent1.putExtra("userid",id);
                startActivity(intent1);
            }
        });


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        intent = getIntent();
        userid =intent.getStringExtra("userid");
        if(userid==null||userid.equals(fuser.getUid())){
            id = fuser.getUid();
        }else{
            id = userid;
        }
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mylayout.removeViews(4, mylayout.getChildCount()-4);
                friend_list(id);
                wall(id);
                refresh.setRefreshing(false);
            }
        });
        friend_list(id);
        wall(id);
        A =0;
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        if(userid==null||userid.equals(fuser.getUid())){
            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            edit_details.setVisibility(View.VISIBLE);
        }else{
            edit_details.setVisibility(View.GONE);
            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
            message_btn.setVisibility(View.VISIBLE);
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                    .child(fuser.getUid())
                    .child("friend");
            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() == null){
                        btn_addfriend.setVisibility(View.VISIBLE);
                    }else{
                        HashMap<String, Object> fr_acc = new HashMap<>();
                        HashMap<String,Object> fr_request = new HashMap<>();
                        HashMap<String, Object> fr_requested = new HashMap<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(dataSnapshot.getKey().equals("acc")){
                            fr_acc =(HashMap<String, Object>)  dataSnapshot.getValue();
                        }else if(dataSnapshot.getKey().equals("request")){
                            fr_request =(HashMap<String, Object>)  dataSnapshot.getValue();
                        }else if (dataSnapshot.getKey().equals("requested")){
                            fr_requested =(HashMap<String, Object>)  dataSnapshot.getValue();
                        }
                        if(fr_acc.containsKey(userid)){
                            btn_addfriend.setVisibility(View.GONE);
                            requested_btn.setVisibility(View.GONE);
                            fr_message_btn.setVisibility(View.VISIBLE);
                            message_btn.setVisibility(View.GONE);
                            response_btn.setVisibility(View.GONE);
                            fr_menu.setVisibility(View.VISIBLE);
                        }else if(fr_request.containsKey(userid)){
                            response_btn.setVisibility(View.VISIBLE);
                            btn_addfriend.setVisibility(View.GONE);
                            requested_btn.setVisibility(View.GONE);
                        }else if(fr_requested.containsKey(userid)){
                            response_btn.setVisibility(View.GONE);
                            btn_addfriend.setVisibility(View.GONE);
                            requested_btn.setVisibility(View.VISIBLE);
                        }else{
                            btn_addfriend.setVisibility(View.VISIBLE);
                        }
                    }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_img.setImageResource(R.mipmap.ic_launcher);
                }else  {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_img);
                }
                if(user.getBackgorund_imgURL().equals("default")){
                    background_img.setImageResource(R.mipmap.ic_launcher_round);
                }
                else{
                    Glide.with(getApplicationContext()).load(user.getBackgorund_imgURL()).into(background_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id).child("Profile");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() ==  null){
                    livein.setVisibility(View.GONE);
                    from.setVisibility(View.GONE);
                    about.setVisibility(View.GONE);
                    edu.setVisibility(View.GONE);
                }else{
                    Map<String, Object> hashMap = (HashMap<String, Object>) snapshot.getValue();
                    livein.setVisibility(View.VISIBLE);
                    from.setVisibility(View.VISIBLE);
                    about.setVisibility(View.VISIBLE);
                    edu.setVisibility(View.VISIBLE);
                    if(hashMap.get("Current City") != null){
                        livein.setText(hashMap.get("Current City").toString());
                    }else{
                        livein.setVisibility(View.GONE);
                    }
                    if(hashMap.get("Education") != null){
                        edu.setText(hashMap.get("Education").toString());
                    }else{
                        edu.setVisibility(View.GONE);
                    }
                    if(hashMap.get("Hometown") != null){
                        from.setText(hashMap.get("Hometown").toString());
                    }else{
                        from.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        edit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Edit_ProfileActivity.class);
                intent.putExtra("userid", id);
                startActivity(intent);
            }
        });
        profile_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    A = 0;
                    popupmenu(A, id);
                }
            });
        background_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    A = 1;
                    popupmenu(A, id);
                }
            });
        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), MessageActivity.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
            }
        });
        btn_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend(fuser.getUid(), userid);
            }
        });
        response_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response_fr(fuser.getUid(), userid);
            }
        });
        fr_message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), MessageActivity.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
    }
    private void friend_list(String id){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                .child(id)
                .child("friend")
                .child("acc");
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                if (map != null) {
                    final List<String> list_fr = new ArrayList<>(map.keySet());
                    int count;
                    if (list_fr.size() == 1) {
                        text_count_fr.setText(list_fr.size() + " friend");
                    } else {
                        text_count_fr.setText(list_fr.size() + " friends");
                    }
                    if (list_fr.size() >= 6) {
                        count = 6;
                    } else {
                        count = list_fr.size();
                    }
                    if (list_fr.size() == 0) {
                        fr_list.setVisibility(View.GONE);
                    } else {
                        fr_list.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < count; i++) {
                        if (i == 0) {
                            profile_img_fr1.setVisibility(View.VISIBLE);
                            fr1_name.setVisibility(View.VISIBLE);
                            showfr(list_fr.get(0), profile_img_fr1, fr1_name);
                        } else if (i == 1) {
                            profile_img_fr2.setVisibility(View.VISIBLE);
                            fr2_name.setVisibility(View.VISIBLE);
                            showfr(list_fr.get(1), profile_img_fr2, fr2_name);
                        } else if (i == 2) {
                            profile_img_fr3.setVisibility(View.VISIBLE);
                            fr3_name.setVisibility(View.VISIBLE);
                            showfr(list_fr.get(2), profile_img_fr3, fr3_name);
                        } else if (i == 3) {
                            profile_img_fr4.setVisibility(View.VISIBLE);
                            fr4_name.setVisibility(View.VISIBLE);
                            showfr(list_fr.get(3), profile_img_fr4, fr4_name);
                        } else if (i == 4) {
                            profile_img_fr5.setVisibility(View.VISIBLE);
                            fr5_name.setVisibility(View.VISIBLE);
                            showfr(list_fr.get(4), profile_img_fr5, fr5_name);
                        } else if (i == 5) {
                            profile_img_fr6.setVisibility(View.VISIBLE);
                            fr6_name.setVisibility(View.VISIBLE);
                            showfr(list_fr.get(5), profile_img_fr6, fr6_name);
                        }
                    }
                    fr1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                            intent.putExtra("userid", list_fr.get(0));
                            startActivity(intent);
                        }
                    });
                    fr2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                            intent.putExtra("userid", list_fr.get(1));
                            startActivity(intent);
                        }
                    });
                    fr3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                            intent.putExtra("userid", list_fr.get(2));
                            startActivity(intent);
                        }
                    });
                    fr4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                            intent.putExtra("userid", list_fr.get(3));
                            startActivity(intent);
                        }
                    });
                    fr5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                            intent.putExtra("userid", list_fr.get(4));
                            startActivity(intent);
                        }
                    });
                    fr6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                            intent.putExtra("userid", list_fr.get(5));
                            startActivity(intent);
                        }
                    });
                }
            }

                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }
        });
    }
    private void wall(final String user_id){
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Post");
        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mhome.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Home home = dataSnapshot.getValue(Home.class);
                    if(home.getCreater().equals(user_id)&&home.getPostToGr().equals("default")){
                        home.setId(dataSnapshot.getKey());
                        mhome.add(home);
                    }
                }
                Collections.reverse(mhome);
                LayoutInflater layoutInflater = LayoutInflater.from(ProfileActivity.this);
                for(final Home item : mhome ){
                    final View view = layoutInflater.inflate(R.layout.item_home,mylayout,false);
                    final String id = item.getId();
                    final TextView username1, content, like_count, comment_count,username_share, content_share,text_change;
                    final ImageView  picture,picture_share;
                    final CircleImageView profile_image,profile_image_share;
                    final Button like, liked, comment;
                    RelativeLayout view_like;
                    MaterialCardView cardView;
                    ImageButton menu_home;

                    text_change = view.findViewById(R.id.text_change);
                    menu_home = view.findViewById(R.id.menu_home);
                    cardView = view.findViewById(R.id.card_view);
                    username_share= view.findViewById(R.id.username_share);
                    content_share = view.findViewById(R.id.content_share);
                    profile_image_share = view.findViewById(R.id.profile_image_share);
                    picture_share = view.findViewById(R.id.picture_share);
                    view_like = view.findViewById(R.id.view_like);
                    comment_count = view.findViewById(R.id.comment_count);
                    username1 = view.findViewById(R.id.username);
                    content = view.findViewById(R.id.content);
                    like_count = view.findViewById(R.id.like_count);
                    profile_image = view.findViewById(R.id.profile_image);
                    picture = view.findViewById(R.id.picture);
                    like= view.findViewById(R.id.like);
                    liked= view.findViewById(R.id.liked);
                    comment = view.findViewById(R.id.comment);
                    username1.setText(item.getUsername());
                    content.setText(item.getContent());
                    menu_home = view.findViewById(R.id.menu_home);

                    menu_home.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menu_home_popup(item.getCreater(), v, item.getId());
                        }
                    });
                    picture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ViewPictureActivity.class);
                            intent.putExtra("imgurl", item.getImg());
                            startActivity(intent);
                        }
                    });
                    if(item.getType()!=null){
                        if(item.getType().equals("changeProfile")){
                            text_change.setVisibility(View.VISIBLE);
                            text_change.setText(" đã thay đổi ảnh đại diện");
                            content.setVisibility(View.GONE);
                        }else if(item.getType().equals("changeBackground")){
                            text_change.setText(" đã thay đổi ảnh background");
                            content.setVisibility(View.GONE);
                        }
                    }
                    if(!item.getIsshare().equals("True")){
                        cardView.setVisibility(View.GONE);
                        picture.setVisibility(View.VISIBLE);
                    }else{
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post")
                                .child(item.getPostShare());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Home home_shared = snapshot.getValue(Home.class);
                                username_share.setText(home_shared.getUsername());
                                content_share.setText(home_shared.getContent());
                                final String img_share = home_shared.getImg();
                                if(home_shared.getImageURL().equals("default")){
                                    profile_image_share.setImageResource(R.mipmap.ic_launcher);
                                }else{
                                    Glide.with(getApplicationContext()).load(home_shared.getImageURL()).into(profile_image_share);
                                }
                                if(home_shared.getImg().equals("default")){
                                    picture_share.setVisibility(View.GONE);
                                }else{
                                    Glide.with(getApplicationContext()).load(home_shared.getImg()).into(picture_share);
                                    if(home_shared.getImg().equals("default")){
                                        picture_share.setVisibility(View.GONE);
                                    }else{
                                        Glide.with(getApplicationContext()).load(home_shared.getImg()).into(picture_share);
                                        picture_share.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), ViewPictureActivity.class);
                                                intent.putExtra("imgurl", img_share);
                                                startActivity(intent);
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
                    if(item.getImageURL().equals("default")){
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    }else{
                        Glide.with(getApplicationContext()).load(item.getImageURL()).into(profile_image);
                    }
                    if(item.getImg().equals("default")){
                        picture.setVisibility(View.GONE);
                    }else{
                        Glide.with(getApplicationContext()).load(item.getImg()).into(picture);
                    }
                    view_like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                            intent.putExtra("homeid", id);
                            startActivity(intent);
                        }
                    });
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Like")
                            .child(item.getId());
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Map<String, Object> liker = (HashMap<String, Object>) snapshot.getValue();
                            int likecount = (int) snapshot.getChildrenCount();
                            Log.e("AAA", String.valueOf(likecount));
                            if(liker!=null){
                                if(liker.containsKey(fuser.getUid())){
                                    liked.setVisibility(View.VISIBLE);
                                    like.setVisibility(View.GONE);
                                }
                            }else{
                                liked.setVisibility(View.GONE);
                                like.setVisibility(View.VISIBLE);
                                like_count.setText("");
                            }
                            if (likecount == 0){
                                like_count.setVisibility(View.VISIBLE);
                            }else if(likecount == 1){
                                like_count.setText(likecount +" like");
                            }
                            else{
                                like_count.setText(likecount +" likes");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                holder.like.setVisibility(View.GONE);

                            liked.setVisibility(View.VISIBLE);
                            like.setVisibility(View.GONE);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Like")
                                    .child(id);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(fuser.getUid(), "Like");
                            reference.updateChildren(hashMap);
                        }
                    });
                    liked.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Like")
                                    .child(id)
                                    .child(fuser.getUid());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().removeValue();
                                    liked.setVisibility(View.GONE);
                                    like.setVisibility(View.VISIBLE);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    });
                    comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                            intent.putExtra("homeid", id);
                            startActivity(intent);
                        }
                    });
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comment")
                            .child(id);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int commentcount = (int) snapshot.getChildrenCount();
                            if(commentcount == 0) {
                                comment_count.setText("");
                            }else if(commentcount ==1){
                                comment_count.setText("1 comment");
                            }else{
                                comment_count.setText(commentcount + " comments");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    if(item.getPostToGr().equals("default")){
                        mylayout.addView(view);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void menu_home_popup(String creater, View v, final String id) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View menupopup = layoutInflater.inflate(R.layout.menu_home, null);
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int heigh = RelativeLayout.LayoutParams.WRAP_CONTENT;
        //boolean focusable = true;
        popupWindow = new PopupWindow(menupopup, width,heigh,true);
        popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
        Button edit_post, delete_post, save_post, hide_post;
        hide_post = menupopup.findViewById(R.id.hide_post);
        edit_post = menupopup.findViewById(R.id.edit_post);
        delete_post = menupopup.findViewById(R.id.delete_post);
        save_post = menupopup.findViewById(R.id.save_post);
        if(!creater.equals(fuser.getUid())){
            hide_post.setVisibility(View.VISIBLE);
        }
        if(creater.equals(fuser.getUid())){
            edit_post.setVisibility(View.VISIBLE);
            delete_post.setVisibility(View.VISIBLE);
        }
        delete_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post").child(id);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Like").child(id);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Comment").child(id);
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                popupWindow.dismiss();
            }
        });
    }
    private void response_fr(final String fUid, final String userid) {
        PopupMenu menu =  new PopupMenu(this, response_btn, Gravity.BOTTOM);
        menu.inflate(R.menu.menu);
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.accept:
                        accept(fUid,userid);
                        return true;
                    case R.id.refuse:
                        refuse(fUid,userid);
                        return true;
                    default:
                        return ProfileActivity.super.onOptionsItemSelected(item);
                }
            }
        });
    }
    private void refuse(String fUid, String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(fUid)
                .child("friend")
                .child("request")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid)
                .child("friend")
                .child("requested")
                .child(fUid);
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void accept(String fUid, final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(fUid)
                .child("friend")
                .child("acc");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(userid,"true");
        reference.updateChildren(hashMap);
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid)
                .child("friend")
                .child("acc");
        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put(fUid,"true");
        reference1.updateChildren(hashMap1);
        response_btn.setVisibility(View.GONE);
        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(fUid)
                .child("request")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid)
                .child("requested")
                .child(fUid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addFriend(final String uid, final String userid) {
        reference= FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .child("friend")
                .child("requested");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(userid,"Requested");
        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    btn_addfriend.setVisibility(View.GONE);
                    requested_btn.setVisibility(View.VISIBLE);
                    DatabaseReference reference1;
                    reference1= FirebaseDatabase.getInstance().getReference("Users")
                            .child(userid)
                            .child("friend")
                            .child("request");
                    HashMap<String,Object> hashMap1 = new HashMap<>();
                    hashMap1.put(uid,"Get Requested");
                    reference1.updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ProfileActivity.this, "Send Complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(ProfileActivity.this, "Can not send friend request", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
        pd.setMessage("Uploading...");
        pd.show();

        if (imageuri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
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
                        String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        final HashMap<String, Object> map = new HashMap<>();
                        if (A==0) {
                            map.put("imageURL", mUri);
                        }
                        else {
                            map.put("backgorund_imgURL",mUri);
                        }
                        reference.updateChildren(map);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("Creater", fuser.getUid());
                        hashMap.put("Content", "");
                        if (mUri == null) {
                            hashMap.put("Img", "default");
                        }else{
                            hashMap.put("Img", mUri);
                        }
                        hashMap.put("PostToGr", "default");
                        hashMap.put("PostToUser", "default");
                        hashMap.put("imageURL", mUri);
                        hashMap.put("username", username.getText());
                        hashMap.put("TimePost", currenttime);
                        if(A==0) {
                            hashMap.put("Type", "changeProfile");
                        }
                        else{
                            hashMap.put("Type", "changeBackground");
                        }
                        hashMap.put("Isshare", "false");
                        hashMap.put("PostShare","default");
                        databaseReference.push().setValue(hashMap);
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
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode==RESULT_OK
        && data!= null && data.getData() !=null){
            imageuri = data.getData();
        }
        if(requestCode == RESULT_CANCELED){

        }
        if (uploadTask != null && uploadTask.isInProgress()){
            Toast.makeText(getApplicationContext(), "Upload is in progress", Toast.LENGTH_SHORT).show();
        }else {
            uploadImage();
        }
    }
    public void popupmenu(int A, final String id){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View menupopup = layoutInflater.inflate(R.layout.popupmenu, null);
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int heigh = RelativeLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(menupopup, width,heigh,focusable);
        popupWindow.showAtLocation(ProfileActivity.this.findViewById(R.id.relativeayout), Gravity.BOTTOM,0,0);
        View container = (View) popupWindow.getContentView().getParent();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wp = (WindowManager.LayoutParams) container.getLayoutParams();
        wp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wp.dimAmount =0.4f;
        wm.updateViewLayout(container,wp);
        showpicture = menupopup.findViewById(R.id.viewpicture);
        changepicture = menupopup.findViewById(R.id.changepicture);
        if(id.equals(fuser.getUid())){
            showpicture.setVisibility(View.VISIBLE);
            changepicture.setVisibility(View.VISIBLE);
        }else{
            showpicture.setVisibility(View.VISIBLE);
            changepicture.setVisibility(View.GONE);
        }
        if(A==0){
            showpicture.setText("   View Profile Picture");
            changepicture.setText("   Select Profile Picture");
            Drawable viewprofile = getDrawable(R.drawable.ic_outline_account_box_24);
            viewprofile.setBounds(0,0,60,60);
            showpicture.setCompoundDrawables(viewprofile, null, null,null );
            changepicture.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_photo_size_select_actual_24,0,0,0);
            showpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                                .child(id);
                        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                intent1 = new Intent(getApplicationContext(), ViewPictureActivity.class);
                                intent1.putExtra("imgurl", user.getImageURL());
                                startActivity(intent1);
                                popupWindow.dismiss();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                }
            });
            changepicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImage();
                    popupWindow.dismiss();
                }
            });
        }else{
            showpicture.setText("   View Profile Cover");
            changepicture.setText("   Select Profile Cover");
            changepicture.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_burst_mode_24,0,0,0);
            showpicture.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_wallpaper_24,0,0,0);
            showpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                            .child(id);
                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            intent1 = new Intent(ProfileActivity.this, ViewPictureActivity.class);
                            intent1.putExtra("imgurl", user.getBackgorund_imgURL());
                            startActivity(intent1);
                            popupWindow.dismiss();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
            changepicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImage();
                    popupWindow.dismiss();
                }
            });
        }
    }
    public void showfr(String userid, final ImageView view, final TextView text){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                text.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    view.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(view);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}