package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkCompletedNotContainsAttachment extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkCompletedNotContainsAttachment(String title, String workCompletedId, String attachmentName,
			String attachmentId) {
		super("已完成工作 title:{} id:{}, 没有查找到附件 name:{} id:{}.", title, workCompletedId,
				attachmentName, attachmentId);
	}

}
