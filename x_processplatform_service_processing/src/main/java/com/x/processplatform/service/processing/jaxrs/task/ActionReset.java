package com.x.processplatform.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ProcessingAttributes;

public class ActionReset extends ActionBase {

	protected WrapOutId execute(Business business, String id, WrapInTask wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Task task = emc.find(id, Task.class, ExceptionWhen.not_found);
		Work work = emc.find(task.getWork(), Work.class, ExceptionWhen.not_found);
		/* 检查reset人员 */
		List<String> identites = SetUniqueList.setUniqueList(new ArrayList<String>());
		for (String str : wrapIn.getIdentityList()) {
			WrapIdentity identity = business.organization().identity().getWithName(str);
			/** 去掉重置给自己 */
			if (!StringUtils.equals(task.getIdentity(), identity.getName())) {
				identites.add(identity.getName());
			}
		}
		if (identites.isEmpty()) {
			throw new Exception("can not reset to empty.");
		}
		TaskCompleted taskCompleted = this.createTaskCompleted(business, task, ProcessingType.reset);
		taskCompleted.setResetIdentityList(identites);
		emc.beginTransaction(TaskCompleted.class);
		emc.persist(taskCompleted, CheckPersistType.all);
		emc.commit();
		emc.beginTransaction(Task.class);
		emc.remove(task, CheckRemoveType.all);
		emc.commit();
		emc.beginTransaction(Work.class);
		work.setManualTaskIdentityList(identites);
		emc.check(work, CheckPersistType.all);
		emc.commit();
		Processing processing = new Processing(new ProcessingAttributes());
		processing.processing(work.getId());
		return new WrapOutId(task.getId());
	}

}