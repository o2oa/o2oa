package com.x.common.core.application.task;

import org.apache.commons.lang3.StringUtils;

import com.x.common.core.application.AbstractThisApplication;
import com.x.common.core.application.communication.Report;
import com.x.common.core.application.communication.ReportEcho;
import com.x.common.core.application.component.x_program_center;
import com.x.common.core.application.configuration.application.Applications;

public class ReportTask implements Runnable {

	public void run() {
		try {
			ReportEcho echo = this.send();
			this.updateApplications(echo.getApplicationsToken());
		} catch (Exception e) {
			System.out.println(AbstractThisApplication.name + " report error:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void updateApplications(String applicationsToken) throws Exception {
		if (!StringUtils.equals(AbstractThisApplication.applications.getToken(), applicationsToken)) {
			AbstractThisApplication.applications = AbstractThisApplication.loadCenterObject("applications",
					Applications.class);
		}
	}

	private ReportEcho send() throws Exception {
		try {
			Report report = this.conreteReport();
			return AbstractThisApplication.applications.putQuery(x_program_center.class, "center/report", report,
					ReportEcho.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(AbstractThisApplication.name + " send report error.", e);
		}
	}

	private Report conreteReport() throws Exception {
		if (null == AbstractThisApplication.clazz || StringUtils.isEmpty(AbstractThisApplication.clazz.getName())) {
			throw new Exception("can not get ThisApplication.clazz");
		}
		if (null == AbstractThisApplication.config) {
			throw new Exception("can not get ThisApplication.config");
		}
		if (StringUtils.isEmpty(AbstractThisApplication.token)) {
			throw new Exception("can not get ThisApplication.token");
		}
		Report report = new Report();
		report.setClassName(AbstractThisApplication.clazz.getName());
		report.setApplicationServer(AbstractThisApplication.config.getApplicationServer());
		report.setToken(AbstractThisApplication.token);
		return report;
	}
}