package com.minh.app.Adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.All_FriendsActivity;
import com.minh.app.CommentActivity;
import com.minh.app.CreatePostActivity;
import com.minh.app.EditActivity;
import com.minh.app.Edit_ProfileActivity;
import com.minh.app.Model.Group;
import com.minh.app.Model.Home;
import com.minh.app.Model.User;
import com.minh.app.ProfileActivity;
import com.minh.app.R;
import com.minh.app.ViewPictureActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mcontext;
    private List<Home> mhome;
    private FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    private String username;
    private String profile_img;
    int likecount, commentcount;
    PopupWindow popupWindow;
    public HomeAdapter (Context mcontext, List<Home> mhome){
        this.mcontext = mcontext;
        this.mhome = mhome;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1){
            View view = LayoutInflater.from(mcontext).inflate(R.layout.header_home, parent, false);
            return new HeaderViewHolder(view);
        }else{
            View view = LayoutInflater.from(mcontext).inflate(R.layout.item_home, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder){
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if(user.getImageURL().equals("default")){
                        headerViewHolder.profile_img.setImageResource(R.mipmap.ic_launcher);
                    }else{
                        Glide.with(mcontext).load(user.getImageURL()).into(headerViewHolder.profile_img);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            headerViewHolder.profile_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mcontext,ProfileActivity.class);
                    mcontext.startActivity(intent);
                }
            });
            headerViewHolder.CreatePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mcontext, CreatePostActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mcontext.startActivity(intent);
                }
            });
        }
        else if(holder instanceof  ViewHolder){
            final ViewHolder itemholder = (ViewHolder) holder;
            final Home home = mhome.get(position-1);
            final String homeid = home.getId();
            final String img = home.getImg();
            holder.setIsRecyclable(false);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(home.getCreater());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if(user.getOnline().equals("True")){
                        itemholder.on.setVisibility(View.VISIBLE);
                    }else{
                        itemholder.off.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Like")
                    .child(home.getId());
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, Object> like = (HashMap<String, Object>) snapshot.getValue();
                    likecount = (int) snapshot.getChildrenCount();

                    if (like != null) {
                        if (like.containsKey(fuser.getUid())) {
                            itemholder.liked.setVisibility(View.VISIBLE);
                            itemholder.like.setVisibility(View.GONE);
                        }
                    } else {
                        itemholder.liked.setVisibility(View.GONE);
                        itemholder.like.setVisibility(View.VISIBLE);
                        itemholder.like_count.setText("");
                    }
                    if (likecount == 0) {
                        itemholder.like_count.setVisibility(View.VISIBLE);
                    } else if (likecount == 1) {
                        itemholder.like_count.setText(likecount + " like");
                    } else {
                        itemholder.like_count.setText(likecount + " likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (home.getType() != null) {
                if (home.getType().equals("changeProfile")) {
                    itemholder.text_change.setVisibility(View.VISIBLE);
                    itemholder.text_change.setText(" đã thay đổi ảnh đại diện");
                    itemholder.content.setVisibility(View.GONE);
                } else if (home.getType().equals("changeBackground")) {
                    itemholder.text_change.setText(" đã thay đổi ảnh background");
                    itemholder.content.setVisibility(View.GONE);
                }
            }
            if (!home.getPostToGr().equals("default")) {
                itemholder.name_gr.setVisibility(View.VISIBLE);
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group")
                        .child(home.getPostToGr());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Group grp = snapshot.getValue(Group.class);
                        itemholder.name_gr.setText(grp.getNameGroup());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else {
                itemholder.name_gr.setVisibility(View.GONE);
            }
            if (!home.getIsshare().equals("True")) {
                itemholder.cardView.setVisibility(View.GONE);
                itemholder.picture.setVisibility(View.VISIBLE);
            } else {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post")
                        .child(home.getPostShare());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final Home home_shared = snapshot.getValue(Home.class);
                        final String img_share = home_shared.getImg();
                        itemholder.username_share.setText(home_shared.getUsername());
                        itemholder.content_share.setText(home_shared.getContent());
                        if (home_shared.getType() != null) {
                            if (home_shared.getType().equals("changeProfile")) {
                                itemholder.text_change_share.setVisibility(View.VISIBLE);
                                itemholder.text_change_share.setText(" đã thay đổi ảnh đại diện");
                                itemholder.content_share.setVisibility(View.GONE);
                            } else if (home_shared.getType().equals("changeBackground")) {
                                itemholder.text_change_share.setText(" đã thay đổi ảnh background");
                                itemholder.content_share.setVisibility(View.GONE);
                            }
                        }
                        if (home_shared.getImageURL().equals("default")) {
                            itemholder.profile_image_share.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(mcontext).load(home_shared.getImageURL()).into(itemholder.profile_image_share);
                        }
                        if (home_shared.getImg().equals("default")) {
                            itemholder.picture_share.setVisibility(View.GONE);
                        } else {
                            Glide.with(mcontext).load(home_shared.getImg()).into(itemholder.picture_share);
                            itemholder.picture_share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mcontext, ViewPictureActivity.class);
                                    intent.putExtra("imgurl", img_share);
                                    mcontext.startActivity(intent);
                                }
                            });
                        }
                        itemholder.username_share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mcontext, ProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("userid", home_shared.getCreater());
                                mcontext.startActivity(intent);
                            }
                        });
                        itemholder.profile_image_share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mcontext, ProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("userid", home_shared.getCreater());
                                mcontext.startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comment")
                    .child(home.getId());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, Object> comment = (HashMap<String, Object>) snapshot.getValue();
                    commentcount = (int) snapshot.getChildrenCount();
                    if (commentcount == 0) {
                        itemholder.comment_count.setText("");
                    } else if (commentcount == 1) {
                        itemholder.comment_count.setText("1 comment");
                    } else {
                        itemholder.comment_count.setText(commentcount + " comments");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            itemholder.username.setText(home.getUsername());
            if (home.getImageURL().equals("default")) {
                itemholder.profile_image.setImageResource(R.mipmap.ic_launcher);
            } else {
                Glide.with(mcontext).load(home.getImageURL()).into(itemholder.profile_image);
            }
            itemholder.content.setText(home.getContent());
            if (home.getImg().equals("default")) {
                itemholder.picture.setVisibility(View.GONE);
            } else {
                Glide.with(mcontext).load(home.getImg()).into(itemholder.picture);
                itemholder.picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(mcontext, ViewPictureActivity.class);
                        intent1.putExtra("imgurl", img);
                        mcontext.startActivity(intent1);
                    }
                });
            }

            itemholder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemholder.liked.setVisibility(View.VISIBLE);
                    itemholder.like.setVisibility(View.GONE);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Like")
                            .child(home.getId());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(fuser.getUid(), "Like");
                    reference.updateChildren(hashMap);
                    if (!fuser.getUid().equals(home.getCreater())) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Noti")
                                .child(home.getCreater());
                        String key = databaseReference.push().getKey();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("ID", key);
                        map.put("Sent", fuser.getUid());
                        map.put("Post", home.getId());
                        map.put("Type", "Like");
                        map.put("IsRead", "False");
                        databaseReference.child(key).updateChildren(map);
                    }
                }
            });
            itemholder.liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemholder.like.setVisibility(View.VISIBLE);
                    itemholder.liked.setVisibility(View.GONE);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Like")
                            .child(homeid)
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
            itemholder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mcontext, CommentActivity.class);
                    intent.putExtra("homeid", homeid);
                    mcontext.startActivity(intent);
                }
            });
            itemholder.view_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mcontext, CommentActivity.class);
                    intent.putExtra("homeid", homeid);
                    mcontext.startActivity(intent);
                }
            });
            itemholder.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mcontext, ProfileActivity.class);
                    if (!home.getCreater().equals(fuser.getUid())) {
                        intent.putExtra("userid", home.getCreater());
                    }
                    mcontext.startActivity(intent);
                }
            });
            itemholder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mcontext, ProfileActivity.class);
                    if (!home.getCreater().equals(fuser.getUid())) {
                        intent.putExtra("userid", home.getCreater());
                    }
                    mcontext.startActivity(intent);
                }
            });
            itemholder.menu_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu_home_popup(home.getCreater(), v, home.getId());
                }
            });
        }
    }

    private void menu_home_popup(String creater, View v,final String id) {
        LayoutInflater layoutInflater = (LayoutInflater) mcontext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View menupopup = layoutInflater.inflate(R.layout.menu_home, null);
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int heigh = RelativeLayout.LayoutParams.WRAP_CONTENT;
        //boolean focusable = true;
        popupWindow = new PopupWindow(menupopup, width,heigh,true);
        popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
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
                Intent intent = new Intent(mcontext, EditActivity.class);
                mcontext.startActivity(intent);
            }
        });
        save_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Save Post").child(fuser.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put(id, "Post");
                databaseReference.updateChildren(map);
                popupWindow.dismiss();
                Toast.makeText(mcontext, "Save", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return mhome.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, content, like_count, comment_count,username_share, content_share, name_gr,text_change,text_change_share;
        public ImageView picture,picture_share;
        public CircleImageView profile_image,profile_image_share, on, off;
        public Button like, liked, comment;
        public RelativeLayout view_like;
        public CardView cardView;
        public ImageButton menu_home;
        //private ImageView img_on;
        //private ImageView img_off;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            on = itemView.findViewById(R.id.on);
            off = itemView.findViewById(R.id.off);
            cardView = itemView.findViewById(R.id.card_view);
            view_like = itemView.findViewById(R.id.view_like);
            comment_count = itemView.findViewById(R.id.comment_count);
            like_count= itemView.findViewById(R.id.like_count);
            comment = itemView.findViewById(R.id.comment);
            like = itemView.findViewById(R.id.like);
            liked = itemView.findViewById(R.id.liked);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            picture = itemView.findViewById(R.id.picture);
            content = itemView.findViewById(R.id.content);
            username_share= itemView.findViewById(R.id.username_share);
            content_share = itemView.findViewById(R.id.content_share);
            profile_image_share = itemView.findViewById(R.id.profile_image_share);
            picture_share = itemView.findViewById(R.id.picture_share);
            text_change = itemView.findViewById(R.id.text_change);
            text_change_share = itemView.findViewById(R.id.text_change_share);
            name_gr = itemView.findViewById(R.id.name_gr);
            menu_home = itemView.findViewById(R.id.menu_home);
        }
    }
    private class viewType{
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return viewType.Header;
        }
        return viewType.Normal;
    }
    private class HeaderViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profile_img;
        Button CreatePost;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_img = itemView.findViewById(R.id.profile_image);
            CreatePost = itemView.findViewById(R.id.createpost);
        }
    }
}
