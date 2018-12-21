package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;

public class RouteFactory extends ElementFactory {

	public RouteFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Route pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Route pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Route.class );
	}

	public List<Route> listWithProcess(Process process) throws Exception {
		List<Route> list = this.listWithProcess(Route.class, process);
		return list;
	}
}