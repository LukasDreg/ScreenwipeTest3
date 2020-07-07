package com.example.screenwipetest3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {



    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //Set up for the Fragments
        DemoFragment demoFragment = new DemoFragment();
        DemoFragment1 demoFragment1 = new DemoFragment1();

        //return the Fragment for the position in the Pager
        if(position == 0)
        {
            return demoFragment;
        }
        else if(position == 1)
        {
            return demoFragment1;
        }
        //in case none is selected
        else
        {
            return demoFragment1;
        }
    }

    //Number of Pages
    @Override
    public int getCount() {
        return 2;
    }
}
