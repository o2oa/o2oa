package com.x.server.console.action;

import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.entity.BBSOperationRecord;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectContent;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSSubjectVoteResult;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionGroup;
import com.x.bbs.entity.BBSVoteRecord;

public class EraseContentBbs extends EraseContent {

	private static Logger logger = LoggerFactory.getLogger(EraseContentBbs.class);
	
	@Override
	public boolean execute() throws Exception {
		this.init("bbs", ItemCategory.bbs);
		addClass(BBSOperationRecord.class);
		addClass(BBSReplyInfo.class);
		addClass(BBSSubjectAttachment.class);
		addClass(BBSSubjectContent.class);
		addClass(BBSSubjectInfo.class);
		addClass(BBSSubjectVoteResult.class);
		addClass(BBSVoteOption.class);
		addClass(BBSVoteOptionGroup.class);
		addClass(BBSVoteRecord.class);
		this.run();
		return true;
	}
}