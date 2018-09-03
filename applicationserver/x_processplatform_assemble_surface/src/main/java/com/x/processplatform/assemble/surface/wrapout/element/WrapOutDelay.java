package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Delay;

public class WrapOutDelay extends Delay {

	private static final long serialVersionUID = 2184569713152663503L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
