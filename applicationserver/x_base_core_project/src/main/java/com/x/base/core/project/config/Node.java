package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.DateTools;

public class Node extends GsonPropertyObject {

	public static final Integer default_nodeAgentPort = 20010;

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
	@FieldDescribe("是否是center节点,仅允许存在一个center节点")	
	private ApplicationServer application;
	private WebServer web;
	private DataServer data;
	private StorageServer storage;
	private String logLevel;
	private ScheduleDump dumpData;
	private ScheduleDump dumpStorage;
	private ScheduleRestore restoreData;
	private ScheduleRestore restoreStorage;
	private Boolean nodeAgentEnable;
	private Boolean nodeAgentEncrypt;
	private Integer nodeAgentPort;
	private Boolean quickStartWebApp;

	public String getLogLevel() {
		// "trace", "debug", "info", "warn", "error" or "off"
		if (StringUtils.equals("trace", this.logLevel)) {
			return "trace";
		} else if (StringUtils.equals("debug", this.logLevel)) {
			return "debug";
		} else if (StringUtils.equals("info", this.logLevel)) {
			return "info";
		} else if (StringUtils.equals("warn", this.logLevel)) {
			return "warn";
		} else if (StringUtils.equals("error", this.logLevel)) {
			return "error";
		} else if (StringUtils.equals("off", this.logLevel)) {
			return "off";
		} else {
			return "warn";
		}
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

	public static class ScheduleDump {

		public boolean available() {
			return DateTools.cronAvailable(this.cron);
		}

		private String cron = "";

		private Integer size = 14;

		public String cron() {
			return (null == cron) ? "" : this.cron;
		}

		public Integer size() {
			return (null == size) ? 14 : this.size;
		}

	}

	public static class ScheduleRestore {

		public boolean available() {
			return DateTools.cronAvailable(this.cron);
		}

		private String cron = "";

		private String date = "";

		public String cron() {
			return (null == cron) ? "" : this.cron;
		}

		public String date() {
			return (null == date) ? "" : this.date;
		}

	}

	public void setQuickStartWebApp(Boolean quickStartWebApp) {
		this.quickStartWebApp = quickStartWebApp;
	}

}
