package com.x.program.center;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.query.core.entity.wrap.WrapQuery;

public class WrapModule extends GsonPropertyObject {

	@FieldDescribe("模块名称")
	private String name;

	@FieldDescribe("标识")
	private String id;

	@FieldDescribe("分类")
	private String category;

	@FieldDescribe("图标")
	private String icon;

	@FieldDescribe("说明")
	private String description;
	
	@FieldDescribe("下载次数")
	private Integer downloadCount;

	@FieldDescribe("流程")
	private List<WrapProcessPlatform> processPlatformList = new ArrayList<>();

	@FieldDescribe("门户")
	private List<WrapPortal> portalList = new ArrayList<>();

	@FieldDescribe("查询")
	private List<WrapQuery> queryList = new ArrayList<>();

	@FieldDescribe("内容")
	private List<WrapCms> cmsList = new ArrayList<>();

	public WrapProcessPlatform getProcessPlatform(String id) {
		for (WrapProcessPlatform _o : ListTools.trim(this.processPlatformList, true, true)) {
			if (StringUtils.equalsIgnoreCase(id, _o.getId())) {
				return _o;
			}
		}
		return null;
	}

	public WrapPortal getPortal(String id) {
		for (WrapPortal _o : ListTools.trim(this.portalList, true, true)) {
			if (StringUtils.equalsIgnoreCase(id, _o.getId())) {
				return _o;
			}
		}
		return null;
	}

	public WrapCms getCms(String id) {
		for (WrapCms _o : ListTools.trim(this.cmsList, true, true)) {
			if (StringUtils.equalsIgnoreCase(id, _o.getId())) {
				return _o;
			}
		}
		return null;
	}

	public WrapQuery getQuery(String id) {
		for (WrapQuery _o : ListTools.trim(this.queryList, true, true)) {
			if (StringUtils.equalsIgnoreCase(id, _o.getId())) {
				return _o;
			}
		}
		return null;
	}

	public List<WrapProcessPlatform> getProcessPlatformList() {
		return processPlatformList;
	}

	public void setProcessPlatformList(List<WrapProcessPlatform> processPlatformList) {
		this.processPlatformList = processPlatformList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WrapPortal> getPortalList() {
		return portalList;
	}

	public void setPortalList(List<WrapPortal> portalList) {
		this.portalList = portalList;
	}

	public List<WrapQuery> getQueryList() {
		return queryList;
	}

	public void setQueryList(List<WrapQuery> queryList) {
		this.queryList = queryList;
	}

	public List<WrapCms> getCmsList() {
		return cmsList;
	}

	public void setCmsList(List<WrapCms> cmsList) {
		this.cmsList = cmsList;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(Integer downloadCount) {
		this.downloadCount = downloadCount;
	}

}
