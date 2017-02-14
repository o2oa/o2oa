package com.x.processplatform.assemble.designer.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Process;

@Wrap(Process.class)
public class WrapInProcess extends Process {

	private static final long serialVersionUID = -8002245968545089464L;

	public static List<String> Excludes = new ArrayList<>();

	static {
		Excludes.add(DISTRIBUTEFACTOR);
		Excludes.add("updateTime");
		Excludes.add("createTime");
		Excludes.add("sequence");
		Excludes.add("lastUpdatePerson");
		Excludes.add("lastUpdateTime");
		Excludes.add("creatorPerson");
	}

	private List<WrapInAgent> agentList;
	private WrapInBegin begin;
	private List<WrapInCancel> cancelList;
	private List<WrapInChoice> choiceList;
	private List<WrapInDelay> delayList;
	private List<WrapInEmbed> embedList;
	private List<WrapInEnd> endList;
	private List<WrapInInvoke> invokeList;
	private List<WrapInManual> manualList;
	private List<WrapInMerge> mergeList;
	private List<WrapInMessage> messageList;
	private List<WrapInRoute> routeList;
	private List<WrapInParallel> parallelList;
	private List<WrapInService> serviceList;
	private List<WrapInSplit> splitList;

	public List<WrapInAgent> getAgentList() {
		return agentList;
	}

	public void setAgentList(List<WrapInAgent> agentList) {
		this.agentList = agentList;
	}

	public WrapInBegin getBegin() {
		return begin;
	}

	public void setBegin(WrapInBegin begin) {
		this.begin = begin;
	}

	public List<WrapInCancel> getCancelList() {
		return cancelList;
	}

	public void setCancelList(List<WrapInCancel> cancelList) {
		this.cancelList = cancelList;
	}

	public List<WrapInChoice> getChoiceList() {
		return choiceList;
	}

	public void setChoiceList(List<WrapInChoice> choiceList) {
		this.choiceList = choiceList;
	}

	public List<WrapInDelay> getDelayList() {
		return delayList;
	}

	public void setDelayList(List<WrapInDelay> delayList) {
		this.delayList = delayList;
	}

	public List<WrapInEmbed> getEmbedList() {
		return embedList;
	}

	public void setEmbedList(List<WrapInEmbed> embedList) {
		this.embedList = embedList;
	}

	public List<WrapInEnd> getEndList() {
		return endList;
	}

	public void setEndList(List<WrapInEnd> endList) {
		this.endList = endList;
	}

	public List<WrapInInvoke> getInvokeList() {
		return invokeList;
	}

	public void setInvokeList(List<WrapInInvoke> invokeList) {
		this.invokeList = invokeList;
	}

	public List<WrapInManual> getManualList() {
		return manualList;
	}

	public void setManualList(List<WrapInManual> manualList) {
		this.manualList = manualList;
	}

	public List<WrapInMerge> getMergeList() {
		return mergeList;
	}

	public void setMergeList(List<WrapInMerge> mergeList) {
		this.mergeList = mergeList;
	}

	public List<WrapInMessage> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<WrapInMessage> messageList) {
		this.messageList = messageList;
	}

	public List<WrapInRoute> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<WrapInRoute> routeList) {
		this.routeList = routeList;
	}

	public List<WrapInParallel> getParallelList() {
		return parallelList;
	}

	public void setParallelList(List<WrapInParallel> parallelList) {
		this.parallelList = parallelList;
	}

	public List<WrapInService> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<WrapInService> serviceList) {
		this.serviceList = serviceList;
	}

	public List<WrapInSplit> getSplitList() {
		return splitList;
	}

	public void setSplitList(List<WrapInSplit> splitList) {
		this.splitList = splitList;
	}

}