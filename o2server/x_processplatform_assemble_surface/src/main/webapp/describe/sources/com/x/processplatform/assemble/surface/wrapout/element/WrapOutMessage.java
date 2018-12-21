package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Message;

public class WrapOutMessage extends Message {

	private static final long serialVersionUID = -2833187584269867692L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
