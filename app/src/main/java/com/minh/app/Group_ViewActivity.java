package com.minh.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
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

public class Group_ViewActivity extends AppCompatActivity {

    Intent intent;
    TextView name_grp,privacy_member;
    ImageView grp_backgr;
    CircleImageView profile_image;
    String GroupID;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    Button createpost,invite;
    List<Home> mPost;
    LinearLayout mylayout;
    PopupWindow popupWindow;

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
        setContentView(R.layout.activity_group__view);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        mPost = new ArrayList<>();

        name_grp = findViewById(R.id.name_grp);
        privacy_member = findViewById(R.id.privacy_member);
        grp_backgr = findViewById(R.id.grp_backgr);
        profile_image = findViewById(R.id.profile_image);
        createpost = findViewById(R.id.createpost);
        mylayout = findViewById(R.id.mylayout);
        invite = findViewById(R.id.invite);
        intent = getIntent();
        GroupID = intent.getStringExtra("GroupID");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group")
                .child(GroupID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Group group = snapshot.getValue(Group.class);
                name_grp.setText(group.getNameGroup());
                if(group.getBackGroundImg().equals("default")){
                    grp_backgr.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(group.getBackGroundImg()).into(grp_backgr);
                }
                databaseReference.child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int size = (int) snapshot.getChildrenCount();
                        privacy_member.setText(group.getPrivacy() + " - " + size + " Members");
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        createpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                intent.putExtra("PostToGrp", GroupID);
                startActivity(intent);
            }
        });
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group")
                        .child(GroupID);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Group group = snapshot.getValue(Group.class);
                        Intent intent = new Intent(getApplicationContext(), Add_MemberActivity.class);
                        intent.putExtra("from", "groupview");
                        intent.putExtra("GroupID",GroupID);
                        intent.putExtra("NameGrp", group.getNameGroup());
                        startActivity(intent);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Post");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPost.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Home home = dataSnapshot.getValue(Home.class);
                    if (home.getPostToGr().equals(GroupID)) {
                        home.setId(dataSnapshot.getKey());
                        mPost.add(home);
                    }
                }
                Collections.reverse(mPost);
                LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                for(final Home item : mPost){
                    View view = layoutInflater.inflate(R.layout.item_home,mylayout,false);
                    final String id = item.getId();
                    final TextView username1, content, like_count, comment_count,username_share, content_share, name_gr;;
                    final ImageView picture,picture_share;
                    final CircleImageView profile_image, profile_image_share;
                    final Button like, liked, comment,share;
                    MaterialCardView cardView;
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
                            intent.putExtra("userid", item.getCreater());
                            startActivity(intent);
                        }
                    });
                    profile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("userid", item.getCreater());
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

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Group")
                            .child(GroupID);
                    databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Group group = snapshot.getValue(Group.class);
                            if(group.getPrivacy().equals("Private")){
                                share.setVisibility(View.GONE);
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)comment.getLayoutParams();
                                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                comment.setLayoutParams(params); //causes layout update
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if(!item.getIsshare().equals("True")){
                        cardView.setVisibility(View.GONE);
                        picture.setVisibility(View.VISIBLE);
                    }else{
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post")
                                .child(item.getPostShare());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            menu_home_popup(item.getCreater(), v, item.getId());
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
                            liked.setVisibility(View.VISIBLE);
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
                            like.setVisibility(View.VISIBLE);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Like")
                                    .child(id)
                                    .child(fuser.getUid());
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
                    mylayout.addView(view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void menu_home_popup(String creater, View v, final String id) {
        LayoutInflater layoutInflater = (LayoutInflater) Group_ViewActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View menupopup = layoutInflater.inflate(R.layout.menu_home, null);
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int heigh = RelativeLayout.LayoutParams.WRAP_CONTENT;
        //boolean focusable = true;
        popupWindow = new PopupWindow(menupopup, width,heigh,true);
        popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
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
        edit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group_ViewActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
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
}