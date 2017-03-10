package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplication;
import com.x.processplatform.core.entity.element.Application;

class ActionListWithApplicationCategory extends ActionBase {

	ActionResult<List<WrapOutApplication>> execute(EffectivePerson effectivePerson, String applicationCategory)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutApplication>> result = new ActionResult<>();
			List<WrapOutApplication> wraps = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = business.application().listWithPersonWithApplicationCategory(effectivePerson,
					applicationCategory);
			/* 由于有多值字段所以需要全部取出 */
			for (Application o : emc.list(Application.class, ids)) {
				WrapOutApplication wrap = outCopier.copy(o);
				wraps.add(wrap);
			}
			Collections.sort(wraps, new Comparator<WrapOutApplication>() {
				public int compare(WrapOutApplication o1, WrapOutApplication o2) {
					/* ASC */
					return ObjectUtils.compare(o1.getName(), o2.getName(), true);
				}
			});
			result.setData(wraps);
			return result;
		}
	}

}