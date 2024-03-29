package com.minh.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Model.Group;
import com.minh.app.Model.Home;
import com.minh.app.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Group_for_NewActivity extends AppCompatActivity {
    Intent intent;
    String GroupID;
    String UserID, IDNoti,Reject;
    ImageView backgr;
    TextView name_grp,privacy_member,text1,text2,text;
    CircleImageView profile_img;
    LinearLayout mylayout;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    List<Home> mHome;
    Button join,delete;
    CardView request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_for__new);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        intent = getIntent();
        GroupID = intent.getStringExtra("GroupID");
        UserID = intent.getStringExtra("user_sent");
        IDNoti = intent.getStringExtra("IDNoti");
        Reject = intent.getStringExtra("Reject");

        name_grp = findViewById(R.id.name_grp);
        privacy_member = findViewById(R.id.privacy_member);
        profile_img = findViewById(R.id.profile_image);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        backgr = findViewById(R.id.grp_backgr);
        mylayout = findViewById(R.id.mylayout);
        text = findViewById(R.id.text);
        join = findViewById(R.id.join);
        delete = findViewById(R.id.delete);
        request = findViewById(R.id.request);
        mHome = new ArrayList<>();

        if(Reject.equals("True")){
            request.setVisibility(View.GONE);
        }
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group")
                        .child(GroupID)
                        .child("Member");
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put(firebaseUser.getUid(),"Member");
                databaseReference.updateChildren(hashMap);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.setVisibility(View.GONE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Noti")
                        .child(firebaseUser.getUid())
                        .child(IDNoti);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Reject", "True");
                databaseReference.updateChildren(hashMap);
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(UserID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageURL().equals("default")){
                    profile_img.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_img);
                }
                text1.setText(user.getUsername()+" đã mời bạn tham gia nhóm");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group")
                .child(GroupID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Group group = snapshot.getValue(Group.class);
                name_grp.setText(group.getNameGroup());
                if(group.getBackGroundImg().equals("default")){
                    backgr.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(group.getBackGroundImg()).into(backgr);
                }
                databaseReference.child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int size = (int) snapshot.getChildrenCount();
                        privacy_member.setText(group.getPrivacy() + " - " + size + " Members");
                        if(group.getPrivacy().equals("Public")){
                            readingpostingrp();
                        }else {
                            text.setVisibility(View.GONE);
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

    private void readingpostingrp() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHome.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Home home = dataSnapshot.getValue(Home.class);
                    if(home.getPostToGr().equals(GroupID)){
                        home.setId(dataSnapshot.getKey());
                        mHome.add(home);
                    }
                }
                Collections.reverse(mHome);
                LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                for(final Home item : mHome){
                    final View view = layoutInflater.inflate(R.layout.item_home,mylayout,false);
                    final String id = item.getId();
                    final TextView username1, content, like_count, comment_count,username_share, content_share, name_gr;
                    final ImageView picture,picture_share;
                    final CircleImageView profile_image, profile_image_share;
                    final Button like, liked, comment, share;
                    final String Creater = item.getCreater();
                    CardView cardView;
                    ImageButton menu_home;
                    RelativeLayout view_like;
                    name_gr = view.findViewById(R.id.name_gr);
                    menu_home = view.findViewById(R.id.menu_home);
                    cardView = view.findViewById(R.id.card_view);
                    username_share= view.findViewById(R.id.username_share);
                    content_share = view.findViewById(R.id.content_share);
                    profile_image_share = view.findViewById(R.id.profile_image_share);
                    picture_share = view.findViewById(R.id.picture_share);
                    share = view.findViewById(R.id.share);
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

                    username1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("userid", Creater);
                            startActivity(intent);
                        }
                    });
                    profile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("userid", Creater);
                            startActivity(intent);
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
                    if(!item.getIsshare().equals("True")){
                        cardView.setVisibility(View.GONE);
                        picture.setVisibility(View.VISIBLE);
                    }else{
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Post")
                                .child(item.getPostShare());
                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final Home home_shared = snapshot.getValue(Home.class);
                                final String img_share = home_shared.getImg();
                                username_share.setText(home_shared.getUsername());
                                content_share.setText(home_shared.getContent());
                                if(home_shared.getImageURL().equals("default")){
                                    profile_image_share.setImageResource(R.mipmap.ic_launcher);
                                }else{
                                    Glide.with(getApplicationContext()).load(home_shared.getImageURL()).into(profile_image_share);
                                }
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
                                username_share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("userid", home_shared.getCreater());
                                        startActivity(intent);
                                    }
                                });
                                profile_image_share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("userid", home_shared.getCreater());
                                        startActivity(intent);
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    if(item.getContent().equals("default")){
                        content.setVisibility(View.VISIBLE);
                    }else {
                        content.setText(item.getContent());
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
                    menu_home.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //menu_home_popup(item.getCreater(), v, item.getId());
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
                                if(liker.containsKey(firebaseUser.getUid())){
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
//                    share.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//                            View menupopup = layoutInflater.inflate(R.layout.sharepost, null);
//                            int width = RelativeLayout.LayoutParams.MATCH_PARENT;
//                            int heigh = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                            //boolean focusable = true;
//                            popupWindow = new PopupWindow(menupopup, width,heigh,true);
//                            popupWindow.showAtLocation(v, Gravity.START,0,0);
//
//                            View container = (View) popupWindow.getContentView().getParent();
//                            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//                            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
//                            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//                            p.dimAmount = 0.3f;
//                            wm.updateViewLayout(container, p);
//                            CircleImageView profile_img;
//                            final EditText post;
//                            Button sharenow;
//                            TextView username;
//                            profile_img = menupopup.findViewById(R.id.profile_img);
//                            post = menupopup.findViewById(R.id.post);
//                            username = menupopup.findViewById(R.id.username);
//                            sharenow = menupopup.findViewById(R.id.share);
//                            if (user_img.equals("default")){
//                                profile_img.setImageResource(R.mipmap.ic_launcher);
//                            }else{
//                                Glide.with(getContext()).load(user_img).into(profile_img);
//                            }
//                            username.setText(user_name);
//                            sharenow.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    sharepost(firebaseUser.getUid(), id,user_name, post.getText().toString(), user_img, item.getPostShare());
//                                    if(!firebaseUser.getUid().equals(item.getCreater())) {
//                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Noti")
//                                                .child(item.getCreater());
//                                        String key = databaseReference.push().getKey();
//                                        HashMap<String, Object> map = new HashMap<>();
//                                        map.put("ID",key);
//                                        map.put("Sent", firebaseUser.getUid());
//                                        map.put("Post", item.getId());
//                                        map.put("Type","Share");
//                                        map.put("IsRead","False");
//                                        databaseReference.child(key).updateChildren(map);
//                                    }
//                                    popupWindow.dismiss();
//                                }
//                            });
//                        }
//                    });
                    like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            liked.setVisibility(View.VISIBLE);
                            like.setVisibility(View.GONE);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Like")
                                    .child(id);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(firebaseUser.getUid(), "Like");
                            reference.updateChildren(hashMap);
                            if(!firebaseUser.getUid().equals(item.getCreater())) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Noti")
                                        .child(item.getCreater());
                                String key = databaseReference.push().getKey();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("ID",key);
                                map.put("Sent", firebaseUser.getUid());
                                map.put("Post", item.getId());
                                map.put("Type","Like");
                                map.put("IsRead","False");
                                databaseReference.child(key).updateChildren(map);
                            }
                        }
                    });
                    liked.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Like")
                                    .child(id)
                                    .child(firebaseUser.getUid());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().removeValue();
                                    like.setVisibility(View.VISIBLE);
                                    liked.setVisibility(View.GONE);
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
                    Log.e("BBB", item.getPostToGr());
                    mylayout.addView(view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}