package com.x.portal.assemble.surface.jaxrs.portal;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.wrapout.WrapOutPortal;
import com.x.portal.core.entity.Portal;

class ActionList extends ActionBase {

	/**
	 * 1.身份在可使用列表中 2.部门在可使用部门中 3.公司在可使用公司中 4.没有限定身份,部门或者公司 5.个人在应用管理员中
	 * 6.是此Portal的创建人员 7.个人有Manage权限 8.个人拥有PortalManager
	 */
	ActionResult<List<WrapOutPortal>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutPortal>> result = new ActionResult<>();
			List<WrapOutPortal> wraps = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = business.portal().list(effectivePerson);
			for (String id : ids) {
				Portal o = business.portal().pick(id);
				if (null == o) {
					throw new PortalNotExistedException(id);
				} else {
					wraps.add(outCopier.copy(o));
				}
			}
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
}