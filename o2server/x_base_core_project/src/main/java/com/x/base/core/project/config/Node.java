package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.CronTools;
import com.x.base.core.project.tools.NumberTools;

public class Node extends ConfigObject {

	private static final long serialVersionUID = -6598734923326779693L;

	private static final Integer DEFAULT_NODEAGENTPORT = 20010;
	private static final String DEFAULT_BANNER = "O2OA";
	private static final Boolean DEFAULT_SELFHEALTHCHECKENABLE = false;
	// private static final Integer DEFAULT_ORDER = 0;

	public static Node defaultInstance() {
		Node o = new Node();
		o.enable = true;
		o.center = CenterServer.defaultInstance();
		o.application = ApplicationServer.defaultInstance();
		o.web = WebServer.defaultInstance();
		o.data = DataServer.defaultInstance();
		o.storage = StorageServer.defaultInstance();
		o.dumpData = new ScheduleDumpData();
		o.restoreData = new ScheduleRestoreData();
		o.nodeAgentEnable = true;
		o.nodeAgentEncrypt = true;
		o.nodeAgentPort = DEFAULT_NODEAGENTPORT;
		o.autoStart = true;
		o.selfHealthCheckEnable = DEFAULT_SELFHEALTHCHECKENABLE;
		// o.order = DEFAULT_ORDER;
		return o;
	}

	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("节点顺序,节点选举顺序0,1,2...")
	private Integer order;
	@FieldDescribe("Center服务器配置")
	private CenterServer center;
	@FieldDescribe("Application服务器配置")
	private ApplicationServer application;
	@FieldDescribe("Web服务器配置")
	private WebServer web;
	@FieldDescribe("Data服务器配置")
	private DataServer data;
	@FieldDescribe("Storage服务器配置")
	private StorageServer storage;
	@FieldDescribe("定时数据导出配置")
	private ScheduleDumpData dumpData;
	@FieldDescribe("定时数据导入配置")
	private ScheduleRestoreData restoreData;
	@FieldDescribe("定时执行java stack trace")
	private ScheduleStackTrace stackTrace;
	@FieldDescribe("是否启用节点代理")
	private Boolean nodeAgentEnable;
	@FieldDescribe("是否启用节点端口")
	private Integer nodeAgentPort;
	@FieldDescribe("是否启用节点代理加密")
	private Boolean nodeAgentEncrypt;
	@FieldDescribe("服务器控制台启动标识")
	private String banner;
	@FieldDescribe("是否自动启动")
	private Boolean autoStart;
	@FieldDescribe("是否启用节点上模块健康自检查,如果启用在提交到center之前将进行模块的健康检查.默认false")
	private Boolean selfHealthCheckEnable;

//	public Integer getOrder() {
//		return order == null ? DEFAULT_ORDER : this.order;
//	}

	public Boolean getSelfHealthCheckEnable() {
		return BooleanUtils.isTrue(selfHealthCheckEnable);
	}

	protected void setCenter(CenterServer centerServer) {
		this.center = centerServer;
	}

	public Boolean autoStart() {
		return BooleanUtils.isNotFalse(autoStart);
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public CenterServer getCenter() {
		return (center == null) ? CenterServer.defaultInstance() : this.center;
	}

	public ApplicationServer getApplication() {
		return (application == null) ? ApplicationServer.defaultInstance() : this.application;
	}

	public WebServer getWeb() {
		return (web == null) ? WebServer.defaultInstance() : this.web;
	}

	public DataServer getData() {
		return (data == null) ? DataServer.defaultInstance() : this.data;
	}

	public StorageServer getStorage() {
		return (storage == null) ? StorageServer.defaultInstance() : this.storage;
	}

	public String getBanner() {
		return StringUtils.isBlank(this.banner) ? DEFAULT_BANNER : this.banner;
	}

	public Integer nodeAgentPort() {
		if (null == this.nodeAgentPort || this.nodeAgentPort < 0) {
			return DEFAULT_NODEAGENTPORT;
		}
		return this.nodeAgentPort;
	}

	public Boolean nodeAgentEnable() {
		return BooleanUtils.isTrue(this.nodeAgentEnable);
	}

	public Boolean nodeAgentEncrypt() {
		return BooleanUtils.isNotFalse(this.nodeAgentEncrypt);
	}

	public ScheduleDumpData dumpData() {
		return (dumpData == null) ? new ScheduleDumpData() : this.dumpData;
	}

	public ScheduleRestoreData restoreData() {
		return (restoreData == null) ? new ScheduleRestoreData() : this.restoreData;
	}

	public ScheduleStackTrace stackTrace() {
		return (stackTrace == null) ? new ScheduleStackTrace() : this.stackTrace;
	}

	public static class ScheduleDumpData extends ConfigObject {

		private static final long serialVersionUID = -3841277189943216415L;

		public static ScheduleDumpData defaultInstance() {
			return new ScheduleDumpData();
		}

		public boolean available() {
			return CronTools.available(this.cron());
		}

		@FieldDescribe("是否启用,默认禁用.")
		private Boolean enable = false;

		@FieldDescribe("定时任务cron表达式,默认每天凌晨2点进行备份.")
		private String cron = "";

		@FieldDescribe("最大保留份数,超过将自动删除最久的数据.")
		private Integer size = 7;

		@FieldDescribe("备份路径")
		private String path = "";

		public Boolean enable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String cron() {
			return (null == cron) ? "5 0 2 * * ?" : this.cron;
		}

		public Integer size() {
			return (null == size) ? 14 : this.size;
		}

		public String path() {
			return StringUtils.trim(path);
		}

	}

	public static class ScheduleRestoreData extends ConfigObject {

		private static final long serialVersionUID = 3352945576815162805L;

		public static ScheduleRestoreData defaultInstance() {
			return new ScheduleRestoreData();
		}

		public boolean available() {
			return CronTools.available(this.cron) && StringUtils.isNotEmpty(this.path);
		}

		@FieldDescribe("是否启用.")
		private Boolean enable = false;

		@FieldDescribe("定时任务cron表达式")
		private String cron = "";

		@FieldDescribe("恢复路径")
		private String path = "";

		public Boolean enable() {
			return (BooleanUtils.isTrue(this.enable)) ? true : false;
		}

		public String cron() {
			return (null == cron) ? "" : this.cron;
		}

		public String path() {
			return StringUtils.trim(path);
		}

	}

	public static class ScheduleStackTrace extends ConfigObject {

		private static final long serialVersionUID = -7308289859339511471L;

		private static final Boolean DEFAULT_ENABLE = false;
		private static final Integer DEFAULT_INTERVAL = 20;

		public ScheduleStackTrace() {
			this.enable = DEFAULT_ENABLE;
			this.interval = DEFAULT_INTERVAL;
		}

		public static ScheduleStackTrace defaultInstance() {
			return new ScheduleStackTrace();
		}

		@FieldDescribe("是否启用.")
		private Boolean enable = false;

		@FieldDescribe("运行jstack 间隔,默认20秒.")
		private Integer interval = DEFAULT_INTERVAL;

		public Boolean getEnable() {
			return (BooleanUtils.isTrue(this.enable));
		}

		public Integer getInterval() {
			return NumberTools.nullOrLessThan(interval, 0) ? DEFAULT_INTERVAL : this.interval;
		}

	}

}
