package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import java.io.Serializable

/**
 * 考勤api 数据对象集合
 * Created by fancy on 2017/3/28.
 */

/**
 * 管理员
 */
data class AdministratorInfoJson(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var organizationName: String = "",
        var organizationOu: String = "",
        var adminName: String = "",
        var adminLevel: String = "",
        var distributeFactor: Int = 0

)

/**
 * 查询审批的过滤条件
 */
data class AppealApprovalQueryFilterJson(
        var status: String = "", // 0待审批 1审批通过 -1审批未通过 999所有
        var yearString: String = "", //年份 2016
        var monthString: String = "",
        var processPerson1: String = "", //审批人 就是当前用户
        var appealReason: String = "",
        var departmentName: String = "",
        var empName: String = ""
)

/**
 * 审批申诉对象
 */
data class AppealApprovalFormJson(
        var ids: ArrayList<String> = ArrayList(),
        var opinion: String = "", //审核意见
        var status: String = ""//审核状态:1-通过;2-需要进行复核;-1-不通过
)

/**
 * 考勤统计周期
 */
data class AttendanceStatisticCycle(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var topUnitName: String = "",
        var unitName: String = "",
        var cycleYear: String = "",
        var cycleMonth: String = "",
        var cycleStartDateString: String = "",
        var cycleEndDateString: String = "",
        var cycleStartDate: String = "",
        var cycleEndDate: String = "",
        var description: String = ""
)

/**
 * 申诉对象
 */
data class AppealInfoJson(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var detailId: String = "",
        var empName: String = "", //distinguishedName
        var topUnitName: String = "", //distinguishedName
        var unitName: String = "", //distinguishedName
        var companyName: String = "",
        var departmentName: String = "",
        var yearString: String = "",
        var monthString: String = "",
        var appealDateString: String = "",
        var recordDateString: String = "",
        var recordDate: String = "",
        var status: Int = 0,
        var startTime: String = "",
        var endTime: String = "",
        var appealReason: String = "",
        var selfHolidayType: String = "",
        var address: String = "",
        var appealDescription: String = "",
        var currentProcessor: String = "",
        var processPerson1: String = "",
        var processPersonDepartment1: String = "",
        var processPersonCompany1: String = ""
)

/**
 * 反馈对象 status：SUCCESS
 */
data class BackInfoJson(
        var message: String = "",
        var status: String = ""
)

/**
 * 考勤信息查询过滤条件
 */
data class AttendanceDetailQueryFilterJson(
        var cycleYear: String = "", //年份 如 2016
        var cycleMonth: String = "", //月份 如 04
        var key: String = "", //recordDateString
        var order: String = "", //排序 desc asc
        var q_empName: String = ""//当前用户
)

/**
 * 考勤详细信息
 */
data class AttendanceDetailInfoJson(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var empNo: String = "",
        var empName: String = "",
        var companyName: String = "",
        var departmentName: String = "",
        var yearString: String = "",
        var monthString: String = "",
        var recordDateString: String = "",
        var recordDate: String = "",
        var cycleYear: String = "",
        var cycleMonth: String = "",
        var isHoliday: Boolean = false,
        var isWorkday: Boolean = false,
        var isGetSelfHolidays: Boolean = false,
        var selfHolidayDayTime: String = "",
        var absentDayTime: String = "",
        var abnormalDutyDayTime: String = "",
        var getSelfHolidayDays: Double = 0.0,
        var isWeekend: Boolean = false,
        var onWorkTime: String = "",
        var offWorkTime: String = "",
        var onDutyTime: String = "",
        var offDutyTime: String = "",
        var isLate: Boolean = false,
        var lateTimeDuration: Long = 0L,
        var isLeaveEarlier: Boolean = false,
        var leaveEarlierTimeDuration: Long = 0L,
        var isAbsent: Boolean = false,
        var isAbnormalDuty: Boolean = false,
        var isLackOfTime: Boolean = false,
        var isWorkOvertime: Boolean = false,
        var workOvertimeTimeDuration: Long = 0L,
        var workTimeDuration: Long = 0L,
        var attendance: Double = 0.0,
        var absence: Double = 0.0,
        var recordStatus: Int = 0,
        var batchName: String = "",
        var description: String = "",
        //申诉相关信息
        var identity: String = "", //多身份的时候选择的身份dn
        var appealStatus: Int = 0, //申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
        var appealReason: String = "", //原因  临时请假  出差 因公外出 其他
        var appealDescription: String = "", //事由
        var selfHolidayType: String = "", //如果原因是临时请假 这里需要选择一个请假类型 ：带薪年休假 带薪病假 带薪福利假 扣薪事假 其他
        var address: String = "", //外出地址
        var startTime: String = "", // yyyy-MM-dd HH:mm
        var endTime: String = "", // yyyy-MM-dd HH:mm
        var appealProcessor: String = "",//申诉审批人
        var processPerson1: String = ""// 审批人一
) : Serializable

/**
 * 手机打卡对象
 */
data class MobileCheckInJson(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var empNo: String = "", //员工号
        var empName: String = "", //姓名
        var recordDateString: String = "", //打卡记录日期字符串：yyyy-mm-dd, 必须填写.
        var signTime: String = "", //打卡时间: hh24:mi:ss, 必须填写.
        var signDescription: String = "", //打卡说明:上班打卡，下班打卡, 可以为空.
        var description: String = "",
        var recordAddress: String = "", //打卡地点描述, 可以为空.
        var longitude: String = "", //经度, 可以为空.
        var latitude: String = "", //纬度, 可以为空.
        var optMachineType: String = "", //操作设备类别：手机品牌|PAD|PC|其他, 可以为空.
        var optSystemName: String = O2.DEVICE_TYPE, //操作设备类别：Mac|Windows|IOS|Android|其他, 可以为空.
        var recordStatus: Int = 0//记录状态：0-未分析 1-已分析
)

/**
 * 查询手机打卡的过滤条件
 */
data class MobileCheckInQueryFilterJson(
        var empNo: String = "",
        var empName: String = "",
        var startDate: String = "",
        var endDate: String = "",
        var signDescription: String = ""//打卡说明:上班打卡，下班打卡, 可以为空.
)

/**
 * 工作场所对象
 */
data class MobileCheckInWorkplaceInfoJson(
        var id: String = "",
        var updateTime: String = "",
        var placeName: String = "",
        var placeAlias: String = "",
        var creator: String = "",
        var longitude: String = "",
        var latitude: String = "",
        var errorRange: Int = 0,
        var description: String = ""
)

/**
 * 考勤设置信息
 */
data class SettingInfoJson(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var configCode: String = "",
        var configName: String = "",
        var configValue: String = "",
        var ordernumber: Int = 0
)