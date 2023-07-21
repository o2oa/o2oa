package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("document/cipher")
@JaxrsDescribe("信息发布信息文档管理(Cipher)")
public class DocumentCipherAction extends StandardJaxrsAction{

	private static  Logger logger = LoggerFactory.getLogger( DocumentCipherAction.class );

	@JaxrsMethodDescribe(value = "直接发布文档信息.", action = ActionPersistPublishByWorkFlow.class)
	@PUT
	@Path("publish/content")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void publishContentByWorkFlow( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistPublishByWorkFlow.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionPersistPublishByWorkFlow().execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "直接发布文档信息.", action = ActionPersistPublishByWorkFlow.class)
	@POST
	@Path("publish/content/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void publishContentByWorkFlowMockPutToPost( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistPublishByWorkFlow.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionPersistPublishByWorkFlow().execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "分页查询符合过滤条件的已发布的信息内容(管理员和Ciper使用).", action = ActionQueryListWithFilterPagingAdmin.class)
	@PUT
	@Path("filter/list/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listWithFilterPaging( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
											@JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListWithFilterPagingAdmin.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryListWithFilterPagingAdmin().execute( request, page, size, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页查询符合过滤条件的已发布的信息内容(管理员和Ciper使用).", action = ActionQueryListWithFilterPagingAdmin.class)
	@POST
	@Path("filter/list/{page}/size/{size}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listWithFilterPagingMockPutToPost( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
											@JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListWithFilterPagingAdmin.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryListWithFilterPagingAdmin().execute( request, page, size, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "查询某用户是否有阅读文档的权限.", action = ActionQueryPermissionReadDocument.class)
	@GET
	@Path("{id}/permission/read/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_PermissionReadDocument( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											@JaxrsParameterDescribe("文档ID") @PathParam("id") String id,
											@JaxrsParameterDescribe("用户") @PathParam("person") String person) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryPermissionReadDocument.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionQueryPermissionReadDocument().execute(effectivePerson, id, person);
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "添加文档的阅读记录.", action = ActionPersistViewRecord.class)
	@POST
	@Path("{id}/persist/view/record")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_documentViewRecord( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											@JaxrsParameterDescribe("文档ID") @PathParam("id") String id, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistViewRecord.Wo> result = new ActionResult<>();

		try {
			result = new ActionPersistViewRecord().execute( request, id, jsonElement, effectivePerson );
		} catch (Exception e) {
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
