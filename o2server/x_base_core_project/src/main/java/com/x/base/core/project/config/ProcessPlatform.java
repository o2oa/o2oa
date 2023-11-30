package com.x.base.core.project.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;

/**
 * @author Zhou Rui
 */
public class ProcessPlatform extends ConfigObject {

	private static final long serialVersionUID = 8025969872758663591L;

	public static final Integer DEFAULT_FORMVERSIONPERIOD = 45;

	public static final Integer DEFAULT_PROCESSVERSIONPERIOD = 45;

	public static final Integer DEFAULT_SCRIPTVERSIONPERIOD = 45;

	public static final Integer DEFAULT_FORMVERSIONCOUNT = 30;

	public static final Integer DEFAULT_PROCESSVERSIONCOUNT = 30;

	public static final Integer DEFAULT_SCRIPTVERSIONCOUNT = 30;

	public static final String DEFAULT_DOCTOWORDTYPE = "local";

	public static final String DOCTOWORDTYPE_LOCAL = "local";

	public static final String DOCTOWORDTYPE_CLOUD = "cloud";

	public static final String DEFAULT_DOCTOWORDDEFAULTFILENAME = "正文.docx";

	public static final String DEFAULT_DOCTOWORDDEFAULTSITE = "$doc";

	public static final Integer DEFAULT_EXECUTORCOUNT = 3;

	public static final Integer DEFAULT_EXECUTORQUEUEBUSYTHRESHOLD = 5;

	public static final Boolean DEFAULT_DELETEPROCESSINUSE = false;

	public static final Boolean DEFAULT_DELETEAPPLICATIONINUSE = false;

	public static final Boolean DEFAULT_PROCESSINGSIGNALPERSISTENABLE = false;

	public static final Integer DEFAULT_ASYNCHRONOUSTIMEOUT = 60;

	public static ProcessPlatform defaultInstance() {
		return new ProcessPlatform();
	}

	public ProcessPlatform() {
		this.maintenanceIdentity = "";
		this.formVersionCount = DEFAULT_FORMVERSIONCOUNT;
		this.processVersionCount = DEFAULT_PROCESSVERSIONCOUNT;
		this.scriptVersionCount = DEFAULT_SCRIPTVERSIONCOUNT;
		this.docToWordType = DEFAULT_DOCTOWORDTYPE;
		this.docToWordDefaultFileName = DEFAULT_DOCTOWORDDEFAULTFILENAME;
		this.docToWordDefaultSite = DEFAULT_DOCTOWORDDEFAULTSITE;
		this.executorCount = DEFAULT_EXECUTORCOUNT;
		this.executorQueueBusyThreshold = DEFAULT_EXECUTORQUEUEBUSYTHRESHOLD;
		this.urge = new Urge();
		this.expire = new Expire();
		this.touchDelay = new TouchDelay();
		this.merge = new Merge();
		this.touchDetained = new TouchDetained();
		this.deleteDraft = new DeleteDraft();
		this.passExpired = new PassExpired();
		this.updateTable = new UpdateTable();
		this.handoverConfig = new HandoverConfig();
		this.processingSignalPersistEnable = DEFAULT_PROCESSINGSIGNALPERSISTENABLE;
		this.asynchronousTimeout = DEFAULT_ASYNCHRONOUSTIMEOUT;
	}

	public Integer getExecutorCount() {
		return ((null == executorCount) || (executorCount < 0)) ? DEFAULT_EXECUTORCOUNT : this.executorCount;
	}

	public Integer getExecutorQueueBusyThreshold() {
		return ((null == executorQueueBusyThreshold) || (executorQueueBusyThreshold < 1))
				? DEFAULT_EXECUTORQUEUEBUSYTHRESHOLD
				: this.executorQueueBusyThreshold;
	}

	public Integer getFormVersionCount() {
		return formVersionCount == null ? DEFAULT_FORMVERSIONCOUNT : this.formVersionCount;
	}

	public Integer getProcessVersionCount() {
		return processVersionCount == null ? DEFAULT_PROCESSVERSIONCOUNT : this.processVersionCount;
	}

	public Integer getScriptVersionCount() {
		return scriptVersionCount == null ? DEFAULT_SCRIPTVERSIONCOUNT : this.scriptVersionCount;
	}

	public String getDocToWordType() {
		return StringUtils.isEmpty(docToWordType) ? DEFAULT_DOCTOWORDTYPE : docToWordType;
	}

