package o2.collect.assemble.jaxrs.module;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class BriefModule extends GsonPropertyObject {

	private String name;
	private String description;
	private String category;
	private String icon;
	private String id;

	private List<BriefProcessPlatform> processPlatformList = new ArrayList<>();

	private List<BriefPortal> portalList = new ArrayList<>();

	private List<BriefCms> cmsList = new ArrayList<>();

	private List<BriefQuery> queryList = new ArrayList<>();

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<BriefProcessPlatform> getProcessPlatformList() {
		return processPlatformList;
	}

	public void setProcessPlatformList(List<BriefProcessPlatform> processPlatformList) {
		this.processPlatformList = processPlatformList;
	}

	public static class BriefProcessPlatform extends GsonPropertyObject {

		private String name;
		private String alias;
		private String id;
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

	public static class BriefPortal extends GsonPropertyObject {

		private String name;
		private String alias;
		private String id;
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

	public static class BriefCms extends GsonPropertyObject {
		private String name;
		private String alias;
		private String id;
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static class BriefQuery extends GsonPropertyObject {
		private String name;
		private String alias;
		private String id;
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	public List<BriefPortal> getPortalList() {
		return portalList;
	}

	public void setPortalList(List<BriefPortal> portalList) {
		this.portalList = portalList;
	}

	public List<BriefCms> getCmsList() {
		return cmsList;
	}

	public void setCmsList(List<BriefCms> cmsList) {
		this.cmsList = cmsList;
	}

	public List<BriefQuery> getQueryList() {
		return queryList;
	}

	public void setQueryList(List<BriefQuery> queryList) {
		this.queryList = queryList;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
