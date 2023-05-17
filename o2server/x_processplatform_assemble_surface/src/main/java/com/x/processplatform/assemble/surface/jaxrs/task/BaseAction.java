package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.util.WorkLogTree;

abstract class BaseAction extends StandardJaxrsAction {

	Cache.CacheCategory cacheCategory = new Cache.CacheCategory(Task.class, Application.class, Process.class);

	protected WorkLogTree workLogTree(Business business, String job) throws Exception {
		return new WorkLogTree(business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, job));
	}
}
