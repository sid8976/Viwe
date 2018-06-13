package com.sid.viwe;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class mainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SelectionPagerAdapter mSelectionPagerAdapter;
    private TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mViewPager = (ViewPager) findViewById(R.id.chat_tab_pager);

        mToolbar=(Toolbar) findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(mToolbar);


        mAuth=FirebaseAuth.getInstance();

        mSelectionPagerAdapter=new SelectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSelectionPagerAdapter);
        mTabLayout=(TabLayout) findViewById(R.id.chat_page_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(Color.parseColor("#000000"),Color.parseColor("#ffffff"));
        getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            sendtohome();
        }
    }

    private void sendtohome()
    {
        Intent i = new Intent(mainActivity.this,homeActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);


        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.chat_page_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         switch (item.getItemId())
         {
             case R.id.chat_page_menu_logout:
                 FirebaseAuth.getInstance().signOut();
                 sendtohome();
                 break;
             case R.id.chat_page_acc_settings:
                 Intent settings = new Intent(mainActivity.this,settingsActivity.class);
                 startActivity(settings);
                 break;
             case R.id.chat_page_new_contact:
                 Intent allusers = new Intent(mainActivity.this,UsersActivity.class);
                 startActivity(allusers);
             case R.id.refresh:
                 recreate();
                 break;
         }
         return true;
         }
    }

