package com.x.base.core.project.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

import org.apache.commons.io.FileUtils;

public class Components extends ConfigObject {

	public static final String NAME_SETTING = "Setting";
	public static final String NAME_ORG = "Org";
	public static final String NAME_CMSMANAGER = "cmsManager";
	public static final String NAME_APPLICATIONEXPLORER = "ApplicationExplorer";
	public static final String NAME_PORTALEXPLORER = "PortalExplorer";
	public static final String NAME_DATAEXPLORER = "DataExplorer";
	public static final String NAME_SERVICEMANAGER = "service.ServiceManager";
	public static final String NAME_APPMARKET = "AppMarketV2";
	public static final String NAME_APPCENTER = "AppCenter";
	public static final String NAME_LOGVIEWER = "LogViewer";
	public static final String NAME_PROFILE = "Profile";
	public static final String NAME_BAM = "BAM";
	public static final String NAME_CMS = "cms";
	public static final String NAME_TASKCENTER = "TaskCenter";
	public static final String NAME_HOMEPAGE = "Homepage";
	public static final String NAME_HOTARTICLE = "HotArticle";
//	public static final String NAME_FILE = "File";
	public static final String NAME_NOTE = "Note";
	public static final String NAME_MEETING = "Meeting";
	//public static final String NAME_ONLINEMEETING = "OnlineMeeting";
	public static final String NAME_ATTENDANCE = "Attendance";
	public static final String NAME_FORUM = "Forum";
	public static final String NAME_MINDER = "Minder";
	public static final String NAME_CALENDAR = "Calendar";
	public static final String NAME_ANN = "ANN";
	public static final String NAME_SEARCH = "Search";
	public static final String NAME_IM = "IMV2";

	public static List<String> SYSTEM_NAME_NAMES = ListTools.toList(NAME_SETTING, NAME_ORG, NAME_CMSMANAGER,
			NAME_APPLICATIONEXPLORER, NAME_PORTALEXPLORER, NAME_DATAEXPLORER, NAME_SERVICEMANAGER, NAME_APPMARKET,
			NAME_APPCENTER, NAME_LOGVIEWER, NAME_PROFILE, NAME_BAM, NAME_CMS, NAME_TASKCENTER, NAME_HOMEPAGE,
			NAME_HOTARTICLE, NAME_NOTE, NAME_MEETING, NAME_ATTENDANCE, NAME_FORUM,
			NAME_MINDER, NAME_CALENDAR, NAME_ANN, NAME_SEARCH, NAME_IM);

	public static final String APPICON_PNG = "appicon.png";

	public static Component systemComponent(String name) {
		switch (name) {
		case NAME_SETTING:
			return new Component(NAME_SETTING, NAME_SETTING, "系统设置", APPICON_PNG, 1, Component.TYPE_SYSTEM);
		case NAME_ORG:
			return new Component(NAME_ORG, NAME_ORG, "组织管理", APPICON_PNG, 2, Component.TYPE_SYSTEM);
		case NAME_CMSMANAGER:
			return new Component(NAME_CMSMANAGER, "cms.Column", "内容管理平台", APPICON_PNG, 3, Component.TYPE_SYSTEM);
		case NAME_APPLICATIONEXPLORER:
			return new Component(NAME_APPLICATIONEXPLORER, "process.ApplicationExplorer", "流程管理平台", APPICON_PNG, 4,
					Component.TYPE_SYSTEM);
		case NAME_PORTALEXPLORER:
			return new Component(NAME_PORTALEXPLORER, "portal.PortalExplorer", "门户管理平台", APPICON_PNG, 5,
					Component.TYPE_SYSTEM);
		case NAME_DATAEXPLORER:
			return new Component(NAME_DATAEXPLORER, "query.QueryExplorer", "数据中心平台", APPICON_PNG, 6,
					Component.TYPE_SYSTEM);
		case NAME_SERVICEMANAGER:
			return new Component(NAME_SERVICEMANAGER, NAME_SERVICEMANAGER, "服务管理平台", APPICON_PNG, 7,
					Component.TYPE_SYSTEM);
		case NAME_APPMARKET:
			return new Component(NAME_APPMARKET, NAME_APPMARKET, "应用市场", APPICON_PNG, 8, Component.TYPE_SYSTEM);
		case NAME_APPCENTER:
			return new Component(NAME_APPCENTER, NAME_APPCENTER, "应用管理", APPICON_PNG, 9, Component.TYPE_SYSTEM);
		case NAME_LOGVIEWER:
			return new Component(NAME_LOGVIEWER, NAME_LOGVIEWER, "日志", APPICON_PNG, 10, Component.TYPE_SYSTEM);
		case NAME_PROFILE:
			return new Component(NAME_PROFILE, NAME_PROFILE, "个人设置", APPICON_PNG, 11, Component.TYPE_SYSTEM);
		case NAME_BAM:
			return new Component(NAME_BAM, NAME_BAM, "流程监控", APPICON_PNG, 12, Component.TYPE_SYSTEM);
		case NAME_CMS:
			return new Component(NAME_CMS, "cms.Index", "信息平台", APPICON_PNG, 12, Component.TYPE_SYSTEM);
		case NAME_TASKCENTER:
			return new Component(NAME_TASKCENTER, "process.TaskCenter", "办公中心", APPICON_PNG, 13, Component.TYPE_SYSTEM);
		case NAME_HOMEPAGE:
			return new Component(NAME_HOMEPAGE, NAME_HOMEPAGE, "首页", APPICON_PNG, 14, Component.TYPE_SYSTEM);
		case NAME_HOTARTICLE:
			return new Component(NAME_HOTARTICLE, NAME_HOTARTICLE, "热点", APPICON_PNG, 15, Component.TYPE_SYSTEM);
//		case NAME_FILE:
//			return new Component(NAME_FILE, NAME_FILE, "云文件", APPICON_PNG, 16, Component.TYPE_SYSTEM);
		case NAME_NOTE:
			return new Component(NAME_NOTE, NAME_NOTE, "便签", APPICON_PNG, 17, Component.TYPE_SYSTEM);
		case NAME_MEETING:
			return new Component(NAME_MEETING, NAME_MEETING, "会议管理", APPICON_PNG, 18, Component.TYPE_SYSTEM);
//		case NAME_ONLINEMEETING:
//			return new Component(NAME_ONLINEMEETING, NAME_ONLINEMEETING, "网络会议", APPICON_PNG, 19,
//					Component.TYPE_SYSTEM);
		case NAME_ATTENDANCE:
			return new Component(NAME_ATTENDANCE, NAME_ATTENDANCE, "考勤管理", APPICON_PNG, 20, Component.TYPE_SYSTEM);
		case NAME_FORUM:
			return new Component(NAME_FORUM, NAME_FORUM, "论坛", APPICON_PNG, 21, Component.TYPE_SYSTEM);
		case NAME_MINDER:
			return new Component(NAME_MINDER, NAME_MINDER, "脑图编辑器", APPICON_PNG, 22, Component.TYPE_SYSTEM);
		case NAME_CALENDAR:
			return new Component(NAME_CALENDAR, NAME_CALENDAR, "日程安排", APPICON_PNG, 23, Component.TYPE_SYSTEM);
		case NAME_ANN:
			return new Component(NAME_ANN, NAME_ANN, "神经网络", APPICON_PNG, 24, Component.TYPE_SYSTEM);
		case NAME_SEARCH:
			return new Component(NAME_SEARCH, NAME_SEARCH, "搜索", APPICON_PNG, 25, Component.TYPE_SYSTEM);
		case NAME_IM:
			return new Component(NAME_IM, NAME_IM, "聊天", APPICON_PNG, 26, Component.TYPE_SYSTEM);
		default:
			return null;
		}
	}

