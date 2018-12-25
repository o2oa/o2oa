package com.x.processplatform.assemble.surface.jaxrs.util;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

abstract class BaseAction extends StandardJaxrsAction {

	static WrapCopier<Task, WrapOutTask> taskOutCopier = WrapCopierFactory.wo(Task.class, WrapOutTask.class, null,
			JpaObject.FieldsInvisible);

	static WrapCopier<Work, WrapOutWork> workOutCopier = WrapCopierFactory.wo(Work.class, WrapOutWork.class, null,
			JpaObject.FieldsInvisible);

	static WrapCopier<WorkCompleted, WrapOutWorkCompleted> workCompletedOutCopier = WrapCopierFactory
			.wo(WorkCompleted.class, WrapOutWorkCompleted.class, null, JpaObject.FieldsInvisible);

	static WrapCopier<Attachment, WrapOutAttachment> attachmentOutCopier = WrapCopierFactory.wo(Attachment.class,
			WrapOutAttachment.class, null, JpaObject.FieldsInvisible);

}
