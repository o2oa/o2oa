package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Process;

public class MessageFactory extends ElementFactory {

	public MessageFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Message pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Message pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Message.class );
	}

	public List<Message> listWithProcess(Process process) throws Exception {
		List<Message> list = this.listWithProcess(Message.class, process);
		return list;
	}
}