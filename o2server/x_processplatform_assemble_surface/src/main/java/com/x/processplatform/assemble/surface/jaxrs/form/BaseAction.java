package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected static Ehcache CACHE = ApplicationCache.instance().getCache(Form.class, Script.class,
			com.x.portal.core.entity.Script.class, com.x.cms.core.entity.element.Script.class);

	protected WoForm toWoFormDataOrMobileData(Form form) {
		WoForm wo = new WoForm();
		wo.setId(form.getId());
		wo.setName(form.getName());
		wo.setAlias(form.getAlias());
		wo.setCategory(form.getCategory());
		wo.setApplication(form.getApplication());
		wo.setHasMobile(form.getHasMobile());
		wo.setData(form.getDataOrMobileData());
		return wo;
	}

	protected WoForm toWoFormMobileDataOrData(Form form) {
		WoForm wo = new WoForm();
		wo.setId(form.getId());
		wo.setName(form.getName());
		wo.setAlias(form.getAlias());
		wo.setCategory(form.getCategory());
		wo.setApplication(form.getApplication());
		wo.setHasMobile(form.getHasMobile());
		wo.setData(form.getMobileDataOrData());
		return wo;
	}

	protected WoScript toWoScript(Script script) {
		WoScript wo = new WoScript();
		wo.setId(script.getId());
		wo.setName(script.getName());
		wo.setAlias(script.getAlias());
		wo.setText(script.getText());
		return wo;
	}

	protected WoScript toWoScript(com.x.cms.core.entity.element.Script script) {
		WoScript wo = new WoScript();
		wo.setId(script.getId());
		wo.setName(script.getName());
		wo.setAlias(script.getAlias());
		wo.setText(script.getText());
		return wo;
	}

	protected WoScript toWoScript(com.x.portal.core.entity.Script script) {
		WoScript wo = new WoScript();
		wo.setId(script.getId());
		wo.setName(script.getName());
		wo.setAlias(script.getAlias());
		wo.setText(script.getText());
		return wo;
	}

	protected WoScript toWoScript(WorkCompletedProperties.Script script) {
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
		private String category;
		private String application;
		private Boolean hasMobile;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public Boolean getHasMobile() {
			return hasMobile;
		}

		public void setHasMobile(Boolean hasMobile) {
			this.hasMobile = hasMobile;
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

	public static class AbstractWo extends GsonPropertyObject {

		private String id;

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

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		
	}

}