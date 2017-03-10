package com.x.organization.assemble.control.jaxrs.identity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;

public class ActionListLikeWithCompanySubNestedDepartmentSubNested extends ActionBase {

	protected List<WrapOutIdentity> execute(Business business, String key,
			WrapInCompanySubNestedDepartmentSubNested wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Set<String> departments = new HashSet<>();
		if (null != wrapIn.getCompanyList()) {
			/* 查询指定公司的所有部门，加入到搜索范围 */
			for (String str : wrapIn.getCompanyList()) {
				departments.addAll(business.department().listSubNestedWithCompany(str));
			}
		}
		if (null != wrapIn.getDepartmentList()) {
			for (String str : wrapIn.getDepartmentList()) {
				/* 把查询部门加入到搜索范围内 */
				departments.add(str);
				/* 把查询部门的子部门加入到搜索范围内 */
				departments.addAll(business.department().listSubNested(str));
			}
		}
		List<String> ids = business.identity().listLikeWithDepartment(departments, key);
		List<WrapOutIdentity> wraps = outCopier.copy(emc.list(Identity.class, ids));
		SortTools.asc(wraps, false, "name");
		/* 将depatmentName扩展到WrapOutIdentity */
		for (WrapOutIdentity o : wraps) {
			o.setDepartmentName(emc.fetchAttribute(o.getDepartment(), Department.class, "name").getName());
		}
		this.fillOnlineStatus(business, wraps);
		return wraps;
	}

}
