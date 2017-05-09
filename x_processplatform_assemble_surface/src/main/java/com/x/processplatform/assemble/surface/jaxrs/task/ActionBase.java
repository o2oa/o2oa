package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInTask;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<Task, WrapOutTask> taskOutCopier = BeanCopyToolsBuilder.create(Task.class, WrapOutTask.class,
			null, WrapOutTask.Excludes);

	static BeanCopyTools<WrapInTask, Task> taskInCopier = BeanCopyToolsBuilder.create(WrapInTask.class, Task.class,
			WrapInTask.Includes, null);

	static BeanCopyTools<Work, WrapOutWork> workOutCopier = BeanCopyToolsBuilder.create(Work.class, WrapOutWork.class,
			null, WrapOutWork.Excludes);

	static BeanCopyTools<WorkCompleted, WrapOutWorkCompleted> workCompletedOutCopier = BeanCopyToolsBuilder
			.create(WorkCompleted.class, WrapOutWorkCompleted.class, null, WrapOutWorkCompleted.Excludes);

	static BeanCopyTools<Attachment, WrapOutAttachment> attachmentOutCopier = BeanCopyToolsBuilder
			.create(Attachment.class, WrapOutAttachment.class, null, WrapOutAttachment.Excludes);

}
