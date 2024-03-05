package com.x.base.core.project.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class AppStyle extends ConfigObject {

	public static AppStyle defaultInstance() {
		return new AppStyle();
	}

	public static final String INDEXTYPE_DEFAULT = "default";
	public static final String INDEXTYPE_PORTAL = "portal";
	public static final String INDEX_PAGE_HOME = "home";
	public static final String INDEX_PAGE_IM = "im";
	public static final String INDEX_PAGE_CONTACT = "contact";
	public static final String INDEX_PAGE_APP = "app";
	public static final String INDEX_PAGE_SETTINGS = "settings";

	public AppStyle() {

		this.indexType = INDEXTYPE_DEFAULT;
		this.indexPortal = "";
		this.simpleMode = false;
		this.indexCentered = false;
		this.systemMessageSwitch = true;
		this.systemMessageCanClick = true;
		this.needGray = false;
		this.appExitAlert = "";
		this.contactPermissionView = "addressPowerView"; // 默认视图名 addressPowerView 可到应用市场下载通讯录应用查看
		this.speechScript = "";
		this.promotionPageScript = "";

	}

	public String getIndexType() {
		return StringUtils.equals(INDEXTYPE_PORTAL, this.indexType) ? INDEXTYPE_PORTAL : INDEXTYPE_DEFAULT;
	}

	public String getIndexPortal() {
		return indexPortal;
	}

	public TreeSet<Image> getImages() throws Exception {
		if (null == images || images.isEmpty()) {
			this.images = new TreeSet<Image>();
		}
		if (!this.images.contains(Image.launch_logo())) {
			this.images.add(Image.launch_logo());
		}
		if (!this.images.contains(Image.login_avatar())) {
			this.images.add(Image.login_avatar());
		}
		if (!this.images.contains(Image.index_bottom_menu_logo_blur())) {
			this.images.add(Image.index_bottom_menu_logo_blur());
		}
		if (!this.images.contains(Image.index_bottom_menu_logo_focus())) {
			this.images.add(Image.index_bottom_menu_logo_focus());
		}
		if (!this.images.contains(Image.process_default())) {
			this.images.add(Image.process_default());
		}
		if (!this.images.contains(Image.setup_about_logo())) {
			this.images.add(Image.setup_about_logo());
		}
		if (!this.images.contains(Image.application_top())) {
			this.images.add(Image.application_top());
		}
		return this.images;
	}

	public TreeSet<NativeApp> getNativeAppList() {
		if (null == this.nativeAppList || this.nativeAppList.isEmpty()) {
			this.nativeAppList = new TreeSet<NativeApp>();
		}
		if (!this.nativeAppList.contains(NativeApp.nativeTask())) {
			this.nativeAppList.add(NativeApp.nativeTask());
		}
		if (!this.nativeAppList.contains(NativeApp.nativeTaskCompleted())) {
			this.nativeAppList.add(NativeApp.nativeTaskCompleted());
		}
		if (!this.nativeAppList.contains(NativeApp.nativeRead())) {
			this.nativeAppList.add(NativeApp.nativeRead());
		}
		if (!this.nativeAppList.contains(NativeApp.nativeReadCompleted())) {
			this.nativeAppList.add(NativeApp.nativeReadCompleted());
		}
		if (!this.nativeAppList.contains(NativeApp.nativeMeeting())) {
			this.nativeAppList.add(NativeApp.nativeMeeting());
		}
		if (!this.nativeAppList.contains(NativeApp.nativeFile())) {
			this.nativeAppList.add(NativeApp.nativeFile());
		}
		if (!this.nativeAppList.contains(NativeApp.nativeCms())) {
			this.nativeAppList.add(NativeApp.nativeCms());
		}
		if (!this.nativeAppList.contains(NativeApp.nativeBbs())) {
			this.nativeAppList.add(NativeApp.nativeBbs());
		}
		if (!this.nativeAppList.contains(NativeApp.nativeAttendance())) {
			this.nativeAppList.add(NativeApp.nativeAttendance());
		}
//		if (!this.nativeAppList.contains(NativeApp.nativeO2ai())) {
//			this.nativeAppList.add(NativeApp.nativeO2ai());
//		}
		if (!this.nativeAppList.contains(NativeApp.nativeCalendar())) {
			this.nativeAppList.add(NativeApp.nativeCalendar());
		}
		if (!this.nativeAppList.contains(NativeApp.nativeMindMap())) {
			this.nativeAppList.add(NativeApp.nativeMindMap());
		}
		return this.nativeAppList;
	}

	@FieldDescribe("首页展现类型,default是移动端原来的首页,portal是门户.")
	private String indexType;

	@FieldDescribe("门户首页.")
	private String indexPortal;

	@FieldDescribe("移动端简易模式")
	private Boolean simpleMode;

	@FieldDescribe("首页居中，首页居中页面个数将不可配置")
	private Boolean indexCentered;

	@FieldDescribe("移动端页面")
	private TreeSet<String> appIndexPages;

	@FieldDescribe(("移动App消息列表中是否显示系统通知"))
	private Boolean systemMessageSwitch;
	@FieldDescribe(("移动App系统通知是否可点击打开"))
	private Boolean systemMessageCanClick;
	
	@FieldDescribe(("是否需要全局黑白"))
	private Boolean needGray;

	@FieldDescribe("app退出提示")
	private String appExitAlert;

	@FieldDescribe("移动App通讯录权限视图")
	private String contactPermissionView;

	@FieldDescribe("导航设置")
	private TreeSet<NativeApp> nativeAppList = new TreeSet<>();

	@FieldDescribe("图片设置.")
	private TreeSet<Image> images = new TreeSet<>();

	@FieldDescribe("首页办公中心，流程过滤条件.")
	private List<String> processFilterList = new ArrayList<>();

	@FieldDescribe("首页信息中心，分类过滤条件.")
	private List<String> cmsCategoryFilterList = new ArrayList<>();

	@FieldDescribe("语音助手 invoke 脚本")
	private String speechScript;

	@FieldDescribe("推广页 invoke 脚本")
	private String promotionPageScript;

	@FieldDescribe("扩展参数")
	private JsonObject extendParam;

	public JsonObject getExtendParam() {
		return extendParam;
	}

	public void setExtendParam(JsonObject extendParam) {
		this.extendParam = extendParam;
	}

	public String getSpeechScript() {
		return speechScript;
	}

	public void setSpeechScript(String speechScript) {
		this.speechScript = speechScript;
	}

	public String getPromotionPageScript() {
		return promotionPageScript;
	}

	public void setPromotionPageScript(String promotionPageScript) {
		this.promotionPageScript = promotionPageScript;
	}

	public Boolean getNeedGray() {
		return needGray;
	}

	public void setNeedGray(Boolean needGray) {
		this.needGray = needGray;
	}

	public Boolean getIndexCentered() {
    return indexCentered;
  }

  public void setIndexCentered(Boolean indexCentered) {
    this.indexCentered = indexCentered;
  }

  public List<String> getProcessFilterList() {
    return processFilterList;
  }

  public void setProcessFilterList(List<String> processFilterList) {
    this.processFilterList = processFilterList;
  }

  public List<String> getCmsCategoryFilterList() {
    return cmsCategoryFilterList;
  }

  public void setCmsCategoryFilterList(List<String> cmsCategoryFilterList) {
    this.cmsCategoryFilterList = cmsCategoryFilterList;
  }

  public Boolean getSystemMessageCanClick() {
		return systemMessageCanClick;
	}

	public void setSystemMessageCanClick(Boolean systemMessageCanClick) {
		this.systemMessageCanClick = systemMessageCanClick;
	}

	public String getAppExitAlert() {
		return appExitAlert;
	}

	public void setAppExitAlert(String appExitAlert) {
		this.appExitAlert = appExitAlert;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public void setIndexPortal(String indexPortal) {
		this.indexPortal = indexPortal;
	}

	public Boolean getSystemMessageSwitch() {
		return systemMessageSwitch;
	}

	public void setSystemMessageSwitch(Boolean systemMessageSwitch) {
		this.systemMessageSwitch = systemMessageSwitch;
	}

	public Boolean getSimpleMode() {
		return simpleMode;
	}

	public void setSimpleMode(Boolean simpleMode) {
		this.simpleMode = simpleMode;
	}


	public String getContactPermissionView() {
		return contactPermissionView;
	}

	public void setContactPermissionView(String contactPermissionView) {
		this.contactPermissionView = contactPermissionView;
	}
	

	public TreeSet<String> getAppIndexPages() {
		if (null == this.appIndexPages || this.appIndexPages.isEmpty()) {
			this.appIndexPages = new TreeSet<String>();
			this.appIndexPages.add(INDEX_PAGE_HOME);
			this.appIndexPages.add(INDEX_PAGE_IM);
			this.appIndexPages.add(INDEX_PAGE_CONTACT);
			this.appIndexPages.add(INDEX_PAGE_APP);
			this.appIndexPages.add(INDEX_PAGE_SETTINGS);
		}
		return this.appIndexPages;
	}

	public void setAppIndexPages(TreeSet<String> appIndexPages) {
		this.appIndexPages = appIndexPages;
	}


	public static class Image extends GsonPropertyObject implements Comparable<Image> {

		// 启动页和关于页面的logo图片
		public static Image launch_logo() {
			Image o = new Image();
			o.setName(name_launch_logo);
			o.setPath(default_launch_logo_path);
			return o;
		}

		// 登录页面logo图片
		public static Image login_avatar() {
			Image o = new Image();
			o.setName(name_login_avatar);
			o.setPath(default_login_avatar_path);
			return o;
		}

		// 底部菜单主页未选中logo图片
		public static Image index_bottom_menu_logo_blur() {
			Image o = new Image();
			o.setName(name_index_bottom_menu_logo_blur);
			o.setPath(default_index_bottom_menu_logo_blur_path);
			return o;
		}

		// 底部菜单主页选中logo图片
		public static Image index_bottom_menu_logo_focus() {
			Image o = new Image();
			o.setName(name_index_bottom_menu_logo_focus);
			o.setPath(default_index_bottom_menu_logo_focus_path);
			return o;
		}

		// 流程默认logo图片
		public static Image process_default() {
			Image o = new Image();
			o.setName(name_process_default);
			o.setPath(default_process_default_path);
			return o;
		}

		// 设置页面logo图片
		public static Image setup_about_logo() {
			Image o = new Image();
			o.setName(name_setup_about_logo);
			o.setPath(default_setup_about_logo_path);
			return o;
		}
		// 应用页面顶部图片
		public static Image application_top() {
			Image o = new Image();
			o.setName(name_application_top);
			o.setPath(default_application_top_path);
			return o;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Image other = (Image) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		public static final String name_launch_logo = "launch_logo";
		public static final String name_login_avatar = "login_avatar";
		public static final String name_index_bottom_menu_logo_blur = "index_bottom_menu_logo_blur";
		public static final String name_index_bottom_menu_logo_focus = "index_bottom_menu_logo_focus";
		// public static final String name_people_avatar_default =
		// "people_avatar_default";
		public static final String name_process_default = "process_default";
		public static final String name_setup_about_logo = "setup_about_logo";
		public static final String name_application_top = "application_top";


		public static final String default_image_folder_path = "x_desktop/img/app";
		public static final String default_launch_logo_path =  "x_desktop/img/app/default/launch_logo.png";
		public static final String default_login_avatar_path = "x_desktop/img/app/default/login_avatar.png";
		public static final String default_index_bottom_menu_logo_blur_path = "x_desktop/img/app/default/index_bottom_menu_logo_blur.png";
		public static final String default_index_bottom_menu_logo_focus_path = "x_desktop/img/app/default/index_bottom_menu_logo_focus.png";
		public static final String default_process_default_path = "x_desktop/img/app/default/process_default.png";
		public static final String default_setup_about_logo_path = "x_desktop/img/app/default/setup_about_logo.png";
		public static final String default_application_top_path = "x_desktop/img/app/default/application_top.png";
 
		private String name;
		private String value;
		private String path; // 图片相对路径 pic1.png , 移动端前端使用的时候 需要拼接  web 服务器地址，如：http://127.0.0.1/pic1.png



		public String getPath() {
			return path;
    }

    public void setPath(String path) {
			this.path = path;
    }

    public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public int compareTo(Image o) {
			return this.name.compareTo(o.getName());
		}

	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_APPSTYLE);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public static class NativeApp extends GsonPropertyObject implements Comparable<NativeApp> {

		private Integer id;
		private String key;
		private String name;
		private String displayName;
		private Boolean enable;
		private IOS iOS = new IOS();

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		
		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public IOS getiOS() {
			return iOS;
		}

		public void setiOS(IOS iOS) {
			this.iOS = iOS;
		}

		public static NativeApp nativeTask() {
			NativeApp o = new NativeApp();
			o.setId(1);
			o.setKey("task");
			o.setName("待办");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("task");
			o.getiOS().setVcname("todoTask");
			return o;
		}

		public static NativeApp nativeTaskCompleted() {
			NativeApp o = new NativeApp();
			o.setId(2);
			o.setKey("taskcompleted");
			o.setName("已办");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("task");
			o.getiOS().setVcname("todoTask");
			return o;
		}

		public static NativeApp nativeRead() {
			NativeApp o = new NativeApp();
			o.setId(3);
			o.setKey("read");
			o.setName("待阅");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("task");
			o.getiOS().setVcname("todoTask");
			return o;
		}

		public static NativeApp nativeReadCompleted() {
			NativeApp o = new NativeApp();
			o.setId(4);
			o.setKey("readcompleted");
			o.setName("已阅");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("task");
			o.getiOS().setVcname("todoTask");
			return o;
		}

		public static NativeApp nativeMeeting() {
			NativeApp o = new NativeApp();
			o.setId(5);
			o.setKey("meeting");
			o.setName("会议管理");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("meeting");
			o.getiOS().setVcname("");
			return o;
		}

		public static NativeApp nativeFile() {
			NativeApp o = new NativeApp();
			o.setId(6);
			o.setKey("yunpan");
			o.setName("企业网盘");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("cloudStorage");
			o.getiOS().setVcname("");
			return o;
		}

		public static NativeApp nativeBbs() {
			NativeApp o = new NativeApp();
			o.setId(7);
			o.setKey("bbs");
			o.setName("企业论坛");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("bbs");
			o.getiOS().setVcname("");
			return o;
		}

		public static NativeApp nativeCms() {
			NativeApp o = new NativeApp();
			o.setId(8);
			o.setKey("cms");
			o.setName("信息中心");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("information");
			o.getiOS().setVcname("");
			return o;
		}

		public static NativeApp nativeAttendance() {
			NativeApp o = new NativeApp();
			o.setId(9);
			o.setKey("attendance");
			o.setName("考勤打卡");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("checkin");
			o.getiOS().setVcname("");
			return o;
		}
//
//		public static NativeApp nativeO2ai() {
//			NativeApp o = new NativeApp();
//			o.setId(10);
//			o.setKey("o2ai");
//			o.setName("语音助手");
//			o.setEnable(true);
//			o.getiOS().setCategory("native");
//			o.getiOS().setSubcategory("coding");
//			o.getiOS().setStoryboard("checkin");
//			o.getiOS().setVcname("");
//			return o;
//		}

		public static NativeApp nativeCalendar() {
			NativeApp o = new NativeApp();
			o.setId(11);
			o.setKey("calendar");
			o.setName("日程安排");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("checkin");
			o.getiOS().setVcname("");
			return o;
		}

		// "id": 12,
		// "key": "",
		// "name": "脑图",
		// "enable": true,
		// "iOS": {
		// "category": "native",
		// "subcategory": "coding",
		// "storyboard": "flutter",
		// "vcname": ""
		// }

		public static NativeApp nativeMindMap() {
			NativeApp o = new NativeApp();
			o.setId(12);
			o.setKey("mindMap");
			o.setName("脑图");
			o.setEnable(true);
			o.getiOS().setCategory("native");
			o.getiOS().setSubcategory("coding");
			o.getiOS().setStoryboard("flutter");
			o.getiOS().setVcname("");
			return o;
		}

		@Override
		public int compareTo(NativeApp o) {
			return this.getId().compareTo(o.getId());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NativeApp other = (NativeApp) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

	}

	public static class IOS {

		private String category;
		private String subcategory;
		private String storyboard;
		private String vcname;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getSubcategory() {
			return subcategory;
		}

		public void setSubcategory(String subcategory) {
			this.subcategory = subcategory;
		}

		public String getStoryboard() {
			return storyboard;
		}

		public void setStoryboard(String storyboard) {
			this.storyboard = storyboard;
		}

		public String getVcname() {
			return vcname;
		}

		public void setVcname(String vcname) {
			this.vcname = vcname;
		}
	}

	public void setImages(TreeSet<Image> images) {
		this.images = images;
	}

	public void setNativeAppList(TreeSet<NativeApp> nativeAppList) {
		this.nativeAppList = nativeAppList;
	}

}