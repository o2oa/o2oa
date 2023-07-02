package com.x.portal.assemble.surface.jaxrs.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

class ActionGetImported extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String portalId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			CacheKey cacheKey = new CacheKey(this.getClass(), flag, portalId);
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
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
				wo.setPortal(portal.getId());
				wo.setPortalName(portal.getName());
				wo.setPortalAlias(portal.getAlias());
				CacheManager.put(cache, cacheKey, wo);
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

		@FieldDescribe("门户应用名称.")
		private String portalName;

		@FieldDescribe("门户应用别名.")
		private String portalAlias;

		public String getPortalName() {
			return portalName;
		}

		public void setPortalName(String portalName) {
			this.portalName = portalName;
		}

		public String getPortalAlias() {
			return portalAlias;
		}

		public void setPortalAlias(String portalAlias) {
			this.portalAlias = portalAlias;
		}
	}

}
