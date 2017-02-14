package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.entity.BBSReplyInfo;

@Wrap( BBSReplyInfo.class)
public class WrapOutReplyInfo extends BBSReplyInfo{
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();	
}
