package com.app.paul.newsapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;

import com.app.paul.newsapp.adapter.AdapterFragmentPager;
import com.app.paul.newsapp.fragments.FragmentBase;

import static com.app.paul.newsapp.adapter.AdapterFragmentPager.PATH_TO_NEWS;

public class MainActivity extends AppCompatActivity {
    FrameLayout container;
    FragmentBase fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ViewPager mViewPagerTour = findViewById(R.id.view_pager);
        AdapterFragmentPager adapterFragmentPager = new AdapterFragmentPager(getSupportFragmentManager());
        mViewPagerTour.setAdapter(adapterFragmentPager);

        TabLayout mTabLayoutHost = findViewById(R.id.tablayout_host);
        mTabLayoutHost.setTabTextColors(ColorStateList.valueOf(Color.parseColor("white")));
        mTabLayoutHost.setupWithViewPager(mViewPagerTour);
        mTabLayoutHost.setTabGravity(TabLayout.GRAVITY_CENTER);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                return true;
            }
        });
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                fragment.load(fragment.getCont());
                fragment.setSearching(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                fragment.buildString(s);
                fragment.load(fragment.getCont());
                fragment.increaseCont();
                fragment.setSearching(true);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                putSearchFragment();
                break;
        }
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    public void putSearchFragment(){

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentBase();
        Bundle bundle;
        bundle = new Bundle();
        bundle.putString(PATH_TO_NEWS, "");
        fragment.setArguments(bundle);
        transaction.replace(R.id.frmae_container, fragment);
        transaction.commit();
    }
}
