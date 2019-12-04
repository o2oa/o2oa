package com.x.cms.assemble.control.jaxrs.search;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.proxy.StandardJaxrsActionProxy;
import com.x.cms.assemble.control.ThisApplication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("searchfilter")
@JaxrsDescribe("信息文档搜索服务")
public class SearchFilterAction extends StandardJaxrsAction {

	private StandardJaxrsActionProxy proxy = new StandardJaxrsActionProxy(ThisApplication.context());

	@JaxrsMethodDescribe(value = "获取用户有权限访问的所有已发布文档分类列表.", action = ActionListAppSearchFilterForDocStatus.class)
	@GET
	@Path("list/publish/filter/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPublishAppSearchFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分类ID") @PathParam("categoryId") String categoryId) {
		EffectivePerson currentPerson = this.effectivePerson(request);
		ActionResult<ActionListAppSearchFilterForDocStatus.Wo> result =
				((ActionListAppSearchFilterForDocStatus)proxy
						.getProxy(ActionListAppSearchFilterForDocStatus.class))
						.execute(request, currentPerson, "published", categoryId);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取用户有权限访问的所有草稿文档分类列表.", action = ActionListAppSearchFilterForDocStatus.class)
	@GET
	@Path("list/draft/filter/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDraftAppSearchFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分类ID") @PathParam("categoryId") String categoryId) {
		EffectivePerson currentPerson = this.effectivePerson(request);
		ActionResult<ActionListAppSearchFilterForDocStatus.Wo> result =
				((ActionListAppSearchFilterForDocStatus)proxy
						.getProxy(ActionListAppSearchFilterForDocStatus.class))
						.execute(request, currentPerson, "draft", categoryId);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取用户有权限访问的所有已归档文档分类列表.", action = ActionListAppSearchFilterForDocStatus.class)
	@GET
	@Path("list/archive/filter/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listArchivedAppSearchFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分类ID") @PathParam("categoryId") String categoryId) {
		EffectivePerson currentPerson = this.effectivePerson(request);
		ActionResult<ActionListAppSearchFilterForDocStatus.Wo> result =
				((ActionListAppSearchFilterForDocStatus)proxy
						.getProxy(ActionListAppSearchFilterForDocStatus.class))
						.execute(request, currentPerson, "archived", categoryId);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}	
}