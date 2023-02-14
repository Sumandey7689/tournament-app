package com.omsidh.huntsmanwar.activity;


import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.fragment.CompleteFragment;
import com.omsidh.huntsmanwar.fragment.OngoingFragment;
import com.omsidh.huntsmanwar.fragment.UpcomingFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyMatchesActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;

    private Bundle bundle;
    private String strId,strTitle,strURL;

    public MyMatchesActivity() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_matches);

        initBundel();
        initToolbar();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);

        OngoingFragment ongoingFragment = new OngoingFragment();
        bundle.putString("ID_KEY",strId);
        bundle.putString("TITLE_KEY",strTitle);
        bundle.putString("URL_KEY",strURL);
        ongoingFragment.setArguments(bundle);

        UpcomingFragment upcomingFragment = new UpcomingFragment();
        bundle.putString("ID_KEY",strId);
        bundle.putString("TITLE_KEY",strTitle);
        bundle.putString("URL_KEY",strURL);
        upcomingFragment.setArguments(bundle);

        CompleteFragment completeFragment = new CompleteFragment();
        bundle.putString("ID_KEY",strId);
        bundle.putString("TITLE_KEY",strTitle);
        bundle.putString("URL_KEY",strURL);
        completeFragment.setArguments(bundle);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(ongoingFragment,"ONGOING");
        pagerAdapter.addFragment(upcomingFragment,"UPCOMING");
        pagerAdapter.addFragment(completeFragment,"COMPLETED");

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);

        /*for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            tabLayout.getTabAt(0).setText("ONGOING");
            tabLayout.getTabAt(1).setText("UPCOMING");
            tabLayout.getTabAt(2).setText("COMPLETED");
        }
*/
    }

    private void initBundel() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            strId = bundle.getString("ID_KEY");
            strTitle = bundle.getString("TITLE_KEY");
            strURL = bundle.getString("URL_KEY");
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My Matches");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
