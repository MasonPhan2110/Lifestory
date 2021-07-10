package com.minh.app.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Adapter.UserAdapter;
import com.minh.app.Model.User;
import com.minh.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> users;
    EditText search_friend;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = view.findViewById(R.id.recyle_view);
        search_friend = view.findViewById(R.id.search_friend);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        users = new ArrayList<>();
        readUsers();
        // Inflate the layout for this fragment
        return view;
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    final User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                            .child(firebaseUser.getUid())
                            .child("friend")
                            .child("acc");
                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Map<String, Object> fr =(HashMap<String, Object>)  snapshot.getValue();
                            if(fr!= null){
                                if (fr.containsKey(user.getId())){
                                    users.add(user);
                                }
                            }
                            userAdapter = new UserAdapter(getContext(),users,0, false);
                            recyclerView.setAdapter(userAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//                    if (!user.getId().equals(firebaseUser.getUid())){
//                        users.add(user);
//                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}