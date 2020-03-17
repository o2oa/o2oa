package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionListSummary extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = this.listEditable(business, effectivePerson);
			List<Wo> wos = Wo.copier.copy(emc.list(Portal.class, ids));
			/* 由于有多值字段所以需要全部取出 */
			for (Wo wo : wos) {
				List<String> os = business.page().listWithPortal(wo.getId());
				// wo.setPageList(WoPage.copier.copy(emc.list(Page.class, os)));
				wo.setPageList(emc.fetch(os, WoPage.copier));
			}
			wos = wos.stream().sorted(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Portal {

		private static final long serialVersionUID = -4436126455534548272L;

		static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class,
				JpaObject.singularAttributeField(Portal.class, true, false), null);

		@FieldDescribe("页面")
		private List<WoPage> pageList = new ArrayList<>();

		public List<WoPage> getPageList() {
			return pageList;
		}

		public void setPageList(List<WoPage> pageList) {
			this.pageList = pageList;
		}
	}

	public static class WoPage extends Page {

		private static final long serialVersionUID = -9051259904153066895L;
		static WrapCopier<Page, WoPage> copier = WrapCopierFactory.wo(Page.class, WoPage.class,
				JpaObject.singularAttributeField(Page.class, true, true), null);
	}

}