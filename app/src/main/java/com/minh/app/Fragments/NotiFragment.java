package com.minh.app.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Group_ViewActivity;
import com.minh.app.Group_for_NewActivity;
import com.minh.app.Model.Home;
import com.minh.app.Model.Noti;
import com.minh.app.Model.User;
import com.minh.app.R;
import com.minh.app.viewPostActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotiFragment extends Fragment {
    LinearLayout my_layout;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    List<Noti> mNoti;
    SwipeRefreshLayout refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_noti, container, false);
        my_layout = view.findViewById(R.id.my_layout);
        refresh = view.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                my_layout.removeViews(2, my_layout.getChildCount()-2);
                readNoti();
                refresh.setRefreshing(false);
            }
        });
        mNoti = new ArrayList<>();
        readNoti();
        return view;
    }

    private void readNoti() {
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Noti")
                .child(fuser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mNoti.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Noti noti = dataSnapshot.getValue(Noti.class);
                    mNoti.add(noti);
                }
                Collections.reverse(mNoti);
                for(final Noti item : mNoti){
                    final CircleImageView post_profile;
                    final TextView noti;
                    final String type,namegrp;
                    final View view1 = layoutInflater.inflate(R.layout.item_noti,my_layout,false);
                    post_profile = view1.findViewById(R.id.post_profile);
                    noti = view1.findViewById(R.id.noti);
                    type = item.getType();
                    if(type.equals("InviteGrp")){
                        namegrp = item.getNameGrp();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(item.getSent());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final User user = snapshot.getValue(User.class);
                                if(user.getImageURL().equals("default")){
                                    post_profile.setImageResource(R.mipmap.ic_launcher);
                                }else{
                                    Glide.with(getContext()).load(user.getImageURL()).into(post_profile);
                                }
                                noti.setText(user.getUsername() + " đã mời bạn vào nhóm " +namegrp);
                                if(item.getIsRead().equals("False")){
                                    noti.setTypeface(null, Typeface.BOLD);
                                    view1.setBackgroundColor(Color.parseColor("#F0F8FF"));
                                }else{
                                    noti.setTypeface(null, Typeface.NORMAL);
                                    view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                }
                                view1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        noti.setTypeface(null, Typeface.NORMAL);
                                        view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Noti")
                                                .child(fuser.getUid())
                                                .child(item.getID());
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("IsRead", "True");
                                        databaseReference1.updateChildren(hashMap);
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Group")
                                                .child(item.getGroup())
                                                .child("Member");
                                        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                HashMap<String, Object> hashMap1 = (HashMap<String, Object>) snapshot.getValue();
                                                if(hashMap1.containsKey(fuser.getUid())){
                                                    Intent intent = new Intent(getContext(), Group_ViewActivity.class);
                                                    intent.putExtra("GroupID",item.getGroup());
                                                    startActivity(intent);
                                                }else{
                                                    Intent intent = new Intent(getContext(), Group_for_NewActivity.class);
                                                    intent.putExtra("GroupID",item.getGroup());
                                                    intent.putExtra("user_sent", user.getId());
                                                    intent.putExtra("IDNoti", item.getID());
                                                    if(item.getReject().equals("True")){
                                                        intent.putExtra("Reject","True");
                                                    }else{
                                                        intent.putExtra("Reject","False");
                                                    }
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                                my_layout.addView(view1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else if(type.equals("Like")){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(item.getSent());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if(user.getImageURL().equals("default")){
                                    post_profile.setImageResource(R.mipmap.ic_launcher);
                                }else{
                                    Glide.with(getContext()).load(user.getImageURL()).into(post_profile);
                                }
                                noti.setText(user.getUsername() + " đã thích bài viết của bạn");
                                if(item.getIsRead().equals("False")){
                                    noti.setTypeface(null, Typeface.BOLD);
                                    view1.setBackgroundColor(Color.parseColor("#F0F8FF"));
                                }else{
                                    noti.setTypeface(null, Typeface.NORMAL);
                                    view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                }
                                view1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        noti.setTypeface(null, Typeface.NORMAL);
                                        view1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Noti")
                                                .child(fuser.getUid())
                                                .child(item.getID());
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("IsRead", "True");
                                        databaseReference1.updateChildren(hashMap);
                                        Intent intent = new Intent(getContext(), viewPostActivity.class);
                                        intent.putExtra("idPost",item.getPost());
                                        startActivity(intent);
                                    }
                                });
                                my_layout.addView(view1);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else if(type.equals("Share")){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}