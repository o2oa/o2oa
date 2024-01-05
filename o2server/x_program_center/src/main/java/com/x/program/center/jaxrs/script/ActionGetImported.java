package com.x.program.center.jaxrs.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Script;

class ActionGetImported extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			CacheKey cacheKey = new CacheKey(this.getClass(), flag);
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
			} else {
				Business business = new Business(emc);
				List<Script> list = new ArrayList<>();
				for (Script o : business.script().listScriptNestedWithFlag(flag)) {
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
				CacheManager.put(cache, cacheKey, wo);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = -2925115682200732092L;

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
