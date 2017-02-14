package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInRead;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutRead;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

abstract class ActionBase extends StandardJaxrsAction {

	protected static BeanCopyTools<Read, WrapOutRead> readOutCopier = BeanCopyToolsBuilder.create(Read.class,
			WrapOutRead.class);

	protected static BeanCopyTools<WrapInRead, Read> readInCopier = BeanCopyToolsBuilder.create(WrapInRead.class,
			Read.class, WrapInRead.Includes, null);

	protected static BeanCopyTools<Work, WrapOutWork> workOutCopier = BeanCopyToolsBuilder.create(Work.class,
			WrapOutWork.class, null, WrapOutWork.Excludes);

	protected static BeanCopyTools<WorkCompleted, WrapOutWorkCompleted> workCompletedOutCopier = BeanCopyToolsBuilder
			.create(WorkCompleted.class, WrapOutWorkCompleted.class, null, WrapOutWorkCompleted.Excludes);

}