package com.x.portal.assemble.surface.jaxrs.script;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

import net.sf.ehcache.Element;

class ActionGetImported extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String portalId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag, portalId);
			Element element = CACHE.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wo = (Wo) element.getObjectValue();
			} else {
				Business business = new Business(emc);
				Portal portal = business.portal().pick(portalId);
				if (null == portal) {
					throw new ExceptionPortalNotExist(portalId);
				}
				if (!business.portal().visible(effectivePerson, portal)) {
					throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
							portal.getId());
				}
				List<Script> list = new ArrayList<>();
				for (Script o : business.script().listScriptNestedWithPortalWithFlag(portal, flag)) {
					list.add(o);
				}
				StringBuffer buffer = new StringBuffer();
				List<String> imported = new ArrayList<>();
				for (Script o : list) {
					buffer.append(o.getText());
					buffer.append(System.lineSeparator());
					imported.add(o.getId());
					if (StringUtils.isNotEmpty(o.getName())) {
						imported.add(o.getName());
					}
					if (StringUtils.isNotEmpty(o.getAlias())) {
						imported.add(o.getAlias());
					}
				}
				wo.setImportedList(imported);
				wo.setText(buffer.toString());
				CACHE.put(new Element(cacheKey, wo));
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = -8067704098385000667L;

		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		private List<String> importedList;

		public List<String> getImportedList() {
			return importedList;
		}

		public void setImportedList(List<String> importedList) {
			this.importedList = importedList;
		}
	}

}
