package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R

/**
 * Created by fancy on 2017/4/24.
 */
sealed class ContactFragmentVO {

    class GroupHeader(var name: String = "",
                      var resId: Int = R.mipmap.icon_contact_my_department) : ContactFragmentVO()

    class MyDepartment(var name: String = "") : ContactFragmentVO()

    class MyCompany(var name: String = "") : ContactFragmentVO()

    class MyGroup(var name: String = "",
                  var display: String = "",
                  var groupList: ArrayList<String> = ArrayList(),
                  var personList: ArrayList<String> = ArrayList()) : ContactFragmentVO()

    class MyCollect(var name: String = "",
                    var dept: String = "",
                    var gender: String = "",
                    var mobile: String = "") : ContactFragmentVO()
}