	public String getDocToWordDefaultFileName() {
		return StringUtils.isEmpty(docToWordDefaultFileName) ? DEFAULT_DOCTOWORDDEFAULTFILENAME
				: docToWordDefaultFileName;
	}

	public String getDocToWordDefaultSite() {
		return StringUtils.isEmpty(docToWordDefaultSite) ? DEFAULT_DOCTOWORDDEFAULTSITE : docToWordDefaultSite;
	}

	@FieldDescribe("维护身份,当工作发生意外错误,无法找到对应的处理人情况下,先尝试将工作分配给创建身份,如果创建身份也不可获取,那么分配给指定人员,默认情况下这个值为空.")
	private String maintenanceIdentity;

	@FieldDescribe("表单历史版本保留数量,0为不保留.")
	private Integer formVersionCount;

	@FieldDescribe("流程历史版本保留数量,0为不保留.")
	private Integer processVersionCount;

	@FieldDescribe("脚本历史版本保留数量,0为不保留.")
	private Integer scriptVersionCount;

	@FieldDescribe("HTML版式公文转换成Word文件方式,local,cloud.")
	private String docToWordType;

	@FieldDescribe("HTML版式公文转换成Word文件缺省文件名.")
	private String docToWordDefaultFileName;

	@FieldDescribe("HTML版式公文转换成Word文件缺省site.")
	private String docToWordDefaultSite;

	@FieldDescribe("执行器数量")
	private Integer executorCount;

	@FieldDescribe("执行器队列繁忙阈值")
	private Integer executorQueueBusyThreshold;

	@FieldDescribe("催办任务设置,发现即将过期时发送提醒消息.")
	private Urge urge;

	@FieldDescribe("将已经过了截至时间的待办标记过期.")
	private Expire expire;

	@FieldDescribe("延时任务设置,定时触发延时任务,当超过延时时间后继续流转.")
	private TouchDelay touchDelay;

	@FieldDescribe("合并任务设置,定时触发合并任务,将已完成工作的Data从Item表中提取合并到WorkCompleted的Data字段中,默认工作完成后2年开始进行合并.")
	private Merge merge;

	@FieldDescribe("清除草稿状态的工作.")
	private DeleteDraft deleteDraft;

	@FieldDescribe("超时工作路由设置.")
	private PassExpired passExpired;

	@FieldDescribe("触发长时间未处理的工作.")
	private TouchDetained touchDetained;

	@FieldDescribe("记录长期滞留工作,待办,待阅设置.")
	private LogLongDetained logLongDetained;

	@FieldDescribe("提醒设置,设置提醒间隔.")
	private Press press;

	@FieldDescribe("同步到自建表设置.")
	private UpdateTable updateTable;

	@FieldDescribe("归档到Hadoop.")
	private ArchiveHadoop archiveHadoop;

	@FieldDescribe("权限交接定时配置.")
	private HandoverConfig handoverConfig;

	@FieldDescribe("事件扩充.")
	private ExtensionEvents extensionEvents;

	@FieldDescribe("是否保存工作处理信号内容,默认false.")
	private Boolean processingSignalPersistEnable;

	@FieldDescribe("异步超时.")
	private Integer asynchronousTimeout;

	public Integer getAsynchronousTimeout() {
		if ((asynchronousTimeout == null) || (asynchronousTimeout < 1)) {
			this.asynchronousTimeout = DEFAULT_ASYNCHRONOUSTIMEOUT;
		}
		return asynchronousTimeout;
	}

	public Boolean getProcessingSignalPersistEnable() {
		if (processingSignalPersistEnable == null) {
			this.processingSignalPersistEnable = DEFAULT_PROCESSINGSIGNALPERSISTENABLE;
		}
		return processingSignalPersistEnable;
	}

	public ExtensionEvents getExtensionEvents() {
		if (null == extensionEvents) {
			this.extensionEvents = new ExtensionEvents();
		}
		return extensionEvents;
	}

	public ArchiveHadoop getArchiveHadoop() {
		return this.archiveHadoop == null ? new ArchiveHadoop() : this.archiveHadoop;
	}

	public Urge getUrge() {
		return this.urge == null ? new Urge() : this.urge;
	}

	public Expire getExpire() {
		return this.expire == null ? new Expire() : this.expire;
	}

