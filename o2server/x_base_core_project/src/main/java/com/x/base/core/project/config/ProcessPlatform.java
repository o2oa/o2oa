package com.x.base.core.project.config;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class ProcessPlatform extends ConfigObject {

	public final static Integer DEFAULT_FORMVERSIONPERIOD = 45;

	public final static Integer DEFAULT_PROCESSVERSIONPERIOD = 45;

	public final static Integer DEFAULT_SCRIPTVERSIONPERIOD = 45;

	public final static Integer DEFAULT_FORMVERSIONCOUNT = 30;

	public final static Integer DEFAULT_PROCESSVERSIONCOUNT = 30;

	public final static Integer DEFAULT_SCRIPTVERSIONCOUNT = 30;

	public final static String DEFAULT_DOCTOWORDTYPE = "local";

	public final static String DOCTOWORDTYPE_LOCAL = "local";
	public final static String DOCTOWORDTYPE_CLOUD = "cloud";

	public final static String DEFAULT_DOCTOWORDDEFAULTFILENAME = "正文.docx";

	public final static String DEFAULT_DOCTOWORDDEFAULTSITE = "$doc";

	public final static Integer DEFAULT_EXECUTORCOUNT = 32;

	public static ProcessPlatform defaultInstance() {
		return new ProcessPlatform();
	}

	public ProcessPlatform() {

		this.maintenanceIdentity = "";
		this.formVersionCount = DEFAULT_FORMVERSIONCOUNT;
		this.processVersionCount = DEFAULT_PROCESSVERSIONCOUNT;
		this.scriptVersionCount = DEFAULT_SCRIPTVERSIONCOUNT;
//		this.formVersionPeriod = DEFAULT_FORMVERSIONPERIOD;
//		this.processVersionPeriod = DEFAULT_PROCESSVERSIONPERIOD;
//		this.scriptVersionPeriod = DEFAULT_SCRIPTVERSIONPERIOD;
		this.docToWordType = DEFAULT_DOCTOWORDTYPE;
		this.docToWordDefaultFileName = DEFAULT_DOCTOWORDDEFAULTFILENAME;
		this.docToWordDefaultSite = DEFAULT_DOCTOWORDDEFAULTSITE;
		this.executorCount = DEFAULT_EXECUTORCOUNT;
		this.urge = new Urge();
		this.expire = new Expire();
		this.touchDelay = new TouchDelay();
		this.dataMerge = new DataMerge();
		this.touchDetained = new TouchDetained();
		this.deleteDraft = new DeleteDraft();
		this.passExpired = new PassExpired();

	}

	@FieldDescribe("维护身份,当工作发生意外错误,无法找到对应的处理人情况下,先尝试将工作分配给创建身份,如果创建身份也不可获取,那么分配给指定人员,默认情况下这个值为空.")
	private String maintenanceIdentity;

	@FieldDescribe("表单历史版本保留数量,0为不保留.")
	private Integer formVersionCount;

	@FieldDescribe("流程历史版本保留数量,0为不保留.")
	private Integer processVersionCount;

	@FieldDescribe("脚本历史版本保留数量,0为不保留.")
	private Integer scriptVersionCount;

//	@FieldDescribe("表单历史版本保留天数.")
//	private Integer formVersionPeriod;
//
//	@FieldDescribe("流程历史版本保留天数.")
//	private Integer processVersionPeriod;
//
//	@FieldDescribe("脚本历史版本保留天数.")
//	private Integer scriptVersionPeriod;

	@FieldDescribe("HTML版式公文转换成Word文件方式,local,cloud.")
	private String docToWordType;

	@FieldDescribe("HTML版式公文转换成Word文件缺省文件名.")
	private String docToWordDefaultFileName;

	@FieldDescribe("HTML版式公文转换成Word文件缺省site.")
	private String docToWordDefaultSite;

	@FieldDescribe("执行器数量")
	private Integer executorCount;

	public Integer getExecutorCount() {
		return ((null == executorCount) || (executorCount < 1)) ? DEFAULT_EXECUTORCOUNT : this.executorCount;
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

//	public Integer getFormVersionPeriod() {
//		return (formVersionPeriod == null || formVersionPeriod < 1) ? DEFAULT_FORMVERSIONPERIOD
//				: this.formVersionPeriod;
//	}
//
//	public Integer getProcessVersionPeriod() {
//		return (processVersionPeriod == null || processVersionPeriod < 1) ? DEFAULT_PROCESSVERSIONPERIOD
//				: this.processVersionPeriod;
//	}
//
//	public Integer getScriptVersionPeriod() {
//		return (scriptVersionPeriod == null || scriptVersionPeriod < 1) ? DEFAULT_SCRIPTVERSIONPERIOD
//				: this.scriptVersionPeriod;
//	}

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

	@FieldDescribe("催办任务设置,发现即将过期时发送提醒消息.")
	private Urge urge;

	@FieldDescribe("将已经过了截至时间的待办标记过期.")
	private Expire expire;

	@FieldDescribe("延时任务设置,定时触发延时任务,当超过延时时间后继续流转.")
	private TouchDelay touchDelay;

	@FieldDescribe("合并任务设置,定时触发合并任务,将已完成工作的Data从Item表中提取合并到WorkCompleted的Data字段中,默认工作完成后2年开始进行合并.")
	private DataMerge dataMerge;

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

	public Urge getUrge() {
		return this.urge == null ? new Urge() : this.urge;
	}

	public Expire getExpire() {
		return this.expire == null ? new Expire() : this.expire;
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

	public DataMerge getDataMerge() {
		return this.dataMerge == null ? new DataMerge() : this.dataMerge;
	}

	public Press getPress() {
		return this.press == null ? new Press() : this.press;
	}

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_PROCESSPLATFORM);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public static class Urge extends ConfigObject {

		public static Urge defaultInstance() {
			Urge o = new Urge();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "30 0/10 8-18 * * ?";

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

		public void setCron(String cron) {
			this.cron = cron;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
	}

	public static class Expire extends ConfigObject {

		public static Expire defaultInstance() {
			Expire o = new Expire();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "45 0/15 8-18 * * ?";

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

		public static TouchDelay defaultInstance() {
			TouchDelay o = new TouchDelay();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "5 0/5 * * * ?";

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

	public static class DataMerge extends ConfigObject {

		public static DataMerge defaultInstance() {
			DataMerge o = new DataMerge();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = false;

		public final static String DEFAULT_CRON = "30 30 6 * * ?";

		public final static Integer DEFAULT_THRESHOLDDAYS = 365 * 2;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("期限,已完成工作结束间隔指定时间进行merge,默认两年后进行merge")
		private Integer thresholdDays = DEFAULT_THRESHOLDDAYS;

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
			return (null == thresholdDays || thresholdDays < 1) ? DEFAULT_THRESHOLDDAYS : thresholdDays;
		}

	}

	public static class TouchDetained extends ConfigObject {

		public static TouchDetained defaultInstance() {
			TouchDetained o = new TouchDetained();
			return o;
		}

		public final static String DEFAULT_CRON = "30 30 12 * * ?";

		public final static Boolean DEFAULT_ENABLE = true;

		public final static Integer DEFAULT_THRESHOLDMINUTES = 60 * 24;

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

		public static DeleteDraft defaultInstance() {
			DeleteDraft o = new DeleteDraft();
			return o;
		}

		public final static String DEFAULT_CRON = "0 0 20 * * ?";

		public final static Boolean DEFAULT_ENABLE = false;

		public final static Integer DEFAULT_THRESHOLDMINUTES = 60 * 24 * 10;

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

		public static PassExpired defaultInstance() {
			PassExpired o = new PassExpired();
			return o;
		}

		public final static String DEFAULT_CRON = "5 5 8-18 * * ?";

		public final static Boolean DEFAULT_ENABLE = true;

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

		public static LogLongDetained defaultInstance() {
			LogLongDetained o = new LogLongDetained();
			return o;
		}

		public final static String DEFAULT_CRON = "0 0 4 * * ?";

		public final static Boolean DEFAULT_ENABLE = true;

		public final static Integer DEFAULT_TASKTHRESHOLDMINUTES = 60 * 24 * 10;

		public final static Integer DEFAULT_READTHRESHOLDMINUTES = 60 * 24 * 10;

		public final static Integer DEFAULT_WORKTHRESHOLDMINUTES = 60 * 24 * 10;

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

		public static Press defaultInstance() {
			Press o = new Press();
			return o;
		}

		public final static Integer DEFAULT_INTERVALMINUTES = 10;

		public final static Integer DEFAULT_COUNT = 3;

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

		public void setIntervalMinutes(Integer intervalMinutes) {
			this.intervalMinutes = intervalMinutes;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

	}

}