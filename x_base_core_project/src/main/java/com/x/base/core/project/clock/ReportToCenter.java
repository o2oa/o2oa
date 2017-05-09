package com.x.base.core.project.clock;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.project.Context;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.server.Config;

public class ReportToCenter extends ClockTimerTask {

	public ReportToCenter(Context context) {
		super(context);
	}

	public void execute() throws Exception {
		Echo echo = this.send();
		this.updateApplications(echo.getApplicationsToken());
	}

	private void updateApplications(String applicationsToken) {
		if ((null == context.applications())
				|| (!StringUtils.equals(context.applications().getToken(), applicationsToken))) {
			context.loadApplications();
		}
	}

	private Echo send() throws Exception {
		try {
			Report report = this.conreteReport();
			ActionResponse response = CipherConnectionAction.put(Config.x_program_centerUrlRoot() + "center/report",
					report);
			return response.getData(Echo.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(context.clazz() + " send error.", e);
		}
	}

	private Report conreteReport() throws Exception {
		Report report = new Report();
		report.setClassName(context.clazz().getName());
		report.setNode(Config.node());
		report.setToken(context.token());
		report.setWeight(context.weight());
		report.setSslEnable(context.sslEnable());
		context.timerJobs().stream().forEach(o -> {
			report.getClockTimerList()
					.add(report.new ClockTimer(o.getTimerTaskClassName(), o.getInitialDelay(), o.getDelay()));
		});
		context.scheduleJobs().stream().forEach(o -> {
			report.getClockScheduleList().add(report.new ClockSchedule(o.getTimerTaskClassName(), o.getCron()));
		});
		return report;
	}

	public static class Report extends GsonPropertyObject {

		private String className;
		private String node;
		private String token;
		private Integer weight;
		private Boolean sslEnable;

		private List<ClockTimer> clockTimerList = new ArrayList<>();

		private List<ClockSchedule> clockScheduleList = new ArrayList<>();

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

		public Boolean getSslEnable() {
			return sslEnable;
		}

		public void setSslEnable(Boolean sslEnable) {
			this.sslEnable = sslEnable;
		}

		public class ClockTimer {

			public ClockTimer(String clockTaskClassName, Integer initialDelay, Integer delay) {
				this.clockTaskClassName = clockTaskClassName;
				this.initialDelay = initialDelay;
				this.delay = delay;
			}

			private String clockTaskClassName;
			private Integer initialDelay;
			private Integer delay;

			public String getClockTaskClassName() {
				return clockTaskClassName;
			}

			public void setClockTaskClassName(String clockTaskClassName) {
				this.clockTaskClassName = clockTaskClassName;
			}

			public Integer getInitialDelay() {
				return initialDelay;
			}

			public void setInitialDelay(Integer initialDelay) {
				this.initialDelay = initialDelay;
			}

			public Integer getDelay() {
				return delay;
			}

			public void setDelay(Integer delay) {
				this.delay = delay;
			}
		}

		public class ClockSchedule {

			public ClockSchedule(String clockTaskClassName, String cron) {
				this.clockTaskClassName = clockTaskClassName;
				this.cron = cron;
			}

			private String clockTaskClassName;
			private String cron;

			public String getClockTaskClassName() {
				return clockTaskClassName;
			}

			public void setClockTaskClassName(String clockTaskClassName) {
				this.clockTaskClassName = clockTaskClassName;
			}

			public String getCron() {
				return cron;
			}

			public void setCron(String cron) {
				this.cron = cron;
			}

		}

		public List<ClockTimer> getClockTimerList() {
			return clockTimerList;
		}

		public void setClockTimerList(List<ClockTimer> clockTimerList) {
			this.clockTimerList = clockTimerList;
		}

		public List<ClockSchedule> getClockScheduleList() {
			return clockScheduleList;
		}

		public void setClockScheduleList(List<ClockSchedule> clockScheduleList) {
			this.clockScheduleList = clockScheduleList;
		}

	}

	public static class Echo extends GsonPropertyObject {

		private String applicationsToken;

		public String getApplicationsToken() {
			return applicationsToken;
		}

		public void setApplicationsToken(String applicationsToken) {
			this.applicationsToken = applicationsToken;
		}

	}

}