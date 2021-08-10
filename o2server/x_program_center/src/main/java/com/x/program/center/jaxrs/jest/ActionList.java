package com.x.program.center.jaxrs.jest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Application;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.program.center.ThisApplication;

class ActionList extends BaseAction {

	private static final String JEST_INDEX_HTML = "/jest/index.html";

	ActionResult<List<Wo>> execute(HttpServletRequest request, String source) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		boolean fromProxy = this.formProxy(request, this.getHost(request));
		String httpProtocol = "http://"; // O2LEE，修正如果开启SSL，x_program_center/jest/list.html 给出的URL都是HTTP协议的，无法访问的问题
		for (Entry<String, CopyOnWriteArrayList<Application>> en : ThisApplication.context().applications()
				.entrySet()) {
			Wo wo = new Wo();
			wo.setClassName(en.getKey());
			wo.setUrlList(new ArrayList<>());
			for (Application o : en.getValue()) {
				wo.setName(o.getName());
				if (BooleanUtils.isTrue(o.getSslEnable())) {
					httpProtocol = "https://";
				}

				if (fromProxy) {
					if (this.isUndefindHost(o.getProxyHost())) {
						wo.getUrlList().add(httpProtocol + this.getHost(request) + ":" + o.getProxyPort()
								+ o.getContextPath() + JEST_INDEX_HTML);
					} else {
						wo.getUrlList().add(httpProtocol + o.getProxyHost() + ":" + o.getProxyPort()
								+ o.getContextPath() + JEST_INDEX_HTML);
					}
				} else {
					wo.getUrlList()
							.add(httpProtocol + o.getNode() + ":" + o.getPort() + o.getContextPath() + JEST_INDEX_HTML);
				}
			}
			wos.add(wo);
			wos = wos.stream().sorted(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
		}
		result.setData(wos);
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		private String className;

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		private String name;
		private List<String> urlList;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getUrlList() {
			return urlList;
		}

		public void setUrlList(List<String> urlList) {
			this.urlList = urlList;
		}

	}

}