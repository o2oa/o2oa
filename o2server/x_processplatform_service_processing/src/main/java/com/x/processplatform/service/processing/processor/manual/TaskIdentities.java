package com.x.processplatform.service.processing.processor.manual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.organization.Empower;
import com.x.base.core.project.tools.ListTools;

public class TaskIdentities extends ArrayList<TaskIdentity> {

	public TaskIdentities(List<String> list) {
		for (String str : ListTools.trim(list, true, true)) {
			TaskIdentity taskIdentity = new TaskIdentity();
			taskIdentity.setIdentity(str);
			this.add(taskIdentity);
		}
	}

	public void update(List<Empower> list) {

		for (Empower empower : list) {
			if (StringUtils.isNotEmpty(empower.getFromIdentity()) && StringUtils.isNotEmpty(empower.getToIdentity())
					&& (!StringUtils.equals(empower.getFromIdentity(), empower.getToIdentity()))) {
				for (TaskIdentity taskIdentity : this) {
					if (StringUtils.equals(taskIdentity.getIdentity(), empower.getFromIdentity())) {
						taskIdentity.setIdentity(empower.getToIdentity());
						taskIdentity.setFromIdentity(empower.getFromIdentity());
						break;
					}
				}
			}
		}
	}

	public TaskIdentities addIdentity(String str) {
		if (StringUtils.isNotEmpty(str)) {
			for (TaskIdentity taskIdentity : this) {
				if (StringUtils.equals(taskIdentity.getIdentity(), str)) {
					return this;
				}
			}
			TaskIdentity taskIdentity = new TaskIdentity();
			taskIdentity.setIdentity(str);
			this.add(taskIdentity);
		}
		return this;
	}

	public TaskIdentities removeIdentity(String str) {
		if (StringUtils.isNotEmpty(str)) {
			for (TaskIdentity taskIdentity : this) {
				if (StringUtils.equals(taskIdentity.getIdentity(), str)) {
					this.remove(taskIdentity);
					return this;
				}
			}
		}
		return this;
	}

	public TaskIdentities addIdentities(Collection<String> collections) {
		for (String str : collections) {
			this.addIdentity(str);
		}
		return this;
	}

	public List<String> identities() {
		List<String> list = new ArrayList<>();
		for (TaskIdentity taskIdentity : this) {
			list.add(taskIdentity.getIdentity());
		}
		return list;
	}

	private static final long serialVersionUID = -5874962038380255744L;

}