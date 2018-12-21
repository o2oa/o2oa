package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Application;

public class WrapOutApplication extends Application {
	
	private static final long serialVersionUID = -7648824521711153693L;
	
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
