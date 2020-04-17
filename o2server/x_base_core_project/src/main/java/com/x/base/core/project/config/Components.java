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

	public static final String NAME_SETTINGS = "Settings";
	public static final String NAME_ORG = "Org";
	public static final String NAME_PROFILE = "Profile";
	public static final String NAME_APPMARKET = "AppMarket";
	public static final String NAME_FILE = "File";
	public static final String NAME_BAM = "BAM";
	public static final String NAME_NOTE = "Note";
	public static final String NAME_MEETING = "Meeting";
	public static final String NAME_ATTENDANCE = "Attendance";
	public static final String NAME_FORUM = "Forum";
	public static final String NAME_HOTARTICLE = "HotArticle";
	public static final String NAME_ONLINEMEETING = "OnlineMeeting";
	public static final String NAME_ANN = "ANN";
	public static final String NAME_MINDER = "Minder";
	public static final String NAME_CALENDAR = "Calendar";
	public static final String NAME_SEARCH = "Search";
	public static final String NAME_HOMEPAGE = "Homepage";

	

	public static List<String> SYSTEM_NAME_NAMES = ListTools.toList(NAME_SETTINGS, NAME_ORG,
			NAME_PROFILE, NAME_APPMARKET, NAME_FILE, NAME_BAM, NAME_NOTE, NAME_MEETING,
			NAME_ATTENDANCE, NAME_FORUM, NAME_HOTARTICLE, NAME_ONLINEMEETING, NAME_ANN,
			NAME_MINDER, NAME_CALENDAR, NAME_SEARCH, NAME_HOMEPAGE);

	public static final String APPICON_PNG = "appicon.png";

	public static Components defaultInstance() {
		Components o = new Components();
		o.systems.add(
				new Component(NAME_SETTINGS, NAME_SETTINGS, "系统设置", APPICON_PNG, 1, Component.TYPE_SYSTEM));
		o.systems.add(
				new Component(NAME_APPMARKET, NAME_APPMARKET, "应用市场", APPICON_PNG, 2, Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_ANN, NAME_ANN, "神经网络", APPICON_PNG, 3, Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_ORG, NAME_ORG, "组织管理", APPICON_PNG, 4, Component.TYPE_SYSTEM));
		o.systems.add(
				new Component(NAME_PROFILE, NAME_PROFILE, "个人设置", APPICON_PNG, 5, Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_BAM, NAME_BAM, "流程监控", APPICON_PNG, 6, Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_FILE, NAME_FILE, "云文件", APPICON_PNG, 7, Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_NOTE, NAME_NOTE, "便签", APPICON_PNG, 8, Component.TYPE_SYSTEM));
		o.systems.add(
				new Component(NAME_MEETING, NAME_MEETING, "会议管理", APPICON_PNG, 9, Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_ATTENDANCE, NAME_ATTENDANCE, "考勤管理", APPICON_PNG, 10,
				Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_FORUM, NAME_FORUM, "论坛", APPICON_PNG, 11, Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_HOTARTICLE, NAME_HOTARTICLE, "热点", APPICON_PNG, 12,
				Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_ONLINEMEETING, NAME_ONLINEMEETING, "网络会议", APPICON_PNG, 13,
				Component.TYPE_SYSTEM));
		o.systems.add(
				new Component(NAME_MINDER, NAME_MINDER, "脑图编辑器", APPICON_PNG, 14, Component.TYPE_SYSTEM));
		o.systems.add(
				new Component(NAME_CALENDAR, NAME_CALENDAR, "日程安排", APPICON_PNG, 15, Component.TYPE_SYSTEM));
		o.systems.add(new Component(NAME_SEARCH, NAME_SEARCH, "搜索", APPICON_PNG, 16, Component.TYPE_SYSTEM));
		o.systems.add(
				new Component(NAME_HOMEPAGE, NAME_HOMEPAGE, "首页", APPICON_PNG, 17, Component.TYPE_SYSTEM));
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

		public Component(String name, String path, String title, String iconPath, Integer order, String type) {
			this.name = name;
			this.path = path;
			this.title = title;
			this.iconPath = iconPath;
			this.order = order;
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
		private Integer order;

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

		public Integer getOrder() {
			return order;
		}

		public void setOrder(Integer order) {
			this.order = order;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public List<Component> getSystems() {
		return systems;
	}

	public void setSystems(List<Component> systems) {
		this.systems = systems;
	}

}