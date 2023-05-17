package com.x.processplatform.core.express;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.core.entity.log.SignalStack;

public class ProcessingAttributes extends GsonPropertyObject {

	private static final long serialVersionUID = 4904585805951490393L;

	public static final String TYPE_TASK = "task";
	public static final String TYPE_APPENDTASK = "appendTask";
	public static final String TYPE_ADDSPLIT = "addSplit";
	public static final String TYPE_REROUTE = "reroute";
	public static final String TYPE_RESET = "reset";
	public static final String TYPE_RETRACT = "retract";
	public static final String TYPE_ROLLBACK = "rollback";
	public static final String TYPE_SERVICE = "service";
	public static final String TYPE_TASKEXTEND = "taskExtend";
	public static final String TYPE_TASKADD = "taskAdd";
	public static final String TYPE_GOBACK = "goBack";

	private Integer loop = 1;

	@FieldDescribe("强制从arrive开始")
	private Boolean forceJoinAtArrive;

	@FieldDescribe("强制从inquire开始")
	private Boolean forceJoinAtInquire;

	@FieldDescribe("忽略授权的身份")
	private List<String> ignoreEmpowerIdentityList = new ArrayList<>();

	@FieldDescribe("当前处理人")
	private String person;

	@FieldDescribe("当前处理人身份")
	private String identity;

	private SignalStack signalStack = new SignalStack();

	public SignalStack getSignalStack() {
		return signalStack;
	}

	public void push(Signal signal) {
		this.signalStack.push(signal);
	}

	public ProcessingAttributes() {
		this.series = StringTools.uniqueToken();
		this.signalStack = new SignalStack();
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
		return BooleanUtils.isTrue(this.getForceJoinAtArrive()) && (loop == 1);
	}

	public Boolean ifForceJoinAtInquire() {
		return BooleanUtils.isTrue(this.getForceJoinAtInquire()) && (loop == 1);
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

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public ProcessingAttributes copyInstancePointToSingletonSignalStack() {
		ProcessingAttributes p = XGsonBuilder.convert(this, ProcessingAttributes.class);
		p.signalStack = this.signalStack;
		return p;
	}

}
