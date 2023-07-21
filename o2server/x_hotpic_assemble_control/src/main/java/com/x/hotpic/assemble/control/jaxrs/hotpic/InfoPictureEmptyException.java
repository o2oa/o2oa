package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoPictureEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoPictureEmptyException() {
		super("信息图片内容为空，无法继续查询或者保存数据。" );
	}
}
