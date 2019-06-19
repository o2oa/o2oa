package com.x.base.core.project.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class ReportToCenter implements Job {

	public static int INTERVAL = 45;

	private static final String DATAMAP_ATTRIBUTE_CONTEXT = "context";

	private Context context;

	public ReportToCenter() {
	}

	public ReportToCenter(Context context) {
		this.context = context;
	}

	public void execute() throws JobExecutionException {
		Echo echo = this.send(context);
		this.updateApplications(context, echo.getApplicationsToken());
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		this.context = (Context) jobExecutionContext.getMergedJobDataMap().get(DATAMAP_ATTRIBUTE_CONTEXT);
		Echo echo = this.send(context);
		this.updateApplications(context, echo.getApplicationsToken());
	}

	private void updateApplications(Context context, String applicationsToken) {
		if ((null == context.applications())
				|| (!StringUtils.equals(context.applications().getToken(), applicationsToken))) {
			context.loadApplications();
		}
	}

	private Echo send(Context context) throws JobExecutionException {
		try {
			Report report = this.conreteReport(context);
			ActionResponse response = CipherConnectionAction.put(false,
					Config.x_program_centerUrlRoot() + "center/report/application", report);
			return response.getData(Echo.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JobExecutionException(context.clazz() + " send error.", e);
		}
	}

	private Report conreteReport(Context context) throws Exception {
		Report report = new Report();
		report.setClassName(context.clazz().getName());
		report.setContextPath(context.contextPath());
		report.setName(context.name());
		report.setNode(Config.node());
		report.setToken(context.token());
		report.setWeight(context.weight());
		report.setSslEnable(context.sslEnable());
		report.setScheduleLocalRequestList(context.getScheduleLocalRequestList());
		report.setScheduleRequestList(context.getScheduleRequestList());
		return report;
	}

	public static class Report extends GsonPropertyObject {

		private String className;
		private String name;
		private String contextPath;
		private String node;
		private String token;
		private Integer weight;
		private Boolean sslEnable;

		private List<ScheduleLocalRequest> scheduleLocalRequestList = new ArrayList<>();

		private List<ScheduleRequest> scheduleRequestList = new ArrayList<>();

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

		public List<ScheduleLocalRequest> getScheduleLocalRequestList() {
			return scheduleLocalRequestList;
		}

		public void setScheduleLocalRequestList(List<ScheduleLocalRequest> scheduleLocalRequestList) {
			this.scheduleLocalRequestList = scheduleLocalRequestList;
		}

		public List<ScheduleRequest> getScheduleRequestList() {
			return scheduleRequestList;
		}

		public void setScheduleRequestList(List<ScheduleRequest> scheduleRequestList) {
			this.scheduleRequestList = scheduleRequestList;
		}

		public String getContextPath() {
			return contextPath;
		}

		public void setContextPath(String contextPath) {
			this.contextPath = contextPath;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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