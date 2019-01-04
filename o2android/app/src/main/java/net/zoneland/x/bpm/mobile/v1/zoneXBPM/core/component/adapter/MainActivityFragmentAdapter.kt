package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * Created by fancy on 2017/6/9.
 * Copyright © 2017 O2. All rights reserved.
 */

class MainActivityFragmentAdapter(val fragmentList:List<Fragment>, val titleList:List<String>, fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titleList[position]
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        super.destroyItem(container, position, `object`)
    }
}