package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import com.x.base.core.project.annotation.FieldDescribe;

public class Communicate extends ConfigObject {

	public static final Boolean DEFAULT_WEBSOCKETENABLE = true;

	public Communicate() {
		this.webSocketEnable = DEFAULT_WEBSOCKETENABLE;
	}

	public static Communicate defaultInstance() {
		return new Communicate();
	}

	@FieldDescribe("是否启用webSocket推送消息.")
	private Boolean webSocketEnable;

	public Boolean webSocketEnable() {
		return BooleanUtils.isTrue(webSocketEnable);
	}

	@FieldDescribe("清理设置.")
	private Clean clean;

	public Clean clean() {
		return this.clean == null ? new Clean() : this.clean;
	}

	public static class Clean extends ConfigObject {

		public static Clean defaultInstance() {
			Clean o = new Clean();
			return o;
		}

		public final static Boolean DEFAULT_ENABLE = true;

		public final static Integer DEFAULT_KEEP = 7;

		public final static String DEFAULT_CRON = "30 30 6 * * ?";

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