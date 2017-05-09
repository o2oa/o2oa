package com.x.organization.assemble.control.alpha.jaxrs.identity;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.http.WrapInStringList;
import com.x.base.core.http.WrapOutOnline;
import com.x.base.core.project.x_collaboration_assemble_websocket;
import com.x.base.core.utils.ListTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.ThisApplication;
import com.x.organization.assemble.control.alpha.wrapin.WrapInIdentity;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

public class ActionBase {

	protected static BeanCopyTools<Identity, WrapOutIdentity> outCopier = BeanCopyToolsBuilder.create(Identity.class,
			WrapOutIdentity.class, null, WrapOutIdentity.Excludes);

	protected static BeanCopyTools<WrapInIdentity, Identity> inCopier = BeanCopyToolsBuilder
			.create(WrapInIdentity.class, Identity.class, null, WrapInIdentity.Excludes);

	protected void fillDepartmentName(Business business, WrapOutIdentity wrap) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		wrap.setDepartmentName(emc.fetchAttribute(wrap.getDepartment(), Department.class, "name").getName());
	}

	protected void fillPersonName(Business business, WrapOutIdentity wrap) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		wrap.setPersonName(emc.fetchAttribute(wrap.getPerson(), Person.class, "name").getName());
	}

	protected void fillOnlineStatus(Business business, WrapOutIdentity wrap) throws Exception {
		this.fillPersonName(business, wrap);
		wrap.setOnlineStatus(WrapOutOnline.status_offline);
		WrapOutOnline online = ThisApplication.context().applications()
				.getQuery(x_collaboration_assemble_websocket.class,
						"online/person/" + URLEncoder.encode(wrap.getPersonName(), DefaultCharset.name))
				.getData(WrapOutOnline.class);
		wrap.setOnlineStatus(online.getOnlineStatus());
	}

	protected void fillOnlineStatus(Business business, List<WrapOutIdentity> wraps) throws Exception {
		for (WrapOutIdentity o : wraps) {
			/* 先填充person */
			this.fillPersonName(business, o);
			/* 填充全部不在线 */
			o.setOnlineStatus(WrapOutOnline.status_offline);
		}
		List<String> personNames = ListTools.extractProperty(wraps, "personName", String.class, true, true);
		WrapInStringList parameters = new WrapInStringList();
		parameters.setValueList(personNames);
		List<WrapOutOnline> onlines = ThisApplication.context().applications()
				.putQuery(x_collaboration_assemble_websocket.class, "online/list", parameters)
				.getDataAsList(WrapOutOnline.class);
		for (WrapOutOnline o : onlines) {
			if (StringUtils.equals(o.getOnlineStatus(), WrapOutOnline.status_online)) {
				for (WrapOutIdentity wrap : wraps) {
					if (StringUtils.equals(wrap.getPersonName(), o.getPerson())) {
						wrap.setOnlineStatus(WrapOutOnline.status_online);
					}
				}
			}
		}
	}
}