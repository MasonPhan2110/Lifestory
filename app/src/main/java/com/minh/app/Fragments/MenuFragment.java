package com.minh.app.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.minh.app.Friend_RequestActivity;
import com.minh.app.LocationActivity;
import com.minh.app.Model.User;
import com.minh.app.ProfileActivity;
import com.minh.app.R;
import com.minh.app.Save_PostActivity;
import com.minh.app.StartActivity;

import de.hdodenhof.circleimageview.CircleImageView;


public class MenuFragment extends Fragment {
    CircleImageView profile_image;
    TextView username;
    DatabaseReference reference;
    Button btn_logout;
    FirebaseUser firebaseUser;
    CardView cardView, fr,grp,video,page,event,job,save,market,mem,date,game,nearby;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        profile_image = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        btn_logout = view.findViewById(R.id.btn_logout);
        cardView = view.findViewById(R.id.card_view);
        fr = view.findViewById(R.id.fr);
        grp = view.findViewById(R.id.grp);
        video = view.findViewById(R.id.video);
        page = view.findViewById(R.id.page);
        event =  view.findViewById(R.id.event);
        job = view.findViewById(R.id.job);
        save= view.findViewById(R.id.save);
        market = view.findViewById(R.id.market);
        mem= view.findViewById(R.id.mem);
        date = view.findViewById(R.id.date);
        game = view.findViewById(R.id.game);
        nearby = view.findViewById(R.id.nearby);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Save_PostActivity.class);
                startActivity(intent);

            }
        });
        fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Friend_RequestActivity.class);
                startActivity((intent));
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded()){
                    User user = snapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    if(user.getImageURL().equals("default")){
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    }else
                    {
                        Glide.with(getContext()).load(user.getImageURL()).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("userid", firebaseUser.getUid());
                startActivity(intent);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationActivity.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}