package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.HashMap;
import java.util.Map;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoMaxAgeFastETag;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedForm;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedScript;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

abstract class BaseAction extends StandardJaxrsAction {

	CacheCategory cacheCategory = new CacheCategory(Form.class, Script.class, com.x.portal.core.entity.Script.class,
			com.x.cms.core.entity.element.Script.class);

	public static class AbstractWo extends WoMaxAgeFastETag {

		private static final long serialVersionUID = 9043017746047829883L;

		private RelatedForm form;

		private Map<String, RelatedForm> relatedFormMap = new HashMap<>();

		private Map<String, RelatedScript> relatedScriptMap = new HashMap<>();

		public RelatedForm getForm() {
			return form;
		}

		public void setForm(RelatedForm form) {
			this.form = form;
		}

		public Map<String, RelatedForm> getRelatedFormMap() {
			return relatedFormMap;
		}

		public void setRelatedFormMap(Map<String, RelatedForm> relatedFormMap) {
			this.relatedFormMap = relatedFormMap;
		}

		public Map<String, RelatedScript> getRelatedScriptMap() {
			return relatedScriptMap;
		}

		public void setRelatedScriptMap(Map<String, RelatedScript> relatedScriptMap) {
			this.relatedScriptMap = relatedScriptMap;
		}

	}

}