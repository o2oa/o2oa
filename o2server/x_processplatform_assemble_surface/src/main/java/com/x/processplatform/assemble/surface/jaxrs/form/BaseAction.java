package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoMaxAgeFastETag;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedForm;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedScript;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

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

	protected List<String> convertScriptToCacheTag(Business business, Map<String, String> map) throws Exception {
		List<String> list = new ArrayList<>();
		for (Entry<String, String> entry : map.entrySet()) {
			switch (entry.getValue()) {
			case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
				Script pp = business.script().pick(entry.getKey());
				if (null != pp) {
					list.add(pp.getId() + pp.getUpdateTime().getTime());
				}
				break;
			case WorkCompletedProperties.RelatedScript.TYPE_CMS:
				com.x.cms.core.entity.element.Script cms = business.cms().script().pick(entry.getKey());
				if (null != cms) {
					list.add(cms.getId() + cms.getUpdateTime().getTime());
				}
				break;
			case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
				com.x.portal.core.entity.Script p = business.portal().script().pick(entry.getKey());
				if (null != p) {
					list.add(p.getId() + p.getUpdateTime().getTime());
				}
				break;
			default:
				break;
			}
		}
		return list;
	}

}