package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

import net.sf.ehcache.Element;

class V2GetWithWorkOrWorkCompleted extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2GetWithWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
					new ExceptionEntityNotExist(workOrWorkCompleted))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = null;
			CacheCategory cacheCategory = new CacheCategory(Form.class, Script.class);
			CacheKey cacheKey = new CacheKey(this.getClass(), workOrWorkCompleted);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
			} else {
				Work work = emc.find(workOrWorkCompleted, Work.class);
				if (null != work) {
					wo = this.work(business, work);
				} else {
					wo = this.workCompleted(business, emc.flag(workOrWorkCompleted, WorkCompleted.class));
				}
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	private Wo work(Business business, Work work) throws Exception {
		Wo wo = new Wo();
		String id = work.getForm();
		if (StringUtils.isEmpty(id)) {
			Activity activity = business.getActivity(work);
			id = PropertyTools.getOrElse(activity, Activity.form_FIELDNAME, String.class, "");
		}
		if (StringUtils.isNotEmpty(id)) {
			Form form = business.form().pick(id);
			if (null != form) {
				wo.setForm(toWoForm(form));
				related(business, wo, form);
			}
		}
		return wo;
	}

	private void related(Business business, Wo wo, Form form) throws Exception {
		for (String relatedFormId : form.getRelatedFormList()) {
			Form relatedForm = business.form().pick(relatedFormId);
			if (null != relatedForm) {
				wo.getRelatedFormMap().put(relatedFormId, toWoForm(relatedForm));
			}
		}
		for (String relatedScriptId : form.getRelatedScriptList()) {
			Script relatedScript = business.script().pick(relatedScriptId);
			if (null != relatedScript) {
				wo.getRelatedScriptMap().put(relatedScriptId, toWoScript(relatedScript));
			}
		}
	}

	private Wo workCompleted(Business business, WorkCompleted workCompleted) throws Exception {
		Wo wo = new Wo();
		// 先使用当前库的表单,如果不存在使用储存的表单.
		if (StringUtils.isNotEmpty(workCompleted.getForm())) {
			Form form = business.form().pick(workCompleted.getForm());
			if (null != form) {
				wo.setForm(toWoForm(form));
				related(business, wo, form);
			}
		} else {
			if (null != workCompleted.getProperties().getForm()) {
				wo.form = toWoForm(workCompleted.getProperties().getForm());
			}
			for (Form f : workCompleted.getProperties().getRelatedFormList()) {
				wo.getRelatedFormMap().put(f.getId(), toWoForm(f));
			}
			for (Script s : workCompleted.getProperties().getRelatedScriptList()) {
				wo.getRelatedScriptMap().put(s.getId(), toWoScript(s));
			}
		}
		return wo;
	}

	private WoForm toWoForm(Form form) {
		WoForm wo = new WoForm();
		wo.setId(form.getId());
		wo.setData(form.getDataOrMobileData());
		return wo;
	}

	private WoScript toWoScript(Script script) {
		WoScript wo = new WoScript();
		wo.setId(script.getId());
		wo.setText(script.getText());
		return wo;
	}

	public static class WoForm extends GsonPropertyObject {
		private String id;
		private String data;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}

	public static class WoScript extends GsonPropertyObject {

		private String id;

		private String text;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

	public static class Wo extends GsonPropertyObject {

		private WoForm form;

		private Map<String, WoForm> relatedFormMap = new HashMap<>();

		private Map<String, WoScript> relatedScriptMap = new HashMap<>();

		public WoForm getForm() {
			return form;
		}

		public void setForm(WoForm form) {
			this.form = form;
		}

		public Map<String, WoForm> getRelatedFormMap() {
			return relatedFormMap;
		}

		public void setRelatedFormMap(Map<String, WoForm> relatedFormMap) {
			this.relatedFormMap = relatedFormMap;
		}

		public Map<String, WoScript> getRelatedScriptMap() {
			return relatedScriptMap;
		}

		public void setRelatedScriptMap(Map<String, WoScript> relatedScriptMap) {
			this.relatedScriptMap = relatedScriptMap;
		}

	}
}