package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.proxy.StandardJaxrsActionProxy;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.queue.DataImportStatus;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("document")
@JaxrsDescribe("信息发布信息文档管理")
public class DocumentAction extends StandardJaxrsAction{

	private StandardJaxrsActionProxy proxy = new StandardJaxrsActionProxy(ThisApplication.context());
	private static  Logger logger = LoggerFactory.getLogger( DocumentAction.class );

	@JaxrsMethodDescribe(value = "变更指定文档的分类信息.", action = ActionPersistChangeCategory.class)
	@PUT
	@Path("category/change")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_changeCategory( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistChangeCategory.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			try {
				result = ((ActionPersistChangeCategory)proxy.getProxy(ActionPersistChangeCategory.class)).execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "指修改指定文档的数据。", action = ActionPersistBatchModifyData.class)
	@PUT
	@Path("batch/data/modify")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_batchDataModify( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistBatchModifyData.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionPersistBatchModifyData)proxy.getProxy(ActionPersistBatchModifyData.class)).execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据导入批次号查询导入状态信息.", action = ActionQueryImportStatusWithName.class)
	@GET
	@Path("batch/{batchName}/status")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_checkImportStatus( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("导入批次号") @PathParam("batchName") String batchName) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<DataImportStatus> result = new ActionResult<>();
		try {
			result = ((ActionQueryImportStatusWithName)proxy.getProxy(ActionQueryImportStatusWithName.class)).execute( request, effectivePerson, batchName );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "查询所有的导入状态信息.", action = ActionQueryAllImportStatus.class)
	@GET
	@Path("batch/status")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_checkAllImportStatus( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<DataImportStatus>> result = new ActionResult<>();
		try {
			result = ((ActionQueryAllImportStatus)proxy.getProxy(ActionQueryAllImportStatus.class)).execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID获取信息发布文档信息对象详细信息，包括附件列表，数据信息.", action = ActionQueryGetDocument.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryGetDocument.Wo> result = new ActionResult<>();
		try {
			result = ((ActionQueryGetDocument)proxy.getProxy(ActionQueryGetDocument.class)).execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "列示文档对象可供排序和展示使用的列名.", action = ActionQueryListDocumentFields.class)
	@GET
	@Path("document/fields")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listDocumentFields( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryListDocumentFields.Wo> result = new ActionResult<>();
		try {
			result = ((ActionQueryListDocumentFields)proxy.getProxy(ActionQueryListDocumentFields.class)).execute( request );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	
	@JaxrsMethodDescribe(value = "根据ID访问信息发布文档信息对象详细信息，包括附件列表，数据信息.", action = ActionQueryViewDocument.class)
	@GET
	@Path("{id}/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_view( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryViewDocument.Wo> result = new ActionResult<>();
		try {
			result = ((ActionQueryViewDocument)proxy.getProxy(ActionQueryViewDocument.class)).execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID获取信息发布文档信息被访问次数.", action = ActionQueryCountViewTimes.class)
	@GET
	@Path("{id}/view/count")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_getViewCount( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryCountViewTimes.Wo> result = new ActionResult<>();
		try {
			result = ((ActionQueryCountViewTimes)proxy.getProxy(ActionQueryCountViewTimes.class)).execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "查询符合过滤条件的已发布的信息数量.", action = ActionQueryCountWithFilter.class)
	@PUT
	@Path("filter/count")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_countDocumentWithFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryCountWithFilter.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = ((ActionQueryCountWithFilter)proxy.getProxy(ActionQueryCountWithFilter.class)).execute( request,  jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID删除信息发布文档信息.", action = ActionPersistDeleteDocument.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_delete( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistDeleteDocument.Wo> result = new ActionResult<>();
		try {
			result = ((ActionPersistDeleteDocument)proxy.getProxy(ActionPersistDeleteDocument.class)).execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据批次号删除信息发布文档信息.", action = ActionPersistDeleteWithBatch.class)
	@DELETE
	@Path("batch/{batchId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_deleteWithBatchName( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("batchId") String batchId) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistDeleteWithBatch.Wo> result = new ActionResult<>();
		try {
			result = ((ActionPersistDeleteWithBatch)proxy.getProxy(ActionPersistDeleteWithBatch.class)).execute( request, batchId, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID归档信息发布文档信息.", action = ActionPersistArchive.class)
	@GET
	@Path("achive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_achive( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistArchive.Wo> result = new ActionResult<>();
		try {
			result = ((ActionPersistArchive)proxy.getProxy(ActionPersistArchive.class)).execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据文档ID正式发布文档，并且使用Message通知所有的阅读者。", action = ActionPersistPublishAndNotify.class)
	@PUT
	@Path("publish/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_publishAndNotify( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("文档ID") @PathParam("id") String id, JsonElement jsonElement ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistPublishAndNotify.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = ((ActionPersistPublishAndNotify)proxy.getProxy(ActionPersistPublishAndNotify.class)).execute( request, id, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "直接发布信息内容，创建新的信息发布文档并且直接发布.", action = ActionPersistPublishContent.class)
	@PUT
	@Path("publish/content")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_publishContent( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistPublishContent.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			System.out.println( "please wait, system try to publish content......" );
			try {
				result = ((ActionPersistPublishContent)proxy.getProxy(ActionPersistPublishContent.class)).execute( request, jsonElement, effectivePerson );
				System.out.println( "system publish content successful!" );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID取消信息内容发布状态，修改为草稿.", action = ActionPersistPublishCancel.class)
	@PUT
	@Path("publish/{id}/cancel")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_publishCancel( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistPublishCancel.Wo> result = new ActionResult<>();
		try {
			result = ((ActionPersistPublishCancel)proxy.getProxy(ActionPersistPublishCancel.class)).execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 下一页.", action = ActionQueryListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listNextWithFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = ((ActionQueryListNextWithFilter)proxy.getProxy(ActionQueryListNextWithFilter.class)).execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 上一页.", action = ActionQueryListPrevWithFilter.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listPrevWithFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
										  @JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id,
										  @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count,
										  JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListPrevWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = ((ActionQueryListPrevWithFilter)proxy.getProxy(ActionQueryListPrevWithFilter.class)).execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "列示符合过滤条件的草稿信息内容, 下一页.", action = ActionQueryListDraftNextWithFilter.class)
	@PUT
	@Path("draft/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listDraftNextWithFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListDraftNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = ((ActionQueryListDraftNextWithFilter)proxy.getProxy(ActionQueryListDraftNextWithFilter.class)).execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据信息发布文档ID查询文档第一张图片信息列表.", action = ActionQueryGetFirstPicture.class)
	@GET
	@Path("pictures/{id}/first")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listFirstPictures( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryGetFirstPicture.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionQueryGetFirstPicture)proxy.getProxy(ActionQueryGetFirstPicture.class)).execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据信息发布文档ID查询文档所有的图片信息列表.", action = ActionQueryListAllPictures.class)
	@GET
	@Path("pictures/{id}/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listAllPictures( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListAllPictures.Wo>> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionQueryListAllPictures)proxy.getProxy(ActionQueryListAllPictures.class)).execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "从Excel文件导入文档数据.", action = ActionPersistImportDataExcel.class)
	@POST
	@Path("import/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void persist_importDocumentFormExcel(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("分类ID") @PathParam("categoryId") String categoryId, 
			@JaxrsParameterDescribe("作为参数的JSON字符串") @FormDataParam("json_data") String json_data,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionPersistImportDataExcel.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = ((ActionPersistImportDataExcel)proxy.getProxy(ActionPersistImportDataExcel.class)).execute(request, effectivePerson, categoryId, bytes,  json_data, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "保存信息发布文档信息对象.", action = ActionPersistSaveDocument.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_save( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistSaveDocument.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			try {
				result = ((ActionPersistSaveDocument)proxy.getProxy(ActionPersistSaveDocument.class)).execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "文档点赞.", action = ActionPersistCommend.class)
	@GET
	@Path("{id}/commend")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_commend( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistCommend.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionPersistCommend)proxy.getProxy(ActionPersistCommend.class)).execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "取消文档点赞.", action = ActionPersistUnCommend.class)
	@GET
	@Path("{id}/uncommend")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_unCommend( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistUnCommend.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionPersistUnCommend)proxy.getProxy(ActionPersistUnCommend.class)).execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "文档置顶.", action = ActionPersistTopDocument.class)
	@GET
	@Path("{id}/top")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_top( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistTopDocument.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionPersistTopDocument)proxy.getProxy(ActionPersistTopDocument.class)).execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "取消文档置顶.", action = ActionPersistUnTopDocument.class)
	@GET
	@Path("{id}/unTop")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void persist_unTop( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistUnTopDocument.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionPersistUnTopDocument)proxy.getProxy(ActionPersistUnTopDocument.class)).execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页查询符合过滤条件的已发布的信息内容.", action = ActionQueryListWithFilterPaging.class)
	@PUT
	@Path("filter/list/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_listWithFilterPaging( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
											@JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionQueryListWithFilterPaging.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = ((ActionQueryListWithFilterPaging)proxy.getProxy(ActionQueryListWithFilterPaging.class)).execute( request, page, size, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "获取文件访问控制信息.", action = ActionQueryGetControl.class)
	@GET
	@Path("{id}/control")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_getControl( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryGetControl.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionQueryGetControl)proxy.getProxy(ActionQueryGetControl.class)).execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "获取文件可见范围内的所有人员.", action = ActionQueryListVisiblePersons.class)
	@GET
	@Path("{id}/persons")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void query_getVisiblePersons( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionQueryListVisiblePersons.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = ((ActionQueryListVisiblePersons)proxy.getProxy(ActionQueryListVisiblePersons.class)).execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}