package com.x.processplatform.core.entity.content;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.MD5Tool;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author sword
 */
public class TaskProcessModeItem extends GsonPropertyObject {

	private static final long serialVersionUID = -4304712246150998121L;

	public TaskProcessModeItem(){
	}

	public TaskProcessModeItem(String routeGroup, Boolean keepTask, String opinion, Map<String, List<String>> organizations){
		this.routeGroup = routeGroup;
		this.keepTask = keepTask;
		this.opinion = opinion;
		this.organizations = organizations;

		String keyStr = this.routeGroup+this.keepTask+this.opinion+ XGsonBuilder.toJson(organizations);
		this.md5Key = MD5Tool.getMD5Str(keyStr);

		this.updateTime = new Date();
		this.hitCount = 1;
	}

	private String routeGroup;

	private Boolean keepTask;

	private String opinion;

	private Map<String, List<String>> organizations;

	public static final String updateTime_FIELDNAME = "updateTime";
	private Date updateTime;

	public static final String hitCount_FIELDNAME = "hitCount";
	private Integer hitCount;

	private String md5Key;

	public String getRouteGroup() {
		return routeGroup;
	}

	public void setRouteGroup(String routeGroup) {
		this.routeGroup = routeGroup;
	}

	public Boolean getKeepTask() {
		return keepTask;
	}

	public void setKeepTask(Boolean keepTask) {
		this.keepTask = keepTask;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public Map<String, List<String>> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Map<String, List<String>> organizations) {
		this.organizations = organizations;
	}

	public Integer getHitCount() {
		return hitCount;
	}

	public void setHitCount(Integer hitCount) {
		this.hitCount = hitCount;
	}

	public String getMd5Key() {
		return md5Key;
	}

	public void setMd5Key(String md5Key) {
		this.md5Key = md5Key;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void addHitCount(){
		this.hitCount++;
		this.updateTime = new Date();
	}

}
