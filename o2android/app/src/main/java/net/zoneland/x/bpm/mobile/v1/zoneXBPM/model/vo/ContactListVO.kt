package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

/**
 * Created by fancy on 2017/4/25.
 */
sealed class ContactListVO {

    class Company(var name: String = "",
                  var companyCount: Int = 0,
                  var departmentCount: Int = 0): ContactListVO()

    class Department(var name: String = "",
                     var departmentCount: Int = 0,
                     var identityCount: Int = 0): ContactListVO()

    class Identity(var name: String = "",
                   var person: String = ""): ContactListVO()

}