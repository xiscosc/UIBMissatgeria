package com.fsc.uibmissatgeria.ui.activities;

import java.util.Locale;

import android.content.Intent;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.ui.fragments.SubjectsFragment;
import com.fsc.uibmissatgeria.ui.fragments.ConversationsFragment;
import com.fsc.uibmissatgeria.ui.fragments.PlaceholderFragment;
import com.fsc.uibmissatgeria.ui.widgets.SlidingTabLayout;


public class PrincipalActivity extends AppCompatActivity {


    private SectionsPagerAdapter adapter;
    private ViewPager pager;
    public AccountUIB accountUIB;
    private Toolbar toolbar;
    private SlidingTabLayout tabs;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        accountUIB = new AccountUIB(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        adapter =  new SectionsPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }

        });

        tabs.setViewPager(pager);

        Intent i = getIntent();
        Boolean fromNotification = i.getBooleanExtra(Constants.NOTIFICATION_CONVERSATIONS_INTENT, false);
        if (fromNotification) {
            pager.setCurrentItem(1);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        this.menu = menu;
        Avatar avatar = accountUIB.getUser().getAvatar();
        if (avatar != null && avatar.hasFile()) {
            MenuItem avatarMenu = menu.getItem(0);
            avatarMenu.setIcon(avatar.getCircleBitmap(this));

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_edit_profille) {
            startProfileEdit();
        }
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void startProfileEdit() {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!accountUIB.isLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.finish();
            startActivity(intent);
        } else if (menu!=null){
            Avatar avatar = accountUIB.getUser().getAvatar();
            if (avatar != null && avatar.hasFile()) {
                MenuItem avatarMenu = menu.getItem(0);
                avatarMenu.setIcon(avatar.getCircleBitmap(this));
            }
        }
    }

    public void loadSettings(MenuItem item) {
        Intent i = new Intent(getApplicationContext(), OptionsActivity.class);
        startActivityForResult(i, 1);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0: return new SubjectsFragment();

                case 1: return new ConversationsFragment();

                default: return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }
}
