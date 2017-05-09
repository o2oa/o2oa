package com.x.portal.assemble.surface.jaxrs.portal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.wrapout.WrapOutPortal;
import com.x.portal.core.entity.Portal;

class ActionGet extends ActionBase {

	/**
	 * 1.身份在可使用列表中 2.部门在可使用部门中 3.公司在可使用公司中 4.没有限定身份,部门或者公司 5.个人在应用管理员中
	 * 6.是此Portal的创建人员 7.个人有Manage权限 8.个人拥有PortalManager
	 */
	ActionResult<WrapOutPortal> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutPortal> result = new ActionResult<>();
			Portal o = business.portal().pick(id);
			if (null == o) {
				throw new PortalNotExistedException(id);
			}
			if (!business.portal().visible(effectivePerson, o)) {
				throw new PortalAccessDeniedException(effectivePerson.getName(), o.getName(), o.getId());
			}
			WrapOutPortal wrap = outCopier.copy(o);
			result.setData(wrap);
			return result;
		}
	}
}