package com.x.processplatform.assemble.surface.jaxrs.attachment;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("attachment")
@JaxrsDescribe("附件操作")
public class AttachmentAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AttachmentAction.class);

	@JaxrsMethodDescribe(value = "测试文件是否存在.", action = ActionAvailable.class)
	@GET
	@Path("{id}/available")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void available(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionAvailable.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAvailable().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据Work和附件Id获取单个附件信息.", action = ActionGetWithWork.class)
	@GET
	@Path("{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionGetWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWork().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted和附件Id获取单个附件信息", action = ActionGetWithWorkCompleted.class)
	@GET
	@Path("{id}/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionGetWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据Work获取Attachment列表.", action = ActionListWithWork.class)
	@GET
	@Path("list/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<List<ActionListWithWork.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWork().execute(effectivePerson, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted获取Attachment列表.", action = ActionListWithWorkCompleted.class)
	@GET
	@Path("list/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<List<ActionListWithWorkCompleted.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWorkCompleted().execute(effectivePerson, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据工作或已完成工作获取Attachment列表.", action = ActionListWithWorkOrWorkCompleted.class)
	@GET
	@Path("list/workorworkcompleted/{workOrWorkCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
		ActionResult<List<ActionListWithWorkOrWorkCompleted.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "删除指定work下的附件.", action = ActionDelete.class)
	@DELETE
	@Path("{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "删除指定workCompleted下的附件. ", action = ActionDeleteWithWorkCompleted.class)
	@DELETE
	@Path("{id}/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								   @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
								   @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<ActionDeleteWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件", action = ActionDownloadWithWork.class)
	@GET
	@Path("download/{id}/work/{workId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWork().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件,匹配文件有个扩展名", action = ActionDownloadWithWork.class)
	@GET
	@Path("download/{id}/work/{workId}/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWork().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件,设定是否使用stream输出", action = ActionDownloadWithWorkStream.class)
	@GET
	@Path("download/{id}/work/{workId}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkStream(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件,设定是否使用stream输出.匹配文件有个扩展名", action = ActionDownloadWithWorkStream.class)
	@GET
	@Path("download/{id}/work/{workId}/stream/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件", action = ActionDownloadWithWorkCompleted.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件,匹配文件有个扩展名.", action = ActionDownloadWithWorkCompleted.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompletedWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件", action = ActionDownloadWithWorkCompletedStream.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompletedStream(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件,匹配文件有个扩展名.", action = ActionDownloadWithWorkCompletedStream.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/stream/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompletedStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "上传附件.", action = ActionUpload.class)
	@POST
	@Path("upload/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void upload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpload().execute(effectivePerson, workId, site, fileName, bytes, disposition,
					extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "上传附件.", action = ActionUploadWithWorkCompleted.class)
	@POST
	@Path("upload/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void uploadWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUploadWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadWithWorkCompleted().execute(effectivePerson, workCompletedId, site, fileName,
					bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));

	}

	@JaxrsMethodDescribe(value = "上传附件.", action = ActionUploadCallback.class)
	@POST
	@Path("upload/work/{workId}/callback/{callback}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.TEXT_HTML_UTF_8)
	public void uploadCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("回调函数名") @PathParam("callback") String callback,
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUploadCallback.Wo<ActionUploadCallback.WoObject>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadCallback().execute(effectivePerson, workId, callback, site, fileName, bytes,
					disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新附件.", action = ActionUpdate.class)
	@PUT
	@Path("update/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void update(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新附件,使用callback方式,为了与前台兼容使用POST方法.", action = ActionUpdateCallback.class)
	@POST
	@Path("update/{id}/work/{workId}/callback/{callback}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.TEXT_HTML_UTF_8)
	public void updateCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("回调函数名") @PathParam("callback") String callback,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpdateCallback.Wo<ActionUpdateCallback.WoObject>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateCallback().execute(effectivePerson, id, workId, callback, fileName, bytes,
					disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	/** 与update方法同,为了兼容ntko对于附件上传只能设置post方法 */
	@JaxrsMethodDescribe(value = "更新附件.", action = ActionUpdate.class)
	@POST
	@Path("update/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void updatePost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "由指定的工作拷贝附件.", action = ActionCopyToWork.class)
	@POST
	@Path("copy/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void copyToWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<List<ActionCopyToWork.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCopyToWork().execute(effectivePerson, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新附件.", action = ActionChangeSite.class)
	@GET
	@Path("{id}/work/{workId}/change/site/{site}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changeSite(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("位置") @PathParam("site") String site) {
		ActionResult<ActionChangeSite.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionChangeSite().execute(effectivePerson, id, workId, site);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新附件.", action = ActionEdit.class)
	@PUT
	@Path("edit/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void edit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, id, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新附件文本.", action = ActionEditText.class)
	@PUT
	@Path("edit/{id}/work/{workId}/text")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void exitText(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionEditText.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEditText().execute(effectivePerson, id, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取附件文本.", action = ActionGetText.class)
	@GET
	@Path("{id}/work/{workId}/text")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getText(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionGetText.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetText().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}