package jiguang.chat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmList;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragmList = fragments;
    }

    @Override
    public Fragment getItem(int index) {
        // TODO Auto-generated method stub
        return mFragmList.get(index);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mFragmList.size();
    }
}