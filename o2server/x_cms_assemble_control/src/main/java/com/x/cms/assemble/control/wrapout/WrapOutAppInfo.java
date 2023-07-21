package com.x.cms.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.cms.core.entity.AppInfo;

public class WrapOutAppInfo extends AppInfo {

	private static final long serialVersionUID = -7648824521711153693L;

	public static List<String> excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
