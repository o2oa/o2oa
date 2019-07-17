package com.x.component.assemble.control.jaxrs.component;

import java.util.List;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.component.core.entity.Component;

import net.sf.ehcache.Ehcache;

class ActionBase extends StandardJaxrsAction {

	Ehcache cache = ApplicationCache.instance().getCache(Component.class);

	static final String COMPONENT_FILE = "File";
	static final String COMPONENT_NOTE = "Note";
	static final String COMPONENT_MEETING = "Meeting";
	static final String COMPONENT_EXECUTION = "Execution";
	static final String COMPONENT_ATTENDANCE = "Attendance";
	static final String COMPONENT_FORUM = "Forum";
	static final String COMPONENT_HOTARTICLE = "HotArticle";
	static final String COMPONENT_EXEMANAGER = "ExeManager";
	static final String COMPONENT_ONLINEMEETING = "OnlineMeeting";
	static final String COMPONENT_ANN = "ANN";
//	static final String COMPONENT_STRATEGY = "Strategy";
//	static final String COMPONENT_REPORT = "Report";
	static final String COMPONENT_MINDER = "Minder";
	static final String COMPONENT_CALENDAR = "Calendar";
	static final String COMPONENT_SEARCH = "Search";

	List<String> DEFAULT_COMPONENT_LIST = ListTools.toList(COMPONENT_FILE, COMPONENT_NOTE, COMPONENT_MEETING,
			COMPONENT_EXECUTION, COMPONENT_ATTENDANCE, COMPONENT_FORUM, COMPONENT_HOTARTICLE, COMPONENT_EXEMANAGER,
			COMPONENT_ONLINEMEETING, COMPONENT_ANN, COMPONENT_MINDER, COMPONENT_CALENDAR);

	// {
	// "name": "File",
	// "path": "File",
	// "title": "云文件",
	// "iconPath": "appicon.png",
	// "visible":true
	// },
	//
	// {
	// "name": "Note",
	// "path": "Note",
	// "title": "便签",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "Meeting",
	// "path": "Meeting",
	// "title": "会议管理",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "Execution",
	// "path": "Execution",
	// "title": "执行力管理",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "Attendance",
	// "path": "Attendance",
	// "title": "考勤管理",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "Forum",
	// "path": "Forum",
	// "title": "论坛",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "HotArticle",
	// "path": "HotArticle",
	// "title": "热点",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "ExeManager",
	// "path": "ExeManager",
	// "title": "执行力管理",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "OnlineMeeting",
	// "path": "OnlineMeeting",
	// "title": "网络会议",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "Strategy",
	// "path": "Strategy",
	// "title": "战略管理",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "Report",
	// "path": "Report",
	// "title": "工作报告",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "Minder",
	// "path": "Minder",
	// "title": "脑图编辑器",
	// "iconPath": "appicon.png",
	// "visible": true
	// },
	// {
	// "name": "Calendar",
	// "path": "Calendar",
	// "title": "日程安排",
	// "iconPath": "appicon.png",
	// "visible": true
	// }

}
