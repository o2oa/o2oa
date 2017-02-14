package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Script;

@Wrap(Script.class)
public class WrapOutScript extends Script {

	private static final long serialVersionUID = 2475165883507548650L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
