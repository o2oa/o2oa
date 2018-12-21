package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Message;

public class WrapMessage extends Message {

	private static final long serialVersionUID = 8424183834921411324L;

	public static WrapCopier<Message, WrapMessage> outCopier = WrapCopierFactory.wo(Message.class, WrapMessage.class,
			null, JpaObject.FieldsInvisible);

	public static WrapCopier<WrapMessage, Message> inCopier = WrapCopierFactory.wi(WrapMessage.class, Message.class,
			null, JpaObject.FieldsUnmodifyExcludeId);
}
