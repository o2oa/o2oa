package com.x.message.assemble.communicate.mq;

import com.x.message.core.entity.Message;

public interface MQInterface {
	public boolean sendMessage(Message message);
}
