package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Message;

@Wrap(Message.class)
public class WrapOutMessage extends Message {

	private static final long serialVersionUID = 8424183834921411324L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
