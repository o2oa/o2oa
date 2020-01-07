package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

/**
 * ToDoFragment 页面ListView的item显示对象
 * 包括信息中心和工作中心对象
 * Created by fancy on 2017/3/27.
 */
data class ToDoFragmentListViewItemVO(var businessId:String = "", //业务id
                                      var businessType:Int = 0, // 0信息中心 1工作中心
                                      var title:String = "",
                                      var type:String = "",
                                      var time:String = "")