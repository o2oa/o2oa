package com.x.processplatform.assemble.surface.jaxrs.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

class ActionGetImported extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetImported.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String applicationFlag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wo wo = new Wo();
			CacheCategory cacheCategory = new CacheCategory(Script.class);
			CacheKey cacheKey = new CacheKey(this.getClass(), flag, applicationFlag);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
			} else {
				Business business = new Business(emc);
				Application application = business.application().pick(applicationFlag);
				if (null == application) {
					throw new ExceptionApplicationNotExist(applicationFlag);
				}
				List<Script> list = new ArrayList<>();
				for (Script o : business.script().listScriptNestedWithApplicationWithUniqueName(application, flag)) {
					list.add(o);
				}
				StringBuilder sb = new StringBuilder("");
				List<String> imported = new ArrayList<>();
				for (Script o : list) {
					sb.append(o.getText());
					sb.append(System.lineSeparator());
					imported.add(o.getId());
					if (StringUtils.isNotEmpty(o.getName())) {
						imported.add(o.getName());
					}
					if (StringUtils.isNotEmpty(o.getAlias())) {
						imported.add(o.getAlias());
					}
				}
				wo.setImportedList(imported);
				wo.setText(sb.toString());
				CacheManager.put(cacheCategory, cacheKey, wo);
			}
			result.setData(wo);
			return result;
		}
	}

	public class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -7633183122160854183L;

		@FieldDescribe("脚本内容")
		private String text;

		@FieldDescribe("应用脚本")
		private List<String> importedList;

		public List<String> getImportedList() {
			return importedList;
		}

		public void setImportedList(List<String> importedList) {
			this.importedList = importedList;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

}