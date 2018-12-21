package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Process;

public class WrapOutProcess extends Process {

	private static final long serialVersionUID = 1521228691441978462L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private List<WrapOutAgent> agentList;
	private WrapOutBegin begin;
	private List<WrapOutCancel> cancelList;
	private List<WrapOutChoice> choiceList;
	private List<WrapOutDelay> delayList;
	private List<WrapOutEmbed> embedList;
	private List<WrapOutEnd> endList;
	private List<WrapOutInvoke> invokeList;
	private List<WrapOutManual> manualList;
	private List<WrapOutMerge> mergeList;
	private List<WrapOutMessage> messageList;
	private List<WrapOutRoute> routeList;
	private List<WrapOutParallel> parallelList;
	private List<WrapOutService> serviceList;
	private List<WrapOutSplit> splitList;

	public List<WrapOutAgent> getAgentList() {
		return agentList;
	}

	public void setAgentList(List<WrapOutAgent> agentList) {
		this.agentList = agentList;
	}

	public WrapOutBegin getBegin() {
		return begin;
	}

	public void setBegin(WrapOutBegin begin) {
		this.begin = begin;
	}

	public List<WrapOutCancel> getCancelList() {
		return cancelList;
	}

	public void setCancelList(List<WrapOutCancel> cancelList) {
		this.cancelList = cancelList;
	}

	public List<WrapOutChoice> getChoiceList() {
		return choiceList;
	}

	public void setChoiceList(List<WrapOutChoice> choiceList) {
		this.choiceList = choiceList;
	}


	public List<WrapOutDelay> getDelayList() {
		return delayList;
	}

	public void setDelayList(List<WrapOutDelay> delayList) {
		this.delayList = delayList;
	}

	public List<WrapOutEmbed> getEmbedList() {
		return embedList;
	}

	public void setEmbedList(List<WrapOutEmbed> embedList) {
		this.embedList = embedList;
	}

	public List<WrapOutEnd> getEndList() {
		return endList;
	}

	public void setEndList(List<WrapOutEnd> endList) {
		this.endList = endList;
	}

	public List<WrapOutInvoke> getInvokeList() {
		return invokeList;
	}

	public void setInvokeList(List<WrapOutInvoke> invokeList) {
		this.invokeList = invokeList;
	}

	public List<WrapOutManual> getManualList() {
		return manualList;
	}

	public void setManualList(List<WrapOutManual> manualList) {
		this.manualList = manualList;
	}

	public List<WrapOutMerge> getMergeList() {
		return mergeList;
	}

	public void setMergeList(List<WrapOutMerge> mergeList) {
		this.mergeList = mergeList;
	}

	public List<WrapOutMessage> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<WrapOutMessage> messageList) {
		this.messageList = messageList;
	}

	public List<WrapOutRoute> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<WrapOutRoute> routeList) {
		this.routeList = routeList;
	}

	public List<WrapOutParallel> getParallelList() {
		return parallelList;
	}

	public void setParallelList(List<WrapOutParallel> parallelList) {
		this.parallelList = parallelList;
	}

	public List<WrapOutService> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<WrapOutService> serviceList) {
		this.serviceList = serviceList;
	}

	public List<WrapOutSplit> getSplitList() {
		return splitList;
	}

	public void setSplitList(List<WrapOutSplit> splitList) {
		this.splitList = splitList;
	}

}
