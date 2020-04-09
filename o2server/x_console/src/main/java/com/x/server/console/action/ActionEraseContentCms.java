package com.x.server.console.action;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.DocumentViewRecord;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.ReadRemind;
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.cms.core.entity.DocumentCommentContent;
import com.x.cms.core.entity.DocumentCommentCommend;
import com.x.cms.core.entity.DocumentCommend;
import com.x.cms.core.entity.CmsBatchOperation;

public class ActionEraseContentCms extends ActionEraseContentProcessPlatform {

	private static Logger logger = LoggerFactory.getLogger(ActionEraseContentCms.class);

	public boolean execute(String password) throws Exception {
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			logger.print("password not match.");
			return false;
		}
		this.init("cms", ItemCategory.cms);
		addClass(Document.class);
		addClass(Review.class);
		addClass(DocumentViewRecord.class);
		addClass(FileInfo.class);
		addClass(Log.class);
		addClass(ReadRemind.class);
		addClass(DocumentCommentInfo.class);
		addClass(DocumentCommentContent.class);
		addClass(DocumentCommentCommend.class);
		addClass(DocumentCommend.class);
		addClass(CmsBatchOperation.class);
		this.run();
		return true;
	}
}