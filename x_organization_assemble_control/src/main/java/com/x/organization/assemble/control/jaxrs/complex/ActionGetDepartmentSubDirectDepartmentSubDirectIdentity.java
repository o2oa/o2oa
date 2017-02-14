package com.x.organization.assemble.control.jaxrs.complex;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapInListString;
import com.x.base.core.http.WrapOutOnline;
import com.x.base.core.project.x_collaboration_assemble_websocket;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.ThisApplication;
import com.x.organization.assemble.control.wrapout.WrapOutDepartment;
import com.x.organization.assemble.control.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

public class ActionGetDepartmentSubDirectDepartmentSubDirectIdentity {

	private static Type collectionType = new TypeToken<ArrayList<WrapOutOnline>>() {
	}.getType();

	protected static BeanCopyTools<Department, WrapOutDepartment> departmentCopier = BeanCopyToolsBuilder
			.create(Department.class, WrapOutDepartment.class, null, WrapOutDepartment.Excludes);
	protected static BeanCopyTools<Identity, WrapOutIdentity> identityCopier = BeanCopyToolsBuilder
			.create(Identity.class, WrapOutIdentity.class, null, WrapOutIdentity.Excludes);

	protected WrapOutDepartment execute(Business business, String departmentId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Department department = emc.find(departmentId, Department.class, ExceptionWhen.not_found);
		WrapOutDepartment wrap = departmentCopier.copy(department);
		wrap.setDepartmentList(this.listSubDirectDepartment(business, department.getId()));
		wrap.setDepartmentSubDirectCount((long) wrap.getDepartmentList().size());
		wrap.setIdentityList(this.listSubDirectIdentity(business, department.getId()));
		wrap.setIdentitySubDirectCount((long) wrap.getIdentityList().size());
		return wrap;
	}

	private List<WrapOutDepartment> listSubDirectDepartment(Business business, String departmentId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.department().listSubDirect(departmentId);
		List<WrapOutDepartment> wraps = departmentCopier.copy(emc.list(Department.class, ids));
		for (WrapOutDepartment o : wraps) {
			o.setDepartmentSubDirectCount(business.department().countSubDirect(o.getId()));
			o.setIdentitySubDirectCount(business.identity().countSubDirectWithDepartment(o.getId()));
		}
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

	private List<WrapOutIdentity> listSubDirectIdentity(Business business, String departmentId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.identity().listSubDirectWithDepartment(departmentId);
		List<WrapOutIdentity> wraps = identityCopier.copy(emc.list(Identity.class, ids));
		this.fillOnlineStatus(business, wraps);
		return wraps;
	}

	protected void fillPersonName(Business business, WrapOutIdentity wrap) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		wrap.setPersonName(emc.fetchAttribute(wrap.getPerson(), Person.class, "name").getName());
	}

	protected void fillOnlineStatus(Business business, List<WrapOutIdentity> wraps) throws Exception {
		for (WrapOutIdentity o : wraps) {
			/* 先填充person */
			this.fillPersonName(business, o);
			/* 填充全部不在线 */
			o.setOnlineStatus(WrapOutOnline.status_offline);
		}
		List<String> personNames = ListTools.extractProperty(wraps, "personName", String.class, true, true);
		WrapInListString parameters = new WrapInListString();
		parameters.setValueList(personNames);
		List<WrapOutOnline> onlines = ThisApplication.applications.putQuery(x_collaboration_assemble_websocket.class,
				"online/list", parameters, collectionType);
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
