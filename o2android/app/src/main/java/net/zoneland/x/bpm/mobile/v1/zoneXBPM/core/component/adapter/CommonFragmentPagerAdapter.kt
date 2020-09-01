package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by fancy on 2017/7/3.
 * Copyright © 2017 O2. All rights reserved.
 */

class CommonFragmentPagerAdapter(fm: FragmentManager, val fragmentList:List<Fragment>, val titles:List<String>): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment =fragmentList[position]

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence = titles[position]
}