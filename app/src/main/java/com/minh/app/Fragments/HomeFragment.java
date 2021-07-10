package com.minh.app.Fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.minh.app.Adapter.HomeAdapter;
import com.minh.app.Class.EndlessRecyclerViewScrollListener;
import com.minh.app.CommentActivity;
import com.minh.app.CreatePostActivity;
import com.minh.app.EditActivity;
import com.minh.app.MainActivity;
import com.minh.app.Model.Group;
import com.minh.app.Model.Home;
import com.minh.app.Model.User;
import com.minh.app.ProfileActivity;
import com.minh.app.R;
import com.minh.app.ViewPictureActivity;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeFragment extends Fragment implements MainActivity.MainActivityListener {
    FirebaseUser fuser;
    List<Home> mhome;
    ArrayList<String> MypostID;
    LinearLayout mylayout;
    static String user_img;
    static String user_name;
    Calendar cal;
    String currenttime;
    SwipeRefreshLayout refresh;
    HomeAdapter homeAdapter;
    RecyclerView recycle_view;
    LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mylayout = (LinearLayout) view.findViewById(R.id.mylayout);
        mhome = new ArrayList<>();
        MypostID = new ArrayList<>();
        refresh = view.findViewById(R.id.refresh);
        recycle_view = view.findViewById(R.id.recycle_view);
        recycle_view.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recycle_view.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        readHome();
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        Date currentLocaltime = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        currenttime = dateFormat.format(currentLocaltime);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mhome.clear();
                recycle_view.getAdapter().notifyDataSetChanged();
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Post");
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Home home = dataSnapshot.getValue(Home.class);
                            home.setId(dataSnapshot.getKey());
                            mhome.add(home);
                        }
                        Collections.reverse(mhome);
                        recycle_view.getAdapter().notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                refresh.setRefreshing(false);
            }
        });
        return view;
    }
    private void loadNextDataFromApi(final int page) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Home> homeList = new ArrayList<>();
                homeList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Home home = dataSnapshot.getValue(Home.class);
                    home.setId(dataSnapshot.getKey());
                    homeList.add(home);
                }
                Collections.reverse(homeList);
                for(int i =0;i<homeList.size();i++){
                    mhome.add(homeList.get(i));
                }
                homeAdapter.notifyDataSetChanged();
                Log.e("CCC", String.valueOf(page));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void readHome() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Post");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mhome.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Home home = dataSnapshot.getValue(Home.class);
                    home.setId(dataSnapshot.getKey());
                    mhome.add(home);
                }
                Collections.reverse(mhome);
                homeAdapter = new HomeAdapter(getContext(),mhome);
                recycle_view.setAdapter(homeAdapter);
                recycle_view.addOnScrollListener(scrollListener);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    public void changeprofileimg() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users")
                .child(fuser.getUid());
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Home home = dataSnapshot.getValue(Home.class);
                            if(home.getCreater().equals(fuser.getUid())){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("imageURL", user.getImageURL());
                                databaseReference.child(dataSnapshot.getKey()).updateChildren(hashMap);
                            }
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
    @Override
    public void backpress() {
        if(linearLayoutManager.findFirstVisibleItemPosition() != 0)
        {
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()){
                @Override
                protected int getVerticalSnapPreference() {
                    return super.getVerticalSnapPreference();
                }
            };
            smoothScroller.setTargetPosition(0);
            linearLayoutManager.startSmoothScroll(smoothScroller);
        }else{
            getActivity().finishAffinity();
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public static class Helper {
        public static boolean isAppRunning(final Context context, final String packageName) {
            final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
            if (procInfos != null)
            {
                for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                    if (processInfo.processName.equals(packageName)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online","True");
        reference.updateChildren(hashMap);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online","False");
        reference.updateChildren(hashMap);
    }

}