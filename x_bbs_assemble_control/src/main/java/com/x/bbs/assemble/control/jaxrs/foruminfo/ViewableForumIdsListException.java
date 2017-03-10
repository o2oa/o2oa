package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ViewableForumIdsListException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ViewableForumIdsListException( Throwable e, String person ) {
		super("根据条件查询BBS论坛分区ID信息列表时发生异常.Person:" + person, e );
	}
}
