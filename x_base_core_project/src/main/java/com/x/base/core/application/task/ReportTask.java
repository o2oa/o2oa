package com.x.base.core.application.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.Applications;
import com.x.base.core.bean.NameValuePair;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.connection.HttpConnection;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.Report;
import com.x.base.core.project.ReportEcho;
import com.x.base.core.project.server.ApplicationServer.NameWeightPair;
import com.x.base.core.project.server.Config;

public class ReportTask implements Runnable {

	public void run() {
		try {
			ReportEcho echo = this.send();
			this.updateApplications(echo.getApplicationsToken());
		} catch (Exception e) {
			System.out.println(AbstractThisApplication.clazz + " run error:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void updateApplications(String applicationsToken) throws Exception {
		try {
			if ((null == AbstractThisApplication.applications)
					|| (!StringUtils.equals(AbstractThisApplication.applications.getToken(), applicationsToken))) {
				AbstractThisApplication.applications = AbstractThisApplication.getFromCenter("/jaxrs/applications",
						Applications.class);
			}
		} catch (Exception e) {
			System.out.println(AbstractThisApplication.clazz + " updateApplications error:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private ReportEcho send() throws Exception {
		try {
			Report report = this.conreteReport();
			EffectivePerson effectivePerson = EffectivePerson.cipher( Config.token().getCipher());
			String url = AbstractThisApplication.getCenterUrl() + "/jaxrs/center/report";
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair(HttpToken.X_Token, effectivePerson.getToken()));
			return HttpConnection.putAsObject(url, heads, report.toString(), ReportEcho.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(AbstractThisApplication.clazz + " send error.", e);
		}
	}

	private Report conreteReport() throws Exception {
		Report report = new Report();
		report.setClassName(AbstractThisApplication.clazz.getName());
		report.setNode(Config.node());
		report.setToken(AbstractThisApplication.token);
		report.setWeight(this.getWeight());
		return report;
	}

	private Integer getWeight() throws Exception {
		Integer weight = null;
		for (NameWeightPair o : Config.currentNode().getApplication().getProjects()) {
			if (StringUtils.equals(o.getName(), AbstractThisApplication.clazz.getSimpleName())) {
				weight = o.getWeight();
				break;
			}
		}
		return weight;
	}
}