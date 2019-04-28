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

	public static ProcessPlatform defaultInstance() {
		return new ProcessPlatform();
	}

	public ProcessPlatform() {
		this.urge = new Urge();
		this.expire = new Expire();
		this.delay = new Delay();
		this.reorganize = new Reorganize();
		this.maintenanceIdentity = "";
	}

	@FieldDescribe("提醒设置,设置提醒间隔.")
	private Press press;

	@FieldDescribe("催办任务设置,发现即将过期时发送提醒消息.")
	private Urge urge;

	@FieldDescribe("过期任务设置,将执行3个独立任务,1.将已经过了截至时间的待办标记过期,2.触发设置了过期路由的工作,3.如果启用了自动流转,那么开始自动流转,可以选择仅处理唯一路由的工作,或者启动基于MLP的人工神经网络进行处理.")
	private Expire expire;

	@FieldDescribe("延时任务设置,定时触发延时任务,当超过延时时间后继续流转.")
	private Delay delay;

	@FieldDescribe("整理任务设置,将执行4个独立任务,1.删除无效的待办,2.删除流程或者应用不存在的工作,3.将活动节点错误的工作调度到开始节点,4.触发滞留时间过长的工作.")
	private Reorganize reorganize;

	@FieldDescribe("维护身份,当工作发生意外错误,无法找到对应的处理人情况下,先尝试将工作分配给创建身份,如果创建身份也不可获取,那么分配给指定人员,默认情况下这个值为空.")
	private String maintenanceIdentity;

	public Press getPress() {
		return this.press == null ? new Press() : this.press;
	}

	public Urge getUrge() {
		return this.urge == null ? new Urge() : this.urge;
	}

	public Expire getExpire() {
		return this.expire == null ? new Expire() : this.expire;
	}

	public Delay getDelay() {
		return this.delay == null ? new Delay() : this.delay;
	}

	public Reorganize getReorganize() {
		return this.reorganize == null ? new Reorganize() : this.reorganize;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_PROCESSPLATFORM);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public static class Press extends ConfigObject {

		public static Press defaultInstance() {
			Press o = new Press();
			return o;
		}

		public final static Integer DEFAULT_INTERVALMINUTES = 10;

		public final static Integer DEFAULT_COUNT = 3;

		@FieldDescribe("提醒间隔(分钟)")
		private Integer intervalMinutes;

		@FieldDescribe("提醒数量限制.")
		private Integer count;

		public Integer getIntervalMinutes() {
			return (intervalMinutes == null || intervalMinutes < 0) ? DEFAULT_INTERVALMINUTES : this.intervalMinutes;
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

	public static class Urge extends ConfigObject {

		public static Urge defaultInstance() {
			Urge o = new Urge();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "6 6/10 8-18 * * ?";

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

		public final static String DEFAULT_CRON = "8 8/10 * * * ?";

		public final static String AUTO_NEURAL = "neural";
		public final static String AUTO_SINGLE = "single";
		public final static String AUTO_DISABLE = "disable";

		public final static String DEFAULT_AUTO = AUTO_DISABLE;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("自动处理模式,disable:禁用,neural:人工神经网络,single:仅处理只有一条路由的工作.")
		private String auto = DEFAULT_AUTO;

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

		public String getAuto() {
			return StringUtils.isEmpty(this.auto) ? DEFAULT_AUTO : this.auto;
		}

		public void setAuto(String auto) {
			this.auto = auto;
		}

	}

	public static class Delay extends ConfigObject {

		public static Delay defaultInstance() {
			Delay o = new Delay();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "2 2/10 * * * ?";

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

		public void setCron(String cron) {
			this.cron = cron;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
	}

	public static class Reorganize extends ConfigObject {

		public static Reorganize defaultInstance() {
			Reorganize o = new Reorganize();
			return o;
		}

		public final static String DEFAULT_CRON = "30 15 8,12,14 * * ?";

		public final static Boolean DEFAULT_ENABLE = true;

		public final static Integer DEFAULT_TRIGGERAFTERMINUTES = 60 * 24;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("当工作滞留设定时间后,将尝试触发工作流转,可以自动处理由于人员变动的引起的工作滞留.")
		private Integer triggerAfterMinutes = DEFAULT_TRIGGERAFTERMINUTES;

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

		public Integer getTriggerAfterMinutes() {
			return null == this.triggerAfterMinutes ? DEFAULT_TRIGGERAFTERMINUTES : this.triggerAfterMinutes;
		}

		public void setTriggerAfterMinutes(Integer triggerAfterMinutes) {
			this.triggerAfterMinutes = triggerAfterMinutes;
		}

	}

	public void setUrge(Urge urge) {
		this.urge = urge;
	}

	public void setExpire(Expire expire) {
		this.expire = expire;
	}

	public void setDelay(Delay delay) {
		this.delay = delay;
	}

	public void setReorganize(Reorganize reorganize) {
		this.reorganize = reorganize;
	}

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void setMaintenanceIdentity(String maintenanceIdentity) {
		this.maintenanceIdentity = maintenanceIdentity;
	}

}