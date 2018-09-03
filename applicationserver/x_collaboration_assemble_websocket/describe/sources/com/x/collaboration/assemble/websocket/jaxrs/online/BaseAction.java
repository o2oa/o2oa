package com.x.collaboration.assemble.websocket.jaxrs.online;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Application;
import com.x.base.core.project.x_collaboration_assemble_websocket;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.WrapInStringList;
import com.x.base.core.project.http.WrapOutOnline;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.collaboration.assemble.websocket.ThisApplication;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	Boolean getOnlineLocal(String person) throws Exception {
		Session session = ThisApplication.connections.get(person);
		return (null == session || (!session.isOpen())) ? false : true;
	}

	Boolean getOnLineRemote(String person) throws Exception {
		List<Application> list = ThisApplication.context().applications().get(x_collaboration_assemble_websocket.class);
		if (ListTools.isNotEmpty(list)) {
			for (Application application : list) {
				if (!StringUtils.equals(application.getToken(), ThisApplication.context().token())) {
					WrapOutOnline wrap = CipherConnectionAction
							.get(false, application, "online", "person", person, "local").getData(WrapOutOnline.class);
					if (StringUtils.equals(wrap.getOnlineStatus(), WrapOutOnline.status_online)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	List<WrapOutOnline> listOnlineLocal(WrapInStringList wrapIn) throws Exception {
		List<WrapOutOnline> wraps = new ArrayList<>();
		if (ListTools.isNotEmpty(wrapIn.getValueList())) {
			for (String str : wrapIn.getValueList()) {
				WrapOutOnline o = new WrapOutOnline();
				o.setPerson(str);
				o.setOnlineStatus(WrapOutOnline.status_offline);
				wraps.add(o);
			}
			for (WrapOutOnline o : wraps) {
				Session session = ThisApplication.connections.get(o.getPerson());
				if ((null != session) && (session.isOpen())) {
					o.setOnlineStatus(WrapOutOnline.status_online);
				}
			}
		}
		return wraps;
	}

	List<WrapOutOnline> listOnLineRemote(WrapInStringList wrapIn) throws Exception {
		List<WrapOutOnline> wraps = new ArrayList<>();
		if (ListTools.isNotEmpty(wrapIn.getValueList())) {
			for (String str : wrapIn.getValueList()) {
				WrapOutOnline o = new WrapOutOnline();
				o.setPerson(str);
				o.setOnlineStatus(WrapOutOnline.status_offline);
				wraps.add(o);
			}
			List<Application> list = ThisApplication.context().applications()
					.get(x_collaboration_assemble_websocket.class);
			if (ListTools.isEmpty(list)) {
				for (Application application : list) {
					if (!StringUtils.equals(application.getToken(), ThisApplication.context().token())) {
						List<WrapOutOnline> results = CipherConnectionAction
								.get(false, application, "online", "list", "local").getDataAsList(WrapOutOnline.class);
						for (WrapOutOnline o : results) {
							if (StringUtils.equals(WrapOutOnline.status_online, o.getOnlineStatus())) {
								for (WrapOutOnline wrap : wraps) {
									if (StringUtils.equals(wrap.getPerson(), o.getPerson())) {
										wrap.setOnlineStatus(WrapOutOnline.status_online);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		return wraps;
	}

	List<WrapOutOnline> listOnlineAllLocal() throws Exception {
		List<WrapOutOnline> wraps = new ArrayList<>();
		for (Entry<String, Session> entry : ThisApplication.connections.entrySet()) {
			Session session = entry.getValue();
			if ((null != session) && (session.isOpen())) {
				WrapOutOnline o = new WrapOutOnline();
				o.setPerson(entry.getKey());
				o.setOnlineStatus(WrapOutOnline.status_online);
				wraps.add(o);
			}
		}
		return wraps;
	}

	List<WrapOutOnline> listOnLineAllRemote() throws Exception {
		Set<String> set = new HashSet<>();
		List<Application> list = ThisApplication.context().applications().get(x_collaboration_assemble_websocket.class);
		if (ListTools.isEmpty(list)) {
			for (Application application : list) {
				if (!StringUtils.equals(application.getToken(), ThisApplication.context().token())) {
					List<WrapOutOnline> results = CipherConnectionAction
							.get(false, application, "online", "list", "all", "local")
							.getDataAsList(WrapOutOnline.class);
					for (WrapOutOnline o : results) {
						set.add(o.getPerson());
					}
				}
			}
		}
		List<WrapOutOnline> wraps = new ArrayList<>();
		for (String str : set) {
			WrapOutOnline wrap = new WrapOutOnline();
			wrap.setPerson(str);
			wrap.setOnlineStatus(WrapOutOnline.status_online);
			wraps.add(wrap);
		}
		return wraps;
	}

}
