package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Invoke;

@Wrap(Invoke.class)
public class WrapOutInvoke extends Invoke {

	private static final long serialVersionUID = 671190420770471675L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
