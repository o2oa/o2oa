package com.x.server.console.action;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.utils.ListTools;

public class ActionVersion extends ActionBase {
	public void execute(String base) throws Exception {
		WrapOutCheck check = this.check(base);
		String str = "current version:" + check.getCurrent() + StringUtils.LF;
		str += "updatable:" + ListTools.isNotEmpty(check.getFollowList()) + StringUtils.LF;
		if (ListTools.isNotEmpty(check.getFollowList())) {
			str += "next version:" + check.getFollowList().get(0).getName() + StringUtils.LF;
			str += "size:" + (check.getFollowList().get(0).getSize() / (1024 * 1024)) + "MB" + StringUtils.LF;
			str += "description:" + StringUtils.LF;
			str += check.getFollowList().get(0).getDescription() + StringUtils.LF;
			if (check.getFollowList().size() > 1) {
				str += "follow version:" + StringUtils.LF;
				for (int i = 1; i < check.getFollowList().size(); i++) {
					str += check.getFollowList().get(i).getName() + StringUtils.LF;
				}
			}
		}
		System.out.println(str);
	}
}
