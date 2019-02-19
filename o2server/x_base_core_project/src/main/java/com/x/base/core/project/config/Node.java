package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

public class Node extends ConfigObject {

	public static final Integer default_nodeAgentPort = 20010;
	public static final String default_banner = "O2OA";
	public static final Integer default_logSize = 14;

	public static Node defaultInstance() {
		Node o = new Node();
		o.enable = true;
		o.isPrimaryCenter = true;
		o.application = ApplicationServer.defaultInstance();
		o.web = WebServer.defaultInstance();
		o.data = DataServer.defaultInstance();
		o.storage = StorageServer.defaultInstance();
		o.logLevel = "warn";
		o.dumpData = new ScheduleDump();
		o.dumpStorage = new ScheduleDump();
		o.restoreData = new ScheduleRestore();
		o.restoreStorage = new ScheduleRestore();
		o.nodeAgentEnable = false;
		o.nodeAgentEncrypt = true;
		o.nodeAgentPort = default_nodeAgentPort;
		o.quickStartWebApp = false;
		return o;
	}

	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("是否是center节点,仅允许存在一个center节点")
	private Boolean isPrimaryCenter;
	@FieldDescribe("Application服务器配置")
	private ApplicationServer application;
	@FieldDescribe("Web服务器配置")
	private WebServer web;
	@FieldDescribe("Data服务器配置")
	private DataServer data;
	@FieldDescribe("Storage服务器配置")
	private StorageServer storage;
	@FieldDescribe("日志级别,默认当前节点的slf4j日志级别,通过系统变量\"org.slf4j.simpleLogger.defaultLogLevel\"设置到当前jvm中.")
	private String logLevel;
	@FieldDescribe("定时数据导出配置")
	private ScheduleDump dumpData;
	@FieldDescribe("定时存储文件导出配置")
	private ScheduleDump dumpStorage;
	@FieldDescribe("定时数据导入配置")
	private ScheduleRestore restoreData;
	@FieldDescribe("定时存储文件导入配置")
	private ScheduleRestore restoreStorage;
	@FieldDescribe("日志文件保留天数.")
	private Integer logSize;
	@FieldDescribe("是否启用节点代理")
	private Boolean nodeAgentEnable;
	@FieldDescribe("是否启用节点端口")
	private Integer nodeAgentPort;
	@FieldDescribe("是否启用节点代理加密")
	private Boolean nodeAgentEncrypt;
	@FieldDescribe("是否使用快速应用部署")
	private Boolean quickStartWebApp;
	@FieldDescribe("服务器控制台启动标识")
	private String banner;
	@FieldDescribe("是否自动启动")
	private Boolean autoStart;

	public Boolean autoStart() {
		return BooleanUtils.isTrue(autoStart);
	}

	public String getLogLevel() {
		// "trace", "debug", "info", "warn", "error" or "off"
		if (StringUtils.equals("trace", this.logLevel)) {
			return "trace";
		} else if (StringUtils.equalsIgnoreCase("debug", this.logLevel)) {
			return "debug";
		} else if (StringUtils.equalsIgnoreCase("info", this.logLevel)) {
			return "info";
		} else if (StringUtils.equalsIgnoreCase("warn", this.logLevel)) {
			return "warn";
		} else if (StringUtils.equalsIgnoreCase("error", this.logLevel)) {
			return "error";
		} else if (StringUtils.equalsIgnoreCase("off", this.logLevel)) {
			return "off";
		} else {
			return "warn";
		}
	}

	public String getBanner() {
		return StringUtils.isBlank(this.banner) ? default_banner : this.banner;
	}

	public Integer logSize() {
		if ((this.logSize != null) && (this.logSize > 0)) {
			return this.logSize;
		}
		return default_logSize;
	}

	public Boolean getQuickStartWebApp() {
		return BooleanUtils.isTrue(quickStartWebApp);
	}

	public Integer nodeAgentPort() {
		if (null == this.nodeAgentPort || this.nodeAgentPort < 0) {
			return default_nodeAgentPort;
		}
		return this.nodeAgentPort;
	}

	public Boolean nodeAgentEnable() {
		return BooleanUtils.isTrue(this.nodeAgentEnable);
	}

	public Boolean nodeAgentEncrypt() {
		return BooleanUtils.isNotFalse(this.nodeAgentEncrypt);
	}

	public Boolean getIsPrimaryCenter() {
		return BooleanUtils.isTrue(this.isPrimaryCenter);
	}

	public ScheduleDump dumpData() {
		return (dumpData == null) ? new ScheduleDump() : this.dumpData;
	}

	public ScheduleDump dumpStorage() {
		return (dumpStorage == null) ? new ScheduleDump() : this.dumpStorage;
	}

	public ScheduleRestore restoreData() {
		return (restoreData == null) ? new ScheduleRestore() : this.restoreData;
	}

	public ScheduleRestore restoreStorage() {
		return (restoreStorage == null) ? new ScheduleRestore() : this.restoreStorage;
	}

	public void setIsPrimaryCenter(Boolean isPrimaryCenter) {
		this.isPrimaryCenter = isPrimaryCenter;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public ApplicationServer getApplication() {
		return application;
	}

	public void setApplication(ApplicationServer application) {
		this.application = application;
	}

	public WebServer getWeb() {
		return web;
	}

	public void setWeb(WebServer web) {
		this.web = web;
	}

	public DataServer getData() {
		return data;
	}

	public void setData(DataServer data) {
		this.data = data;
	}

	public StorageServer getStorage() {
		return storage;
	}

	public void setStorage(StorageServer storage) {
		this.storage = storage;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public static class ScheduleDump extends ConfigObject {

		public static ScheduleDump defaultInstance() {
			return new ScheduleDump();
		}

		public boolean available() {
			return DateTools.cronAvailable(this.cron);
		}

		@FieldDescribe("定时任务cron表达式")
		private String cron = "";

		@FieldDescribe("最大保留份数,超过将自动删除最久的数据.")
		private Integer size = 14;

		public String cron() {
			return (null == cron) ? "" : this.cron;
		}

		public Integer size() {
			return (null == size) ? 14 : this.size;
		}

	}

	public static class ScheduleRestore extends ConfigObject {

		public static ScheduleRestore defaultInstance() {
			return new ScheduleRestore();
		}

		public boolean available() {
			return DateTools.cronAvailable(this.cron);
		}

		@FieldDescribe("定时任务cron表达式")
		private String cron = "";

		@FieldDescribe("导入数据时间戳,需要在local/dump下有此时间戳的文件.")
		private String date = "";

		public String cron() {
			return (null == cron) ? "" : this.cron;
		}

		public String date() {
			return (null == date) ? "" : this.date;
		}

	}

}
