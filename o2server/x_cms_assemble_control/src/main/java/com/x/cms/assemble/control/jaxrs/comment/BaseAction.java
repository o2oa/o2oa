package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.CommentCommendPersistService;
import com.x.cms.assemble.control.service.DocumentCommentInfoPersistService;
import com.x.cms.assemble.control.service.DocumentCommentInfoQueryService;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.core.entity.DocumentCommentInfo;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache commentInfoCache = ApplicationCache.instance().getCache( DocumentCommentInfo.class );
	
	protected 	DocumentCommentInfoPersistService documentCommentInfoPersistService = new DocumentCommentInfoPersistService();
	
	protected 	DocumentCommentInfoQueryService documentCommentInfoQueryService = new DocumentCommentInfoQueryService();
	
	protected DocumentQueryService documentInfoServiceAdv = new DocumentQueryService();
	
	protected CommentCommendPersistService commentCommendPersistService = new CommentCommendPersistService();
}
