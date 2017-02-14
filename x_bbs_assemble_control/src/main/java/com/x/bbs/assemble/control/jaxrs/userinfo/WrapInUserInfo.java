package com.x.bbs.assemble.control.jaxrs.userinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.entity.BBSUserInfo;

@Wrap( BBSUserInfo.class)
public class WrapInUserInfo extends BBSUserInfo{

	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();

}
