package com.x.teamwork.assemble.control.jaxrs.stat;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.*;
import com.x.teamwork.core.entity.Project;

public class BaseAction extends StandardJaxrsAction {

	protected TaskQueryService taskQueryService = new TaskQueryService();

	protected ProjectQueryService projectQueryService = new ProjectQueryService();

	protected ProjectGroupQueryService projectGroupQueryService = new ProjectGroupQueryService();

	protected SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();

}
