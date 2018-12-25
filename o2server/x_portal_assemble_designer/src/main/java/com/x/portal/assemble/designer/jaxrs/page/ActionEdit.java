package com.x.portal.assemble.designer.jaxrs.page;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionEdit extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Page page = emc.find(id, Page.class);
			if (null == page) {
				throw new PageNotExistedException(id);
			}
			Portal portal = emc.find(page.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(page.getPortal());
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Page.class);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wi.copier.copy(wi, page);
			this.checkName(business, page);
			this.checkAlias(business, page);
			emc.check(page, CheckPersistType.all);
			/** 更新首页 */
			if (this.isBecomeFirstPage(business, portal, page)) {
				emc.beginTransaction(Portal.class);
				portal.setFirstPage(page.getId());
			} else if (StringUtils.isEmpty(portal.getFirstPage())
					|| (null == emc.find(portal.getFirstPage(), Page.class))) {
				/* 如果是第一个页面,设置这个页面为当前页面 */
				emc.beginTransaction(Portal.class);
				portal.setFirstPage(page.getId());
			}
			emc.commit();
			ApplicationCache.notify(Page.class);
			ApplicationCache.notify(Portal.class);
			Wo wo = new Wo();
			wo.setId(page.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Page {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Page> copier = WrapCopierFactory.wi(Wi.class, Page.class, null, JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

	}

}