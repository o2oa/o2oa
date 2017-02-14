package com.x.organization.assemble.control.jaxrs.person;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.set.ListOrderedSet;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;

public class ActionListSubNestedWithGroup extends ActionBase {

	protected List<WrapOutPerson> execute(Business business, String groupId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		/* 检查group是否存在 */
		Group group = emc.find(groupId, Group.class, ExceptionWhen.not_found);
		List<String> ids = new ArrayList<>();
		ids.addAll(business.group().listSubNested(group.getId()));
		/* 将当前群组也加入到需要搜索成员的群组中 */
		ids.add(group.getId());
		ListOrderedSet<String> set = new ListOrderedSet<>();
		for (String str : ids) {
			set.addAll(emc.find(str, Group.class).getPersonList());
		}
		List<WrapOutPerson> wraps = outCopier.copy(emc.list(Person.class, set));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
