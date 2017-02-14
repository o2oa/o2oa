package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplication;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutProcess;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

abstract class ActionBase {

	protected static BeanCopyTools<Application, WrapOutApplication> applicationOutCopier = BeanCopyToolsBuilder
			.create(Application.class, WrapOutApplication.class, null, WrapOutApplication.Excludes);

	protected static BeanCopyTools<Process, WrapOutProcess> processOutCopier = BeanCopyToolsBuilder
			.create(Process.class, WrapOutProcess.class, null, WrapOutProcess.Excludes);
	
}
