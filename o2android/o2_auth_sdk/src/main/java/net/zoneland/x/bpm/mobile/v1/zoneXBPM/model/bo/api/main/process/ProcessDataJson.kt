package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.process

/**
 * 流程数据对象
 */
data class ProcessDataJson(
		var process: ProcessJson?,
		var weekBegin: Int = 0,
		var meetingViewer: List<String> = ArrayList(),
		var disableViewList: List<String> = ArrayList(),
		var mobileCreateEnable: Boolean = false,
		var toMyMeetingViewName: String = "",
		var toMonthViewName: String = "",
		var toWeekViewName: String = "",
		var toDayViewName: String = "",
		var toListViewName: String = "",
		var toRoomViewName: String = ""
)

data class ProcessJson(
		var name: String = "",
		var id: String = "",
		var application: String = "",
		var applicationName: String = "",
		var alias: String = ""
)