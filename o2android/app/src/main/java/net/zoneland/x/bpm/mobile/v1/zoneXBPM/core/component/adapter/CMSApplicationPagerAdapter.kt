package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application.CMSCategoryFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson

/**
 * Created by fancy on 2017/6/29.
 * Copyright © 2017 O2. All rights reserved.
 */

class CMSApplicationPagerAdapter(fm: FragmentManager, val categoryList: List<CMSCategoryInfoJson>) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        val info = categoryList[position]
        val bundle = Bundle()
        bundle.putSerializable(CMSCategoryFragment.CMS_CATEGORY_OBJECT_KEY, info)
        val fragment = CMSCategoryFragment()
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int  =  categoryList.size

    override fun getPageTitle(position: Int): CharSequence  =  categoryList[position].categoryName

}