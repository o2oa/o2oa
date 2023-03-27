package com.x.cms.assemble.control.jaxrs.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoMaxAgeFastETag;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class BaseAction extends StandardJaxrsAction {

	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(Form.class, View.class, ViewFieldConfig.class, ViewCategory.class,
			Script.class, com.x.processplatform.core.entity.element.Script.class, com.x.portal.core.entity.Script.class,
			com.x.program.center.core.entity.Script.class);

	protected LogService logService = new LogService();

	protected static final String FORM_OPEN_MODE_READ = "read";

	protected List<String> convertScriptToCacheTag(Business business, Map<String, String> map) throws Exception {
		List<String> list = new ArrayList<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			switch (entry.getValue()) {
				case RelatedScript.TYPE_PROCESS_PLATFORM:
					com.x.processplatform.core.entity.element.Script pp = business.process().script().pick(entry.getKey());
					if (null != pp) {
						list.add(pp.getId() + pp.getUpdateTime().getTime());
					}
					break;
				case RelatedScript.TYPE_CMS:
					Script cms = business.getScriptFactory().pick(entry.getKey());
					if (null != cms) {
						list.add(cms.getId() + cms.getUpdateTime().getTime());
					}
					break;
				case RelatedScript.TYPE_PORTAL:
					com.x.portal.core.entity.Script p = business.portal().script().pick(entry.getKey());
					if (null != p) {
						list.add(p.getId() + p.getUpdateTime().getTime());
					}
					break;
				case RelatedScript.TYPE_SERVICE:
					com.x.program.center.core.entity.Script cs = business.centerService().script().pick(entry.getKey());
					if (null != cs) {
						list.add(cs.getId() + cs.getUpdateTime().getTime());
					}
					break;
				default:
					break;
			}
		}
		return list;
	}

	public static class AbstractWo extends WoMaxAgeFastETag {

		private static final long serialVersionUID = 4473155051937494454L;

		private RelatedForm form;

		private Map<String, RelatedForm> relatedFormMap = new HashMap<>();

		private Map<String, RelatedScript> relatedScriptMap = new HashMap<>();

		public RelatedForm getForm() {
			return form;
		}

		public void setForm(RelatedForm form) {
			this.form = form;
		}

		public Map<String, RelatedScript> getRelatedScriptMap() {
			return relatedScriptMap;
		}

		public void setRelatedScriptMap(Map<String, RelatedScript> relatedScriptMap) {
			this.relatedScriptMap = relatedScriptMap;
		}

		public Map<String, RelatedForm> getRelatedFormMap() {
			return relatedFormMap;
		}

		public void setRelatedFormMap(Map<String, RelatedForm> relatedFormMap) {
			this.relatedFormMap = relatedFormMap;
		}
	}

	public static class RelatedForm extends GsonPropertyObject {

		private static final long serialVersionUID = 3099673570508709007L;

		public RelatedForm() {
		}

		public RelatedForm(Form form, String data) {
			this.id = form.getId();
			this.alias = form.getAlias();
			this.name = form.getName();
			this.description = form.getDescription();
			this.appId = form.getAppId();
			this.hasMobile = form.getHasMobile();
			this.data = data;
		}

		private String id;
		private String alias;
		private String name;
		private String description;
		private String appId;
		private Boolean hasMobile;
		private String data;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
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

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static class RelatedScript extends GsonPropertyObject {

		private static final long serialVersionUID = -4465416667636529080L;

		public static final String TYPE_PROCESS_PLATFORM = "processPlatform";
		public static final String TYPE_CMS = "cms";
		public static final String TYPE_PORTAL = "portal";
		public static final String TYPE_SERVICE = "service";

		public RelatedScript() {
		}

		public RelatedScript(String id, String name, String alias, String text, String type) {
			this.id = id;
			this.alias = alias;
			this.name = name;
			this.text = text;
			this.type = type;
		}

		private String type;

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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}
}
