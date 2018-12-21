package com.x.collaboration.core.message.operation;

import com.x.collaboration.core.message.BaseMessage;
import com.x.collaboration.core.message.MessageCategory;

public class OperationMessage extends BaseMessage {
	public OperationMessage() {
		super(MessageCategory.operation);
	}
}
