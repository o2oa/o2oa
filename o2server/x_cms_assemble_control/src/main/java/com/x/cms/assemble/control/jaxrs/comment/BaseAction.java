package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.CommentCommendPersistService;
import com.x.cms.assemble.control.service.DocCommendPersistService;
import com.x.cms.assemble.control.service.DocCommendQueryService;
import com.x.cms.assemble.control.service.DocumentCommentInfoPersistService;
import com.x.cms.assemble.control.service.DocumentCommentInfoQueryService;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommentInfo;

public class BaseAction extends StandardJaxrsAction {

	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(DocumentCommentInfo.class, Document.class);

	protected DocumentCommentInfoPersistService documentCommentInfoPersistService = new DocumentCommentInfoPersistService();

	protected DocumentCommentInfoQueryService documentCommentInfoQueryService = new DocumentCommentInfoQueryService();

	protected DocumentQueryService documentInfoServiceAdv = new DocumentQueryService();

	protected CommentCommendPersistService commentCommendPersistService = new CommentCommendPersistService();
	protected DocCommendQueryService docCommendQueryService = new DocCommendQueryService();

	protected DocCommendPersistService docCommendPersistService = new DocCommendPersistService();
}
