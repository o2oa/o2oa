package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api

/**
 * 日志收集信息对象
 * Created by fancyLou on 12/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

data class APILogData(
        var unit:String = "",//绑定的单位id
        var unitName:String = "",
        var centerHost:String = "",//中心服务器host
        var centerContext:String = "",
        var centerPort:String = "",
        var deviceToken:String = "",//手机设备号
        var distinguishedName:String = "",//当前用户的distinguishedName
        var name:String = "",//当前用户的姓名
        var mobile:String = "",
        var o2Version:String = "",//当前O2应用版本号
        var osType:String = "",//android ios
        var osVersion:String = "",//当前手机系统版本
        var osCpu:String = "",//cpu 信息
        var osMemory:String = "",//内存信息
        var osDpi:String = "",//手机分辨率
        var androidManufacturer:String = "",//android手机制造商
        var manufacturerOsVersion:String = "",//android 定制系统版本
        var logDate:String = "",//日志记录日期
        var logContent:String = ""//这个是日志信息 内容比较大 有可能是几M的

)