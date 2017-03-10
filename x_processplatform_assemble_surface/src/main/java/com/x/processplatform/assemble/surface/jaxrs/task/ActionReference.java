package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.builder.WorkLogBuilder;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkLog;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionReference extends ActionBase {

	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new TaskNotExistedException(id);
			}
			if (effectivePerson.isNotUser(effectivePerson.getName()) && effectivePerson.isNotManager()) {
				throw new TaskAccessDeniedException(effectivePerson.getName(), task.getId());
			}
			WrapOutMap wrap = new WrapOutMap();
			/* 组装 Task 信息 */
			wrap.put("task", taskOutCopier.copy(task));
			Work work = emc.find(task.getWork(), Work.class);
			/* 组装 Work */
			if (null != work) {
				wrap.put("work", workOutCopier.copy(work));
				/* 组装 Attachment */
				wrap.put("attachmentList", this.listAttachment(business, work));
			}
			/* 装载WorkLog 信息 */
			wrap.put("workCompletedList", this.listWorkCompleted(business, task));
			wrap.put("workLogList", this.listWorkLog(business, task));
			result.setData(wrap);
			return result;
		}
	}

	private List<WrapOutAttachment> listAttachment(Business business, Work work) throws Exception {
		List<Attachment> list = business.entityManagerContainer().list(Attachment.class, work.getAttachmentList());
		List<WrapOutAttachment> wraps = attachmentOutCopier.copy(list);
		SortTools.asc(wraps, "createTime");
		return wraps;
	}

	private List<WrapOutWorkLog> listWorkLog(Business business, Task task) throws Exception {
		List<String> ids = business.workLog().listWithFromActivityTokenBackward(task.getActivityToken());
		List<WorkLog> list = business.entityManagerContainer().list(WorkLog.class, ids);
		return WorkLogBuilder.complex(business, list);
	}

	private List<WrapOutWorkCompleted> listWorkCompleted(Business business, Task task) throws Exception {
		List<WrapOutWorkCompleted> list = workCompletedOutCopier
				.copy(business.workCompleted().listWithJobObject(task.getJob()));
		SortTools.asc(list, "createTime");
		return list;
	}
}
