package com.x.portal.assemble.surface.jaxrs.portal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;

class ActionList extends BaseAction {

	/**
	 * 1.身份在可使用列表中 2.部门在可使用部门中 3.公司在可使用公司中 4.没有限定身份,部门或者公司 5.个人在应用管理员中
	 * 6.是此Portal的创建人员 7.个人有Manage权限 8.个人拥有PortalManager
	 */
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = business.portal().list(effectivePerson);
			for (String id : ids) {
				Portal o = business.portal().pick(id);
				if (null == o) {
					throw new ExceptionPortalNotExist(id);
				} else {
					wos.add(Wo.copier.copy(o));
				}
			}
			wos = wos.stream().sorted(Comparator.comparing(Portal::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Portal {

		private static final long serialVersionUID = -5240059905993945729L;
		static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class,
				JpaObject.singularAttributeField(Portal.class, true, false), JpaObject.FieldsInvisible);

	}
}