package com.x.cms.assemble.search.jaxrs.spider;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("spider")
@JaxrsDescribe("CMS文档爬虫")
public class CMS_SpiderAction extends StandardJaxrsAction{
	
	private static  Logger logger = LoggerFactory.getLogger( CMS_SpiderAction.class );
	
	@JaxrsMethodDescribe(value = "爬取所有的CMS信息内容到搜索服务器", action = ActionCollectDocuments.class)
	@GET
	@Path("cms/collect/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public void collect_documents(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request ) {
		ActionResult<WoId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCollectDocuments().execute( request, effectivePerson );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
}