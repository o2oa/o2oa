package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

class ActionGetWithWorkOrWorkCompletedMobile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetWithWorkOrWorkCompletedMobile.class);

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Audit audit = logger.audit(effectivePerson);
			ActionResult<JsonElement> result = new ActionResult<>();

			Business business = new Business(emc);

			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
					new ExceptionEntityNotExist(workOrWorkCompleted))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			JsonElement wo = null;

			Work work = emc.find(workOrWorkCompleted, Work.class);

			if (null != work) {
				wo = gson.toJsonTree(this.work(business, work));
			} else {
				wo = gson.toJsonTree(this.workCompleted(business, emc.flag(workOrWorkCompleted, WorkCompleted.class)));
			}
			audit.log(null, "查看");
			result.setData(wo);
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
				wo.setForm(toWoMobileForm(form));
				related(business, wo, form);
			}
		}
		return wo;
	}

	private void related(Business business, Wo wo, Form form) throws Exception {
		if (StringUtils.isNotBlank(form.getMobileData())) {
			for (String relatedFormId : form.getMobileRelatedFormList()) {
				Form relatedForm = business.form().pick(relatedFormId);
				if (null != relatedForm) {
					wo.getRelatedFormMap().put(relatedFormId, toWoMobileForm(relatedForm));
				}
			}
		} else {
			for (String relatedFormId : form.getRelatedFormList()) {
				Form relatedForm = business.form().pick(relatedFormId);
				if (null != relatedForm) {
					wo.getRelatedFormMap().put(relatedFormId, toWoForm(relatedForm));
				}
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
				wo.setForm(toWoMobileForm(form));
				related(business, wo, form);
			}
		} else {
			if (null != workCompleted.getProperties().getForm()) {
				wo.form = toWoMobileForm(workCompleted.getProperties().getForm());
			}
			if (StringUtils.isNotBlank(workCompleted.getProperties().getForm().getMobileData())) {
				for (Form f : workCompleted.getProperties().getMobileRelatedFormList()) {
					wo.getRelatedFormMap().put(f.getId(), toWoMobileForm(f));
				}
			} else {
				for (Form f : workCompleted.getProperties().getRelatedFormList()) {
					wo.getRelatedFormMap().put(f.getId(), toWoForm(f));
				}
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
		wo.setName(form.getName());
		wo.setAlias(form.getAlias());
		wo.setData(form.getDataOrMobileData());
		return wo;
	}

	private WoForm toWoMobileForm(Form form) {
		WoForm wo = new WoForm();
		wo.setId(form.getId());
		wo.setName(form.getName());
		wo.setAlias(form.getAlias());
		wo.setData(form.getMobileDataOrData());
		return wo;
	}

	private WoScript toWoScript(Script script) {
		WoScript wo = new WoScript();
		wo.setId(script.getId());
		wo.setName(script.getName());
		wo.setAlias(script.getAlias());
		wo.setText(script.getText());
		return wo;
	}

	public static class WoForm extends GsonPropertyObject {
		private String id;
		private String alias;
		private String name;
		private String data;

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

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
		private String alias;
		private String name;
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

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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