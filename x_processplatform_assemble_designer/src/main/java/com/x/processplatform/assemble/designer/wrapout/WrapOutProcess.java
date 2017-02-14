package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Process;

@Wrap(Process.class)
public class WrapOutProcess extends Process {

	private static final long serialVersionUID = 1439909268641168987L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}