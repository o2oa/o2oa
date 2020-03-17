package com.x.processplatform.assemble.surface.jaxrs.script;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

import net.sf.ehcache.Element;

class ActionGetImported extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetImported.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String applicationFlag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wo wo = new Wo();
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag, applicationFlag);
			Element element = CACHE.get(cacheKey);
			if (null != element && null != element.getObjectValue()) {
				wo = (Wo) element.getObjectValue();
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
				StringBuffer buffer = new StringBuffer("");
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

	public class Wo extends GsonPropertyObject {

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