package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;

public class WorkProperties extends JsonProperties {

	private static final long serialVersionUID = -62236689373222398L;

	@FieldDescribe("强制待办处理人")
	private List<String> manualForceTaskIdentityList = new ArrayList<>();

	@FieldDescribe("授权对象")
	private Map<String, String> manualEmpowerMap = new LinkedHashMap<>();

	@FieldDescribe("服务回调值")
	private Map<String, Object> serviceValue = new LinkedHashMap<>();

	@FieldDescribe("标题")
	private String title;

	@FieldDescribe("调用子流程后子流程结束将回写,用于标识子流程已经结束.")
	private String embedCompleted;

	@FieldDescribe("父工作,在当前工作是通过子流程调用时产生.")
	private String parentWork;

	@FieldDescribe("父工作Job,在当前工作是通过子流程调用时产生.")
	private String parentJob;

	@FieldDescribe("Embed活动生成的Work的Job.")
	private String embedTargetJob;

	@FieldDescribe("拆分值列表")
	private List<String> splitValueList = new ArrayList<>();

	@FieldDescribe("待办身份矩阵")
	private ManualTaskIdentityMatrix manualTaskIdentityMatrix = new ManualTaskIdentityMatrix();

	public ManualTaskIdentityMatrix getManualTaskIdentityMatrix() {
		return manualTaskIdentityMatrix;
	}

	public void setManualTaskIdentityMatrix(ManualTaskIdentityMatrix manualTaskIdentityMatrix) {
		this.manualTaskIdentityMatrix = manualTaskIdentityMatrix;
	}

	public List<String> getManualForceTaskIdentityList() {
		if (this.manualForceTaskIdentityList == null) {
			this.manualForceTaskIdentityList = new ArrayList<>();
		}
		return this.manualForceTaskIdentityList;
	}

	public void setManualForceTaskIdentityList(List<String> manualForceTaskIdentityList) {
		this.manualForceTaskIdentityList = manualForceTaskIdentityList;
	}

	public Map<String, String> getManualEmpowerMap() {
		if (this.manualEmpowerMap == null) {
			this.manualEmpowerMap = new LinkedHashMap<>();
		}
		return this.manualEmpowerMap;
	}

	public void setManualEmpowerMap(Map<String, String> manualEmpowerMap) {
		this.manualEmpowerMap = manualEmpowerMap;
	}

	public Map<String, Object> getServiceValue() {
		if (this.serviceValue == null) {
			this.serviceValue = new LinkedHashMap<String, Object>();
		}
		return this.serviceValue;
	}

	public void setServiceValue(Map<String, Object> serviceValue) {
		this.serviceValue = serviceValue;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getSplitValueList() {
		if (null == this.splitValueList) {
			this.splitValueList = new ArrayList<>();
		}
		return this.splitValueList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.splitValueList = splitValueList;
	}

	public String getEmbedCompleted() {
		return embedCompleted;
	}

	public void setEmbedCompleted(String embedCompleted) {
		this.embedCompleted = embedCompleted;
	}

	public String getParentWork() {
		return parentWork;
	}

	public void setParentWork(String parentWork) {
		this.parentWork = parentWork;
	}

	public String getParentJob() {
		return parentJob;
	}

	public void setParentJob(String parentJob) {
		this.parentJob = parentJob;
	}

	public String getEmbedTargetJob() {
		return embedTargetJob;
	}

	public void setEmbedTargetJob(String embedTargetJob) {
		this.embedTargetJob = embedTargetJob;
	}

}
