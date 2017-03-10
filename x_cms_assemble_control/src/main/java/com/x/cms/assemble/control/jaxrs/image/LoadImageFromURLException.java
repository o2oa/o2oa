package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.exception.PromptException;

class LoadImageFromURLException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	LoadImageFromURLException( Throwable e, String url ) {
		super("从指定的URL获取图片信息发生异常!URL:" + url, e );
	}
}
