package com.example.instamaterial.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private ArrayList<Fragment> fragments;
    private ArrayList<String> names;
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments=new ArrayList<>();
        names = new ArrayList<>();
    }
    public void addFragments(Fragment fragment,String name){
        fragments.add(fragment);
        names.add(name);
    }
    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return names.get(position);
    }
}
