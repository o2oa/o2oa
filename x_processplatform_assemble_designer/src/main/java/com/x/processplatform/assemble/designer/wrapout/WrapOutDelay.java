package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Delay;

@Wrap(Delay.class)
public class WrapOutDelay extends Delay {

	private static final long serialVersionUID = 6695709068501511733L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
