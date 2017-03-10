package com.x.cms.assemble.control.jaxrs.search;

import java.util.List;

import com.x.base.core.exception.PromptException;

class CategoryInfoFilterListException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public CategoryInfoFilterListException(Throwable e, List<String> app_ids, String docStatus, String categoryId) {
		super("系统在根据可访问栏目ID列表，文档状态以及可访问分类ID统计涉及到的所有分类名称列表时发生异常。AppIds:" + app_ids + ", DocStatus:" + docStatus + ", CategoryId:" +  categoryId);
	}
}
