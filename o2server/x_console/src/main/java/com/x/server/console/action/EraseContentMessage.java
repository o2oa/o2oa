package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.Instant;
import com.x.message.core.entity.Mass;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Org;

public class EraseContentMessage extends EraseContent {

	private static Logger logger = LoggerFactory.getLogger(EraseContentMessage.class);

	@Override
	public boolean execute() throws Exception {
		this.init("message", null);
		addClass(IMConversation.class);
		addClass(IMConversationExt.class);
		addClass(IMMsg.class);
		addClass(Instant.class);
		addClass(Mass.class);
		addClass(Message.class);
		addClass(Org.class);
		this.run();
		return true;
	}
}