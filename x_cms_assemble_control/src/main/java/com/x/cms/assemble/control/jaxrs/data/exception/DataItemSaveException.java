package com.x.cms.assemble.control.jaxrs.data.exception;

import com.x.base.core.exception.PromptException;

public class DataItemSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DataItemSaveException( Throwable e, String docId, String ...path ) {
		super("文档数据信息保存时发生异常。DocId:" + docId + ", path:" + path, e );
	}
}
