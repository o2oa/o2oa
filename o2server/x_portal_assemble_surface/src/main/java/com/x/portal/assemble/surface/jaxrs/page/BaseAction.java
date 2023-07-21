package com.x.portal.assemble.surface.jaxrs.page;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoMaxAgeFastETag;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;

abstract class BaseAction extends StandardJaxrsAction {

	CacheCategory pageCache = new CacheCategory(Page.class);

	protected boolean isNotLoginPage(String id) throws Exception {
		return !(Config.portal().getIndexPage().getEnable()
				&& StringUtils.equals(id, Config.portal().getIndexPage().getPage()));
	}

	CacheCategory cacheCategory = new CacheCategory(Page.class, Widget.class, Script.class, com.x.processplatform.core.entity.element.Script.class,
			com.x.cms.core.entity.element.Script.class, com.x.program.center.core.entity.Script.class);

	public static class AbstractWo extends WoMaxAgeFastETag {

		private static final long serialVersionUID = -246989645483328034L;

		private RelatedPage page;

		private Map<String, RelatedWidget> relatedWidgetMap = new HashMap<>();

		private Map<String, RelatedScript> relatedScriptMap = new HashMap<>();

		public RelatedPage getPage() {
			return page;
		}

		public void setPage(RelatedPage page) {
			this.page = page;
		}

		public Map<String, RelatedWidget> getRelatedWidgetMap() {
			return relatedWidgetMap;
		}

		public void setRelatedWidgetMap(Map<String, RelatedWidget> relatedWidgetMap) {
			this.relatedWidgetMap = relatedWidgetMap;
		}

		public Map<String, RelatedScript> getRelatedScriptMap() {
			return relatedScriptMap;
		}

		public void setRelatedScriptMap(Map<String, RelatedScript> relatedScriptMap) {
			this.relatedScriptMap = relatedScriptMap;
		}

	}

	public static class RelatedPage extends GsonPropertyObject {

		private static final long serialVersionUID = 7525243539062813646L;

		public RelatedPage() {

		}

		public RelatedPage(Page page, String data) {
			this.id = page.getId();
			this.alias = page.getAlias();
			this.name = page.getName();
			this.description = page.getDescription();
			this.portal = page.getPortal();
			this.hasMobile = page.getHasMobile();
			this.data = data;
		}

		private String id;
		private String alias;
		private String name;
		private String description;
		private String portal;
		private Boolean hasMobile;
		private String data;

		public String getPortal() {
			return portal;
		}

		public void setPortal(String portal) {
			this.portal = portal;
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

	public static class RelatedWidget extends GsonPropertyObject {

		private static final long serialVersionUID = -6649777538891528958L;

		public RelatedWidget() {

		}

		public RelatedWidget(Widget widget, String data) {
			this.id = widget.getId();
			this.alias = widget.getAlias();
			this.name = widget.getName();
			this.portal = widget.getPortal();
			this.hasMobile = widget.getHasMobile();
			this.data = data;
		}

		private String id;
		private String alias;
		private String name;
		private String portal;
		private Boolean hasMobile;
		private String data;

		public String getPortal() {
			return portal;
		}

		public void setPortal(String portal) {
			this.portal = portal;
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

	public static class RelatedScript extends GsonPropertyObject {

		private static final long serialVersionUID = -4465416667636529080L;

		public static final String TYPE_PROCESSPLATFORM = "processPlatform";
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
