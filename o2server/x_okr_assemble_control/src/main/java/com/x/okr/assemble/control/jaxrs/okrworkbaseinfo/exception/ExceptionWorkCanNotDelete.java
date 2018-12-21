package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import java.util.List;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkCanNotDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkCanNotDelete( List<String> ids ) {
		super("工作仍存在"+ ids.size() +"个下级工作，该工作暂无法删除." );
	}
}