	public static Components defaultInstance() {
		Components o = new Components();
		o.systems.add(systemComponent(NAME_SETTING));
		o.systems.add(systemComponent(NAME_ORG));
		o.systems.add(systemComponent(NAME_CMSMANAGER));
		o.systems.add(systemComponent(NAME_CMS));
		o.systems.add(systemComponent(NAME_APPLICATIONEXPLORER));
		o.systems.add(systemComponent(NAME_PORTALEXPLORER));
		o.systems.add(systemComponent(NAME_DATAEXPLORER));
		o.systems.add(systemComponent(NAME_SERVICEMANAGER));
		o.systems.add(systemComponent(NAME_APPMARKET));
		o.systems.add(systemComponent(NAME_APPCENTER));
		o.systems.add(systemComponent(NAME_LOGVIEWER));
		o.systems.add(systemComponent(NAME_PROFILE));
		o.systems.add(systemComponent(NAME_BAM));
		o.systems.add(systemComponent(NAME_TASKCENTER));
		o.systems.add(systemComponent(NAME_HOMEPAGE));
		o.systems.add(systemComponent(NAME_HOTARTICLE));
//		o.systems.add(systemComponent(NAME_FILE));
		o.systems.add(systemComponent(NAME_NOTE));
		o.systems.add(systemComponent(NAME_MEETING));
//		o.systems.add(systemComponent(NAME_ONLINEMEETING));
		o.systems.add(systemComponent(NAME_ATTENDANCE));
		o.systems.add(systemComponent(NAME_FORUM));
		o.systems.add(systemComponent(NAME_MINDER));
		o.systems.add(systemComponent(NAME_CALENDAR));
		o.systems.add(systemComponent(NAME_ANN));
		o.systems.add(systemComponent(NAME_SEARCH));
		o.systems.add(systemComponent(NAME_IM));
		return o;
	}

	@FieldDescribe("默认模块")
	private List<Component> systems = new ArrayList<>();

	public Components() {
		// nothing
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_PORTAL);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public static class Component {

		public static final String TYPE_SYSTEM = "system";
		public static final String TYPE_CUSTOM = "custom";

		public Component() {

		}

		public Component(String name, String path, String title, String iconPath, Integer orderNumber, String type) {
			this.name = name;
			this.path = path;
			this.title = title;
			this.iconPath = iconPath;
			this.orderNumber = orderNumber;
			this.type = type;
		}

		@FieldDescribe("名称")
		private String name;

		@FieldDescribe("路径")
		private String path;

		@FieldDescribe("标题")
		private String title;

		@FieldDescribe("iconPath")
		private String iconPath;

		@FieldDescribe("排序号")
		private Integer orderNumber;

		@FieldDescribe("类型")
		private String type;

		@FieldDescribe("允许列表,可以混用person,role")
		private List<String> allowList = new ArrayList<>();

		@FieldDescribe("禁止列表,可以混用person,role")
		private List<String> dentyList = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIconPath() {
			return iconPath;
		}

		public void setIconPath(String iconPath) {
			this.iconPath = iconPath;
		}

		public List<String> getAllowList() {
			return allowList;
		}

		public void setAllowList(List<String> allowList) {
			this.allowList = allowList;
		}

		public List<String> getDentyList() {
			return dentyList;
		}

		public void setDentyList(List<String> dentyList) {
			this.dentyList = dentyList;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Integer getOrderNumber() {
			return orderNumber;
		}

		public void setOrderNumber(Integer orderNumber) {
			this.orderNumber = orderNumber;
		}

	}

	public List<Component> getSystems() {
		return systems;
	}

	public void setSystems(List<Component> systems) {
		this.systems = systems;
	}

}