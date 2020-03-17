package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileInfoIsNotImage extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFileInfoIsNotImage( String id ) {
		super("文件信息不为图片格式，无法进行图片转换。Id:" + id );
	}
}
