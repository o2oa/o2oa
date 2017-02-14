package com.x.organization.assemble.control.jaxrs.role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutRole;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Role;

public class ActionListWithGroup extends ActionBase {

	protected List<WrapOutRole> execute(Business business, String groupId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		/* 检查Group是否存在 */
		Group group = emc.find(groupId, Group.class, ExceptionWhen.not_found);
		List<String> groups = new ArrayList<>();
		groups.addAll(business.group().listSupNested(group.getId()));
		/* 将自己加入到查询中 */
		groups.add(groupId);
		Set<String> ids = new HashSet<>();
		for (String str : groups) {
			ids.addAll(business.role().listWithGroup(str));
		}
		List<WrapOutRole> wraps = outCopier.copy(emc.list(Role.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}