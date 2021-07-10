package com.minh.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.keyframes.KeyframesDrawable;
import com.facebook.keyframes.KeyframesDrawableBuilder;
import com.facebook.keyframes.deserializers.KFImageDeserializer;
import com.facebook.keyframes.model.KFImage;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.app.Fragments.ChatFragment;
import com.minh.app.Fragments.FriendsFragment;
import com.minh.app.Fragments.GroupFragment;
import com.minh.app.Fragments.HomeFragment;
import com.minh.app.Fragments.MenuFragment;
import com.minh.app.Fragments.NotiFragment;
import com.minh.app.Model.Chat;
import com.minh.app.Model.Noti;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int[] tabIcons ={
            R.drawable.home,
            R.drawable.notification,
            R.drawable.group,
            R.drawable.chat,
            R.drawable.friend,
            R.drawable.menu
    };
    private TabLayout tabLayout;
    ImageButton search_btn;
    private ViewPager viewPager;
    AppBarLayout appBarLayout;
    DatabaseReference reference;
    Window window;
    public  int A=1;
    FirebaseUser fuser;
    MainActivityListener mlistener, mainActivityListener;
    public int B=0;
    RelativeLayout relativelayout;
    List<Noti> mNoti;
    public MainActivity(){

    }

    @Override
    public void onBackPressed() {

        if (tabLayout.getTabAt(0).isSelected()){
            backpress();
        }else {
//            setupViewPager(viewPager);
//            tabLayout.setupWithViewPager(viewPager);
//            setupTabIcons();
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativelayout = findViewById(R.id.relativelayout);
        search_btn = findViewById(R.id.search_btn);
        tabLayout =(TabLayout) findViewById(R.id.tab_layout);
        viewPager =(ViewPager) findViewById(R.id.view_pager);
        final Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.Appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mNoti = new ArrayList<>();
        window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Noti").child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mNoti.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Noti noti = dataSnapshot.getValue(Noti.class);
                    if(noti.getIsRead().equals("False")){
                        mNoti.add(noti);
                    }
                }
                Log.e("AAA","Noti"+ String.valueOf(mNoti.size()));
                if(mNoti.size() != 0){
                    tabIcons[1]=R.drawable.unread;
                }
                setupTabIcons();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(new BlendModeColorFilter(Color.parseColor("#4267B2"), BlendMode.SRC_IN));
                if(tab.getPosition() != 0){
                    Transition transition = new Fade();
                    transition.setDuration(80);
                    transition.addTarget(toolbar);
                    TransitionManager.beginDelayedTransition(appBarLayout,transition);
                    toolbar.setVisibility(View.GONE);
                }
                if(tab.getPosition() == 0){
                    Transition transition = new Fade();
                    transition.setDuration(80);
                    transition.addTarget(toolbar);
                    TransitionManager.beginDelayedTransition(appBarLayout,transition);
                    toolbar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(new BlendModeColorFilter(Color.rgb(230,230,230), BlendMode.SRC_IN));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private  void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new NotiFragment(), "Noti");
        adapter.addFragment(new GroupFragment(),"Group");
        adapter.addFragment(new ChatFragment(), "Chat");
        adapter.addFragment(new FriendsFragment(), "Friends");
        adapter.addFragment(new MenuFragment(), "Menu");
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        Log.e("AAA","tab 1 "+tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
        tabLayout.getTabAt(5).setIcon(tabIcons[5]);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        private ArrayList<Drawable> drawables;

        ViewPagerAdapter(FragmentManager fm){
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }
        @NonNull
        @Override
        public CharSequence getPageTitle(int position){
            return null;
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if(fragment instanceof MainActivityListener){
            mlistener = (MainActivityListener) fragment;
            mainActivityListener = (MainActivityListener) fragment;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mlistener = null;
        mainActivityListener = null;
    }

    @Override
    protected void onPostResume() {
        if(B==0){
            super.onPostResume();
            B++;
        }else{
            super.onPostResume();
            changeprofileimg();
        }
    }
    void changeprofileimg(){
        mlistener.changeprofileimg();
    }
    public interface MainActivityListener{
        void changeprofileimg();
        void backpress();
    }
    void backpress(){
        mainActivityListener.backpress();
    }
    
}
