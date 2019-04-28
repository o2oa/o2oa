package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.queue.DataImportStatus;

@Path("document")
@JaxrsDescribe("信息发布信息文档管理")
public class DocumentAction extends StandardJaxrsAction{
	
	private static  Logger logger = LoggerFactory.getLogger( DocumentAction.class );

	@JaxrsMethodDescribe(value = "变更指定文档的分类信息.", action = ActionChangeCategory.class)
	@PUT
	@Path("category/change")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeCategory( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionChangeCategory.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			try {
				result = new ActionChangeCategory().execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "指修改指定文档的数据.", action = ActionBatchModifyDataWithIds.class)
	@PUT
	@Path("batch/data/modify")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response batchDataModify( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionBatchModifyDataWithIds.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionBatchModifyDataWithIds().execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据导入批次号查询导入状态信息.", action = ActionCehckImportStatus.class)
	@GET
	@Path("batch/{batchName}/status")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkImportStatus(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("导入批次号") @PathParam("batchName") String batchName) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<DataImportStatus> result = new ActionResult<>();
		try {
			result = new ActionCehckImportStatus().execute( request, effectivePerson, batchName );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "查询所有的导入状态信息.", action = ActionCheckAllImportStatus.class)
	@GET
	@Path("batch/status")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkAllImportStatus(@Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<DataImportStatus>> result = new ActionResult<>();
		try {
			result = new ActionCheckAllImportStatus().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID获取信息发布文档信息对象详细信息，包括附件列表，数据信息.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据标识强制更新指定的分类Review.", action = ActionUpdateReviewForce.class)
	@GET
	@Path("review/{id}/update")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateReviewForce(@Context HttpServletRequest request, @JaxrsParameterDescribe("文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WoId> result = new ActionResult<>();
		try {
			result = new ActionUpdateReviewForce().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionDocumentInfoProcess(e, "根据指定ID强制更新指定的分类Review时发生异常。id:" + id );
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID访问信息发布文档信息对象详细信息，包括附件列表，数据信息.", action = ActionView.class)
	@GET
	@Path("{id}/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response view(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionView.Wo> result = new ActionResult<>();
		try {
			result = new ActionView().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID获取信息发布文档信息被访问次数.", action = ActionCountViewTimes.class)
	@GET
	@Path("{id}/view/count")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getViewCount(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionCountViewTimes.Wo> result = new ActionResult<>();
		try {
			result = new ActionCountViewTimes().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "查询符合过滤条件的已发布的信息数量.", action = ActionCountLatestDocumentWithFilter.class)
	@PUT
	@Path("filter/count")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countDocumentWithFilter( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionCountLatestDocumentWithFilter.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionCountLatestDocumentWithFilter().execute( request,  jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID删除信息发布文档信息.", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据批次号删除信息发布文档信息.", action = ActionDeleteWithImportBatchName.class)
	@DELETE
	@Path("batch/{batchId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithBatchName(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("batchId") String batchId) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionDeleteWithImportBatchName.Wo> result = new ActionResult<>();
		try {
			result = new ActionDeleteWithImportBatchName().execute( request, batchId, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID归档信息发布文档信息.", action = ActionArchive.class)
	@PUT
	@Path("achive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response achive(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionArchive.Wo> result = new ActionResult<>();
		try {
			result = new ActionArchive().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID修改信息发布文档状态为已发布.", action = ActionPublish.class)
	@PUT
	@Path("publish/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response publish(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id, JsonElement jsonElement ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPublish.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionPublish().execute( request, id, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "直接发布信息内容，创建新的信息发布文档并且直接发布.", action = ActionPublishContent.class)
	@PUT
	@Path("publish/content")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response publishContent(@Context HttpServletRequest request, JsonElement jsonElement ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPublishContent.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			System.out.println( "please wait, system try to publish content......" );
			try {
				result = new ActionPublishContent().execute( request, jsonElement, effectivePerson );
				System.out.println( "system publish content successful!" );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID取消信息内容发布状态，修改为草稿.", action = ActionPublishCancel.class)
	@PUT
	@Path("publish/{id}/cancel")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response publishCancel(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPublishCancel.Wo> result = new ActionResult<>();
		try {
			result = new ActionPublishCancel().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "列示符合过滤条件的已发布的信息内容, 下一页.", action = ActionListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionListNextWithFilter().execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "列示符合过滤条件的草稿信息内容, 下一页.", action = ActionListDraftNextWithFilter.class)
	@PUT
	@Path("draft/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftNextWithFilter( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListDraftNextWithFilter.Wo>> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionListDraftNextWithFilter().execute( request, id, count, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "根据信息发布文档ID查询文档第一张图片信息列表.", action = ActionGetFirstPicture.class)
	@GET
	@Path("pictures/{id}/first")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listFirstPictures( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionGetFirstPicture.Wo> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionGetFirstPicture().execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "根据信息发布文档ID查询文档所有的图片信息列表.", action = ActionListAllPictures.class)
	@GET
	@Path("pictures/{id}/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllPictures( @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("id") String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListAllPictures.Wo>> result = new ActionResult<>();
		Boolean check = true;
		if( check ){
			try {
				result = new ActionListAllPictures().execute( request, id, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "从Excel文件导入文档数据.", action = ActionImportDataExcel.class)
	@POST
	@Path("import/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void importDocumentFormExcel(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("分类ID") @PathParam("categoryId") String categoryId, 
			@JaxrsParameterDescribe("作为参数的JSON字符串") @FormDataParam("json_data") String json_data,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionImportDataExcel.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImportDataExcel().execute(request, effectivePerson, categoryId, bytes,  json_data, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "保存信息发布文档信息对象.", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			try {
				result = new ActionSave().execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}