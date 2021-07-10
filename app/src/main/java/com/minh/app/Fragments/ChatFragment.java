package com.minh.app.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Adapter.UserAdapter;
import com.minh.app.Model.Chatlist;
import com.minh.app.Model.User;
import com.minh.app.R;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> muser;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private List<Chatlist> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat, container, false);
        // Inflate the layout for this fragment

        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chatlist chatlist = dataSnapshot.getValue(Chatlist.class);
                    userList.add(chatlist);
                }
                chatList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void chatList() {
        muser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                muser.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for (Chatlist chatlist : userList){
                        if (user.getId().equals(chatlist.getId())){
                            muser.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(),muser,0, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}