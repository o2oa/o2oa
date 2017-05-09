package com.x.organization.assemble.control.alpha.jaxrs.person;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.set.ListOrderedSet;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;

class ActionListSubNestedWithGroup extends ActionBase {

	protected ActionResult<List<WrapOutPerson>> execute(String groupId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
			Business business = new Business(emc);
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
			this.updateIcon(wraps);
			result.setData(wraps);
			return result;
		}
	}

}