	public UpdateTable getUpdateTable() {
		return this.updateTable == null ? new UpdateTable() : this.updateTable;
	}

	public HandoverConfig getHandoverConfig() {
		return this.handoverConfig == null ? new HandoverConfig() : this.handoverConfig;
	}

	public PassExpired getPassExpired() {
		return this.passExpired == null ? new PassExpired() : this.passExpired;
	}

	public TouchDelay getTouchDelay() {
		return this.touchDelay == null ? new TouchDelay() : this.touchDelay;
	}

	public TouchDetained getTouchDetained() {
		return this.touchDetained == null ? new TouchDetained() : this.touchDetained;
	}

	public DeleteDraft getDeleteDraft() {
		return this.deleteDraft == null ? new DeleteDraft() : this.deleteDraft;
	}

	public LogLongDetained getLogLongDetained() {
		return this.logLongDetained == null ? new LogLongDetained() : this.logLongDetained;
	}

	public Merge getMerge() {
		return this.merge == null ? new Merge() : this.merge;
	}

	public Press getPress() {
		return this.press == null ? new Press() : this.press;
	}

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void save() throws IOException {
		File file = new File(Config.base(), Config.PATH_CONFIG_PROCESSPLATFORM);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public static class Urge extends ConfigObject {

		private static final long serialVersionUID = 1159238658106337292L;

		public static Urge defaultInstance() {
			return new Urge();
		}

		public static final Boolean DEFAULT_ENABLE = false;

		public static final String DEFAULT_CRON = "30 0/10 8-18 * * ?";

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}

	public static class Expire extends ConfigObject {

		private static final long serialVersionUID = -8659190297973094979L;

		public static Expire defaultInstance() {
			return new Expire();
		}

		public static final Boolean DEFAULT_ENABLE = true;

		public static final String DEFAULT_CRON = "45 0/15 8-18 * * ?";

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}

	public static class TouchDelay extends ConfigObject {

		private static final long serialVersionUID = 8716159849643530909L;

		public static TouchDelay defaultInstance() {
			return new TouchDelay();
		}

		public static final Boolean DEFAULT_ENABLE = true;

		public static final String DEFAULT_CRON = "5 0/5 * * * ?";

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}

	public static class Merge extends ConfigObject {

		private static final long serialVersionUID = -5858277850858377338L;

		public static Merge defaultInstance() {
			return new Merge();
		}

		public Merge() {
			this.enable = DEFAULT_ENABLE;
			this.cron = DEFAULT_CRON;
			this.thresholdDays = DEFAULT_THRESHOLDDAYS;
			this.batchSize = DEFAULT_BATCHSIZE;
		}

		public static final Boolean DEFAULT_ENABLE = false;

		public static final String DEFAULT_CRON = "30 30 6 * * ?";

		public static final Integer DEFAULT_THRESHOLDDAYS = 365 * 2;

		public static final Integer DEFAULT_BATCHSIZE = 100;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("期限,已完成工作结束间隔指定时间进行merge,默认两年后进行merge")
		private Integer thresholdDays = DEFAULT_THRESHOLDDAYS;

		@FieldDescribe("批量大小.")
		private Integer batchSize = DEFAULT_BATCHSIZE;

		public Integer getBatchSize() {
			return NumberTools.nullOrLessThan(this.batchSize, 1) ? DEFAULT_BATCHSIZE : this.batchSize;
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public Integer getThresholdDays() {
			return (null == thresholdDays || thresholdDays < 0) ? DEFAULT_THRESHOLDDAYS : thresholdDays;
		}

	}

	public static class TouchDetained extends ConfigObject {

		private static final long serialVersionUID = -1557669565639237145L;

		public static TouchDetained defaultInstance() {
			return new TouchDetained();
		}

		public static final String DEFAULT_CRON = "30 30 12 * * ?";

		public static final Boolean DEFAULT_ENABLE = false;

		public static final Integer DEFAULT_THRESHOLDMINUTES = 60 * 24;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("当工作滞留设定时间后,将尝试触发工作流转,可以自动处理由于人员变动的引起的工作滞留,默认24*60分钟.")
		private Integer thresholdMinutes = DEFAULT_THRESHOLDMINUTES;

		public Integer getThresholdMinutes() {
			return (null == thresholdMinutes || thresholdMinutes < 0) ? DEFAULT_THRESHOLDMINUTES : thresholdMinutes;
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

	}

	public static class DeleteDraft extends ConfigObject {

		private static final long serialVersionUID = -2127401618190754038L;

		public static DeleteDraft defaultInstance() {
			return new DeleteDraft();
		}

		public static final String DEFAULT_CRON = "0 0 20 * * ?";

		public static final Boolean DEFAULT_ENABLE = false;

		public static final Integer DEFAULT_THRESHOLDMINUTES = 60 * 24 * 10;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("设定阈值,如果超过这个时间认为是可以删除的草稿,默认为10天.")
		private Integer thresholdMinutes = DEFAULT_THRESHOLDMINUTES;

		public Integer getThresholdMinutes() {
			return (null == thresholdMinutes || thresholdMinutes < 0) ? DEFAULT_THRESHOLDMINUTES : thresholdMinutes;
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

	}

	public static class PassExpired extends ConfigObject {

		private static final long serialVersionUID = 2660210790582417811L;

		public static PassExpired defaultInstance() {
			return new PassExpired();
		}

		public static final String DEFAULT_CRON = "5 5 8-18 * * ?";

		public static final Boolean DEFAULT_ENABLE = true;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}
	}

	public static class LogLongDetained extends ConfigObject {

		private static final long serialVersionUID = 6183346578358650613L;

		public static LogLongDetained defaultInstance() {
			return new LogLongDetained();
		}

		public static final String DEFAULT_CRON = "0 0 4 * * ?";

		public static final Boolean DEFAULT_ENABLE = true;

		public static final Integer DEFAULT_TASKTHRESHOLDMINUTES = 60 * 24 * 10;

		public static final Integer DEFAULT_READTHRESHOLDMINUTES = 60 * 24 * 10;

		public static final Integer DEFAULT_WORKTHRESHOLDMINUTES = 60 * 24 * 10;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("设定待办滞留阈值,.")
		private Integer taskThresholdMinutes = DEFAULT_TASKTHRESHOLDMINUTES;

		@FieldDescribe("设定待阅滞留阈值,.")
		private Integer readThresholdMinutes = DEFAULT_READTHRESHOLDMINUTES;

		@FieldDescribe("设定工作滞留阈值,.")
		private Integer workThresholdMinutes = DEFAULT_WORKTHRESHOLDMINUTES;

		public Integer getTaskThresholdMinutes() {
			return (null == taskThresholdMinutes || taskThresholdMinutes < 0) ? DEFAULT_TASKTHRESHOLDMINUTES
					: taskThresholdMinutes;
		}

		public Integer getReadThresholdMinutes() {
			return (null == readThresholdMinutes || readThresholdMinutes < 0) ? DEFAULT_READTHRESHOLDMINUTES
					: readThresholdMinutes;
		}

		public Integer getWorkThresholdMinutes() {
			return (null == workThresholdMinutes || workThresholdMinutes < 0) ? DEFAULT_WORKTHRESHOLDMINUTES
					: workThresholdMinutes;
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

	}

	public static class Press extends ConfigObject {

		private static final long serialVersionUID = -4223137429803117953L;

		public static Press defaultInstance() {
			return new Press();
		}

		public static final Integer DEFAULT_INTERVALMINUTES = 10;

		public static final Integer DEFAULT_COUNT = 3;

		@FieldDescribe("提醒间隔(分钟)")
		private Integer intervalMinutes = DEFAULT_INTERVALMINUTES;

		@FieldDescribe("提醒数量限制.")
		private Integer count = DEFAULT_COUNT;

		public Integer getIntervalMinutes() {
			return (intervalMinutes == null || intervalMinutes < 1) ? DEFAULT_INTERVALMINUTES : this.intervalMinutes;
		}

		public Integer getCount() {
			return (count == null || count < 0) ? DEFAULT_COUNT : this.count;
		}

	}

	public static class ExtensionEvents {

		@FieldDescribe("工作附件上传.")
		private WorkExtensionEvents workAttachmentUploadEvents = new WorkExtensionEvents();
		@FieldDescribe("工作附件下载.")
		private WorkExtensionEvents workAttachmentDownloadEvents = new WorkExtensionEvents();
		@FieldDescribe("工作版式文件转word.")
		private WorkExtensionEvents workDocToWordEvents = new WorkExtensionEvents();
		@FieldDescribe("工作版式文件转OFD.")
		private WorkExtensionEvents workDocToOfdEvents = new WorkExtensionEvents();
		@FieldDescribe("已完成工作附件上传.")
		private WorkCompletedExtensionEvents workCompletedAttachmentUploadEvents = new WorkCompletedExtensionEvents();
		@FieldDescribe("已完成工作附件下载.")
		private WorkCompletedExtensionEvents workCompletedAttachmentDownloadEvents = new WorkCompletedExtensionEvents();
		@FieldDescribe("已完成工作版式文件转word.")
		private WorkCompletedExtensionEvents workCompletedDocToWordEvents = new WorkCompletedExtensionEvents();
		@FieldDescribe("已完成工作版式文件转OFD.")
		private WorkCompletedExtensionEvents workCompletedDocToOfdEvents = new WorkCompletedExtensionEvents();

		public WorkExtensionEvents getWorkAttachmentUploadEvents() {
			if (null == this.workAttachmentUploadEvents) {
				this.workAttachmentUploadEvents = new WorkExtensionEvents();
			}
			return workAttachmentUploadEvents;
		}

		public WorkExtensionEvents getWorkAttachmentDownloadEvents() {
			if (null == this.workAttachmentDownloadEvents) {
				this.workAttachmentDownloadEvents = new WorkExtensionEvents();
			}
			return workAttachmentDownloadEvents;
		}

		public WorkExtensionEvents getWorkDocToWordEvents() {
			if (null == this.workDocToWordEvents) {
				this.workDocToWordEvents = new WorkExtensionEvents();
			}
			return workDocToWordEvents;
		}

		public WorkExtensionEvents getWorkDocToOfdEvents() {
			if (null == this.workDocToOfdEvents) {
				this.workDocToOfdEvents = new WorkExtensionEvents();
			}
			return workDocToOfdEvents;
		}

		public WorkCompletedExtensionEvents getWorkCompletedAttachmentUploadEvents() {
			if (null == this.workCompletedAttachmentUploadEvents) {
				this.workCompletedAttachmentUploadEvents = new WorkCompletedExtensionEvents();
			}
			return workCompletedAttachmentUploadEvents;
		}

		public WorkCompletedExtensionEvents getWorkCompletedAttachmentDownloadEvents() {
			if (null == this.workCompletedAttachmentDownloadEvents) {
				this.workCompletedAttachmentDownloadEvents = new WorkCompletedExtensionEvents();
			}
			return workCompletedAttachmentDownloadEvents;
		}

		public WorkCompletedExtensionEvents getWorkCompletedDocToWordEvents() {
			if (null == this.workCompletedDocToWordEvents) {
				this.workCompletedDocToWordEvents = new WorkCompletedExtensionEvents();
			}
			return workCompletedDocToWordEvents;
		}

		public WorkCompletedExtensionEvents getWorkCompletedDocToOfdEvents() {
			if (null == this.workCompletedDocToOfdEvents) {
				this.workCompletedDocToOfdEvents = new WorkCompletedExtensionEvents();
			}
			return workCompletedDocToOfdEvents;
		}

	}

	public static class WorkExtensionEvents extends ArrayList<WorkExtensionEvent> {

		private static final long serialVersionUID = -2847222465064494098L;

		public Optional<WorkExtensionEvent> bind(String application, String process, String activity) {
			return this.stream().filter(o -> BooleanUtils.isTrue(o.getEnable()))
					.filter(o -> (ListTools.contains(o.getApplications(), application)
							&& ListTools.contains(o.getProcesses(), process)
							&& ListTools.contains(o.getActivities(), activity))
							|| (ListTools.contains(o.getApplications(), application)
									&& ListTools.contains(o.getProcesses(), process)
									&& ListTools.isEmpty(o.getActivities()))
							|| (ListTools.contains(o.getApplications(), application)
									&& ListTools.isEmpty(o.getProcesses()) && ListTools.isEmpty(o.getActivities()))
							|| (ListTools.isEmpty(o.getApplications()) && ListTools.isEmpty(o.getProcesses())
									&& ListTools.isEmpty(o.getActivities())))
					.sorted((x, y) -> {
						if (x.getActivities().contains(activity)) {
							return 1;
						} else if (y.getActivities().contains(activity)) {
							return -1;
						} else if (x.getProcesses().contains(process)) {
							return 1;
						} else if (y.getProcesses().contains(process)) {
							return -1;
						} else if (x.getApplications().contains(application)) {
							return 1;
						} else if (y.getApplications().contains(application)) {
							return -1;
						} else {
							return 0;
						}
					}).findFirst();
		}

	}

	public static class WorkExtensionEvent {

		private Boolean enable;

		private List<String> applications;
		private List<String> processes;
		private List<String> activities;

		private String url;

		private String custom;

		public Boolean getEnable() {
			return enable;
		}

		public List<String> getApplications() {
			return applications;
		}

		public List<String> getProcesses() {
			return processes;
		}

		public List<String> getActivities() {
			return activities;
		}

		public String getUrl() {
			return url;
		}

		public String getCustom() {
			return custom;
		}

	}

	/**
	 *
	 * 查找优先级 流程-> 应用 -> 默认设置(流程和应用为空)
	 *
	 */
	public static class WorkCompletedExtensionEvents extends ArrayList<WorkCompletedExtensionEvent> {

		private static final long serialVersionUID = -5527994436451255023L;

		public Optional<WorkCompletedExtensionEvent> bind(String application, String process) {
			return this.stream().filter(o -> BooleanUtils.isTrue(o.getEnable()))
					.filter(o -> (ListTools.contains(o.getApplications(), application)
							&& ListTools.contains(o.getProcesses(), process))
							|| (ListTools.contains(o.getApplications(), application)
									&& ListTools.isEmpty(o.getProcesses()))
							|| (ListTools.isEmpty(o.getApplications()) && ListTools.isEmpty(o.getProcesses())))
					.sorted((x, y) -> {
						if (x.getProcesses().contains(process)) {
							return 1;
						} else if (y.getProcesses().contains(process)) {
							return -1;
						} else if (x.getApplications().contains(application)) {
							return 1;
						} else if (y.getApplications().contains(application)) {
							return -1;
						} else {
							return 0;
						}
					}).findFirst();
		}
	}

	public static class WorkCompletedExtensionEvent {

		private Boolean enable;
		private List<String> applications;
		private List<String> processes;
		private String url;
		private String custom;

		public Boolean getEnable() {
			return enable;
		}

		public List<String> getApplications() {
			return applications;
		}

		public List<String> getProcesses() {
			return processes;
		}

		public String getUrl() {
			return url;
		}

		public String getCustom() {
			return custom;
		}

	}

	public static class ArchiveHadoop extends ConfigObject {

		private static final long serialVersionUID = -8274136904009320770L;

		public static ArchiveHadoop defaultInstance() {
			return new ArchiveHadoop();
		}

		public ArchiveHadoop() {
			this.enable = DEFAULT_ENABLE;
			this.cron = DEFAULT_CRON;
			this.fsDefaultFS = DEFAULT_FS_DEFAULTFS;
			this.username = DEFAULT_USERNAME;
			this.path = DEFAULT_PATH;
		}

		private static final Boolean DEFAULT_ENABLE = false;
		public static final String DEFAULT_CRON = "20 20 * * * ?";
		private static final String DEFAULT_FS_DEFAULTFS = "hdfs://";
		private static final String DEFAULT_USERNAME = "";
		private static final String DEFAULT_PATH = "";

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("hadoop地址.")
		private String fsDefaultFS;

		@FieldDescribe("hadoop用户名.")
		private String username;

		@FieldDescribe("fs路径前缀.")
		private String path;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String getFsDefaultFS() {
			return StringUtils.isEmpty(this.fsDefaultFS) ? DEFAULT_FS_DEFAULTFS : this.fsDefaultFS;
		}

		public String getUsername() {
			return StringUtils.isEmpty(this.username) ? DEFAULT_USERNAME : this.username;
		}

		public String getPath() {
			return StringUtils.isEmpty(this.path) ? DEFAULT_PATH : this.path;
		}
	}

	public static class UpdateTable extends ConfigObject {

		private static final long serialVersionUID = -7066262450518673067L;

		public static UpdateTable defaultInstance() {
			return new UpdateTable();
		}

		public static final Boolean DEFAULT_ENABLE = true;

		public static final String DEFAULT_CRON = "20 20 * * * ?";

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}

	public static class HandoverConfig extends ConfigObject {

		private static final long serialVersionUID = -5710800319348361625L;

		public static HandoverConfig defaultInstance() {
			return new HandoverConfig();
		}

		public static final Boolean DEFAULT_ENABLE = true;

		public static final String DEFAULT_CRON = "0 0/5 * * * ?";

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}
}
