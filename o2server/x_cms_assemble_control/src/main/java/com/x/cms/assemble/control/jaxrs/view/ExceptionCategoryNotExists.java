package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryNotExists( String id ) {
		super( "分类不存在，无法继续进行列表数据查询。ID:" + id );
	}
}
