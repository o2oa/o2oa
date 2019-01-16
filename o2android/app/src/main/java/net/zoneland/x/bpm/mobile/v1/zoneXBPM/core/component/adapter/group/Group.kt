package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group

/**
 * Created by fancy on 2017/3/30.
 */


data class Group<out T, out R>(val header: T, val children: List<R>)