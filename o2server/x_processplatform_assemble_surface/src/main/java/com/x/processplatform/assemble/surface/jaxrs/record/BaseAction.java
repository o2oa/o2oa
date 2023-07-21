package com.x.processplatform.assemble.surface.jaxrs.record;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;

abstract class BaseAction extends StandardJaxrsAction {

	protected Record taskToRecord(Task task) {
		Record o = new Record();
		o.setType(Record.TYPE_CURRENTTASK);
		o.setFromActivity(task.getActivity());
		o.setFromActivityAlias(task.getActivityAlias());
		o.setFromActivityName(task.getActivityName());
		o.setFromActivityToken(task.getActivityToken());
		o.setFromActivityType(task.getActivityType());
		o.setPerson(task.getPerson());
		o.setIdentity(o.getIdentity());
		o.setUnit(task.getUnit());
		o.getProperties().setStartTime(task.getStartTime());
		o.getProperties().setEmpowerFromIdentity(task.getEmpowerFromIdentity());
		return o;
	}

}