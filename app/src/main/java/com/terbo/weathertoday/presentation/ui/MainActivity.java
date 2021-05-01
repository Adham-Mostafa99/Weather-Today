package com.terbo.weathertoday.presentation.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.terbo.weathertoday.presentation.adapter.FragmentAdapter;
import com.terbo.weathertoday.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    private FragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        fragmentAdapter.addFragment(new CustomWeather(), "Custom");
        fragmentAdapter.addFragment(new CurrentCityWeather(), "Your Location");

        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    ((CustomWeather) fragmentAdapter.getItem(0)).create();
                else if (position == 1)
                    ((CurrentCityWeather) fragmentAdapter.getItem(1)).create();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}