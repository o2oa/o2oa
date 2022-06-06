package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import com.x.base.core.project.annotation.FieldDescribe;

public class Communicate extends ConfigObject {

	private static final long serialVersionUID = -9032410002099116514L;

	public static final Boolean DEFAULT_WSENABLE = true;
//	public static final Boolean DEFAULT_PMSENABLE = true;
//	public static final Boolean DEFAULT_CALENDARENABLE = true;
//	public static final Boolean DEFAULT_UPDATEQUERYTABLE = true;

	public Communicate() {
		this.wsEnable = DEFAULT_WSENABLE;
//		this.pmsEnable = DEFAULT_PMSENABLE;
//		this.calendarEnable = DEFAULT_CALENDARENABLE;
//		this.updateQueryTableEnable = DEFAULT_CALENDARENABLE;
	}

	public static Communicate defaultInstance() {
		return new Communicate();
	}

	@FieldDescribe("是否启用ws消息.")
	private Boolean wsEnable;

//	@FieldDescribe("是否启用pms消息.")
//	private Boolean pmsEnable;
//
//	@FieldDescribe("是否启用calendar消息.")
//	private Boolean calendarEnable;
//
//	@FieldDescribe("是否启用calendar消息.")
//	private Boolean updateQueryTableEnable;

	public Boolean wsEnable() {
		return BooleanUtils.isTrue(this.wsEnable);
	}
//
//	public Boolean pmsEnable() {
//		return BooleanUtils.isTrue(this.pmsEnable);
//	}
//
//	public Boolean calendarEnable() {
//		return BooleanUtils.isTrue(this.calendarEnable);
//	}
//
//	public Boolean updateQueryTableEnable() {
//		return BooleanUtils.isTrue(this.updateQueryTableEnable);
//	}

//	@FieldDescribe("定时触发消息消费队列.")
//	private TriggerMessageConsumeQueue triggerMessageConsumeQueue;
//
//	public TriggerMessageConsumeQueue triggerMessageConsumeQueue() {
//		return this.triggerMessageConsumeQueue == null ? new TriggerMessageConsumeQueue()
//				: this.triggerMessageConsumeQueue;
//	}
//
//	public static class TriggerMessageConsumeQueue extends ConfigObject {
//
//		private static final long serialVersionUID = 1559477154694423422L;
//
//		public static TriggerMessageConsumeQueue defaultInstance() {
//			return new TriggerMessageConsumeQueue();
//		}
//
//		public static final Boolean DEFAULT_ENABLE = true;
//		public static final String DEFAULT_CRON = "20 20 * * * ?"; // 每小时运行一次
//
//		@FieldDescribe("是否启用")
//		private Boolean enable = DEFAULT_ENABLE;
//
//		@FieldDescribe("定时cron表达式")
//		private String cron = DEFAULT_CRON;
//
//		public String getCron() {
//			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
//				return this.cron;
//			} else {
//				return DEFAULT_CRON;
//			}
//		}
//
//		public Boolean getEnable() {
//			return BooleanUtils.isTrue(this.enable);
//		}
//
//		public void setCron(String cron) {
//			this.cron = cron;
//		}
//
//		public void setEnable(Boolean enable) {
//			this.enable = enable;
//		}
//	}

	@FieldDescribe("清理设置.")
	private Clean clean;

	public Clean clean() {
		return this.clean == null ? new Clean() : this.clean;
	}

	public static class Clean extends ConfigObject {
		private static final long serialVersionUID = 1L;

		public static Clean defaultInstance() {
			return new Clean();
		}

		public static final Boolean DEFAULT_ENABLE = true;

		public static final Integer DEFAULT_KEEP = 7;

		public static final String DEFAULT_CRON = "30 30 6 * * ?";

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("消息保留天数")
		private Integer keep = DEFAULT_KEEP;

		public Integer getKeep() {
			if ((null == this.keep) || (this.keep < 1)) {
				return DEFAULT_KEEP;
			} else {
				return this.keep;
			}
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

		public void setCron(String cron) {
			this.cron = cron;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
	}
}