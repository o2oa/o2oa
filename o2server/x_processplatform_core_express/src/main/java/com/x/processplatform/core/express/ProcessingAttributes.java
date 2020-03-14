package com.x.processplatform.core.express;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.StringTools;

public class ProcessingAttributes extends GsonPropertyObject {

	public static final String TYPE_TASK = "task";
	public static final String TYPE_APPENDTASK = "appendTask";
	public static final String TYPE_REROUTE = "reroute";
	public static final String TYPE_RESET = "reset";
	public static final String TYPE_RETRACT = "retract";
	public static final String TYPE_ROLLBACK = "rollback";
	private Integer loop = 1;

	@FieldDescribe("强制从arrive开始")
	private Boolean forceJoinAtArrive;

	@FieldDescribe("强制从inquire开始")
	private Boolean forceJoinAtInquire;

	@FieldDescribe("忽略授权的身份")
	private List<String> ignoreEmpowerIdentityList = new ArrayList<>();

	public ProcessingAttributes() {
		this.series = StringTools.uniqueToken();
	}

	private Boolean debugger = false;

	private List<String> manualForceTaskIdentityList = new ArrayList<>();

	private JsonElement routeData;

	private String type;

	private String series;

	public List<String> getIgnoreEmpowerIdentityList() {
		if (null == ignoreEmpowerIdentityList) {
			this.ignoreEmpowerIdentityList = new ArrayList<>();
		}
		return ignoreEmpowerIdentityList;
	}

	public Boolean ifForceJoinAtArrive() {
		if (this.getForceJoinAtArrive() && loop == 1) {
			return true;
		}
		return false;
	}

	public Boolean ifForceJoinAtInquire() {
		if (this.getForceJoinAtInquire() && loop == 1) {
			return true;
		}
		return false;
	}

	public Boolean getForceJoinAtArrive() {
		if (null == forceJoinAtArrive) {
			forceJoinAtArrive = false;
		}
		return forceJoinAtArrive;
	}

	public Boolean getForceJoinAtInquire() {
		if (null == forceJoinAtInquire) {
			forceJoinAtInquire = false;
		}
		return forceJoinAtInquire;
	}

	public Boolean getDebugger() {
		return debugger;
	}

	public void setDebugger(Boolean debugger) {
		this.debugger = debugger;
	}

	public List<String> getManualForceTaskIdentityList() {
		if (null == this.manualForceTaskIdentityList) {
			this.manualForceTaskIdentityList = new ArrayList<>();
		}
		return this.manualForceTaskIdentityList;
	}

	public void setManualForceTaskIdentityList(List<String> manualForceTaskIdentityList) {
		this.manualForceTaskIdentityList = manualForceTaskIdentityList;
	}

	public JsonElement getRouteData() {
		return routeData;
	}

	public void setRouteData(JsonElement routeData) {
		this.routeData = routeData;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Integer getLoop() {
		if (null == loop) {
			loop = 1;
		}
		return loop;
	}

	public void increaseLoop() {
		this.loop = loop + 1;
	}

	public void setForceJoinAtArrive(Boolean forceJoinAtArrive) {
		this.forceJoinAtArrive = forceJoinAtArrive;
	}

	public void setForceJoinAtInquire(Boolean forceJoinAtInquire) {
		this.forceJoinAtInquire = forceJoinAtInquire;
	}

	public void setIgnoreEmpowerIdentityList(List<String> ignoreEmpowerIdentityList) {
		this.ignoreEmpowerIdentityList = ignoreEmpowerIdentityList;
	}

}
