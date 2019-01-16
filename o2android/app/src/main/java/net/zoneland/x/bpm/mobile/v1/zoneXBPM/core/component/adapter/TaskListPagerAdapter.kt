package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.TaskFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskApplicationData

/**
 * Created by fancy on 2017/6/21.
 * Copyright © 2017 O2. All rights reserved.
 */

class TaskListPagerAdapter(fm:FragmentManager, val applications:ArrayList<TaskApplicationData>): FragmentPagerAdapter(fm) {



    override fun getItem(position: Int): Fragment {
        val fragment = TaskFragment()
        val arguments = Bundle()
        arguments.putString(TaskFragment.APPLICATION_ID_KEY, applications[position].value)
        fragment.arguments = arguments
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence  = applications[position].name

    override fun getCount(): Int  = applications.size
}