package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Publish;

/**
 * @author sword
 */
public class PublishFactory extends ElementFactory {

	public PublishFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Publish pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	@Deprecated
	public Publish pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Publish.class);
	}

	public List<Publish> listWithProcess(Process process) throws Exception {
		return this.listWithProcess(Publish.class, process);
	}
}
