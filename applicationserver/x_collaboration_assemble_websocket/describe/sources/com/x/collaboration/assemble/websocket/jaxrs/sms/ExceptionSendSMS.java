package com.x.collaboration.assemble.websocket.jaxrs.sms;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSendSMS extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSendSMS( Throwable e, String personName ) {
	    super("系统发送短信到企业短信中心发生异常.Person:" + personName , e );
	}
}
