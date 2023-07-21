package com.x.processplatform.core.entity.element.wrap;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Process;

public class WrapProcess extends Process {

	private static final long serialVersionUID = 1439909268641168987L;

	public static final WrapCopier<Process, WrapProcess> outCopier = WrapCopierFactory.wo(Process.class,
			WrapProcess.class, null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapProcess, Process> inCopier = WrapCopierFactory.wi(WrapProcess.class,
			Process.class, null, ListTools.toList(FieldsUnmodifyIncludePorpertiesExcludeId, creatorPerson_FIELDNAME,
					lastUpdatePerson_FIELDNAME, lastUpdateTime_FIELDNAME),
			false);

	private List<WrapAgent> agentList = new ArrayList<>();
	private WrapBegin begin;
	private List<WrapCancel> cancelList = new ArrayList<>();
	private List<WrapChoice> choiceList = new ArrayList<>();
	private List<WrapDelay> delayList = new ArrayList<>();
	private List<WrapEmbed> embedList = new ArrayList<>();
	private List<WrapEnd> endList = new ArrayList<>();
	private List<WrapInvoke> invokeList = new ArrayList<>();
	private List<WrapManual> manualList = new ArrayList<>();
	private List<WrapMerge> mergeList = new ArrayList<>();
	private List<WrapRoute> routeList = new ArrayList<>();
	private List<WrapParallel> parallelList = new ArrayList<>();
	private List<WrapPublish> publishList = new ArrayList<>();
	private List<WrapService> serviceList = new ArrayList<>();
	private List<WrapSplit> splitList = new ArrayList<>();

	public List<WrapAgent> getAgentList() {
		return agentList;
	}

	public void setAgentList(List<WrapAgent> agentList) {
		this.agentList = agentList;
	}

	public List<WrapCancel> getCancelList() {
		return cancelList;
	}

	public void setCancelList(List<WrapCancel> cancelList) {
		this.cancelList = cancelList;
	}

	public List<WrapChoice> getChoiceList() {
		return choiceList;
	}

	public void setChoiceList(List<WrapChoice> choiceList) {
		this.choiceList = choiceList;
	}

	public List<WrapDelay> getDelayList() {
		return delayList;
	}

	public void setDelayList(List<WrapDelay> delayList) {
		this.delayList = delayList;
	}

	public List<WrapEmbed> getEmbedList() {
		return embedList;
	}

	public void setEmbedList(List<WrapEmbed> embedList) {
		this.embedList = embedList;
	}

	public List<WrapEnd> getEndList() {
		return endList;
	}

	public void setEndList(List<WrapEnd> endList) {
		this.endList = endList;
	}

	public List<WrapInvoke> getInvokeList() {
		return invokeList;
	}

	public void setInvokeList(List<WrapInvoke> invokeList) {
		this.invokeList = invokeList;
	}

	public List<WrapManual> getManualList() {
		return manualList;
	}

	public void setManualList(List<WrapManual> manualList) {
		this.manualList = manualList;
	}

	public List<WrapMerge> getMergeList() {
		return mergeList;
	}

	public void setMergeList(List<WrapMerge> mergeList) {
		this.mergeList = mergeList;
	}

	public List<WrapRoute> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<WrapRoute> routeList) {
		this.routeList = routeList;
	}

	public List<WrapParallel> getParallelList() {
		return parallelList;
	}

	public void setParallelList(List<WrapParallel> parallelList) {
		this.parallelList = parallelList;
	}

	public List<WrapService> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<WrapService> serviceList) {
		this.serviceList = serviceList;
	}

	public List<WrapSplit> getSplitList() {
		return splitList;
	}

	public void setSplitList(List<WrapSplit> splitList) {
		this.splitList = splitList;
	}

	public WrapBegin getBegin() {
		return begin;
	}

	public void setBegin(WrapBegin begin) {
		this.begin = begin;
	}

	public List<WrapPublish> getPublishList() {
		return publishList;
	}

	public void setPublishList(List<WrapPublish> publishList) {
		this.publishList = publishList;
	}
}
