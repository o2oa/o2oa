package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted和附件Id获取单个附件信息", action = ActionGetWithWorkOrWorkCompleted.class)
	@GET
	@Path("{id}/workorworkcompleted/{workOrWorkCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionGetWithWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkOrWorkCompleted().execute(effectivePerson, id, workOrWorkCompleted);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据工作的job获取Attachment列表.", action = ActionListWithJob.class)
	@GET
	@Path("list/job/{job}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithJob(@Suspended final AsyncResponse asyncResponse,
											@Context HttpServletRequest request,
											@JaxrsParameterDescribe("工作的job") @PathParam("job") String job) {
		ActionResult<List<ActionListWithJob.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithJob().execute(effectivePerson, job);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除指定work下的附件.", action = ActionDeleteWithWork.class)
	@DELETE
	@Path("{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWork().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "Mock Get To Delete.", action = ActionDeleteWithWork.class)
	@GET
	@Path("{id}/work/{workId}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWork().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除指定workCompleted下的附件. ", action = ActionDeleteWithWorkCompleted.class)
	@DELETE
	@Path("{id}/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<ActionDeleteWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "Mock Get To Delete.", action = ActionDeleteWithWorkCompleted.class)
	@GET
	@Path("{id}/workcompleted/{workCompletedId}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteWithWorkCompletedMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<ActionDeleteWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "下载指定附件", action = ActionDownload.class)
	@GET
	@Path("download/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								 @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
								 @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownload().execute(effectivePerson, id, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "下载指定附件,设定用stream输出", action = ActionDownloadStream.class)
	@GET
	@Path("download/{id}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						 @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
						 @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadStream().execute(effectivePerson, id, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件", action = ActionDownloadWithWork.class)
	@GET
	@Path("download/{id}/work/{workId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWork().execute(effectivePerson, id, workId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件,匹配文件有个扩展名", action = ActionDownloadWithWork.class)
	@GET
	@Path("download/{id}/work/{workId}/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWork().execute(effectivePerson, id, workId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件,设定是否使用stream输出", action = ActionDownloadWithWorkStream.class)
	@GET
	@Path("download/{id}/work/{workId}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkStream(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work下载附件,设定是否使用stream输出.匹配文件有个扩展名", action = ActionDownloadWithWorkStream.class)
	@GET
	@Path("download/{id}/work/{workId}/stream/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件", action = ActionDownloadWithWorkCompleted.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件,匹配文件有个扩展名.", action = ActionDownloadWithWorkCompleted.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompletedWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件", action = ActionDownloadWithWorkCompletedStream.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompletedStream(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId,
					fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据WorkCompleted下载附件,匹配文件有个扩展名.", action = ActionDownloadWithWorkCompletedStream.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/stream/{fileName}.{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompletedStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
		ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId,
					fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传附件.", action = ActionUploadWithWork.class)
	@POST
	@Path("upload/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void upload(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUploadWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			if (StringUtils.isEmpty(extraParam)) {
				extraParam = this.request2Json(request);
			}
			if (bytes == null) {
				Map<String, List<FormDataBodyPart>> map = form.getFields();
				for (String key : map.keySet()) {
					FormDataBodyPart part = map.get(key).get(0);
					if ("application".equals(part.getMediaType().getType())) {
						bytes = part.getValueAs(byte[].class);
						break;
					}
				}
			}
			result = new ActionUploadWithWork().execute(effectivePerson, workId, site, fileName, bytes, disposition,
					extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传附件.", action = ActionUploadWithWorkCompleted.class)
	@POST
	@Path("upload/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void uploadWithWorkCompleted(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUploadWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			if (StringUtils.isEmpty(extraParam)) {
				extraParam = this.request2Json(request);
			}
			if (bytes == null) {
				Map<String, List<FormDataBodyPart>> map = form.getFields();
				for (String key : map.keySet()) {
					FormDataBodyPart part = map.get(key).get(0);
					if ("application".equals(part.getMediaType().getType())) {
						bytes = part.getValueAs(byte[].class);
						break;
					}
				}
			}
			result = new ActionUploadWithWorkCompleted().execute(effectivePerson, workCompletedId, site, fileName,
					bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));

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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新附件.", action = ActionUpdate.class)
	@PUT
	@Path("update/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void update(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) byte[] bytes,
			@JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			if (StringUtils.isEmpty(extraParam)) {
				extraParam = this.request2Json(request);
			}
			if (bytes == null) {
				Map<String, List<FormDataBodyPart>> map = form.getFields();
				for (String key : map.keySet()) {
					FormDataBodyPart part = map.get(key).get(0);
					if ("application".equals(part.getMediaType().getType())) {
						bytes = part.getValueAs(byte[].class);
						break;
					}
				}
			}
			result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "Mock Get To Delete.", action = ActionUpdate.class)
	@POST
	@Path("update/{id}/work/{workId}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void updateMockPutToPost(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) byte[] bytes,
			@JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			if (StringUtils.isEmpty(extraParam)) {
				extraParam = this.request2Json(request);
			}
			if (bytes == null) {
				Map<String, List<FormDataBodyPart>> map = form.getFields();
				for (String key : map.keySet()) {
					FormDataBodyPart part = map.get(key).get(0);
					if ("application".equals(part.getMediaType().getType())) {
						bytes = part.getValueAs(byte[].class);
						break;
					}
				}
			}
			result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新Attachment信息", action = ActionUpdateContent.class)
	@PUT
	@Path("update/content/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateContent(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionUpdateContent.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateContent().execute(effectivePerson, id, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "Mock Get To Delete.", action = ActionUpdateContent.class)
	@POST
	@Path("update/content/{id}/work/{workId}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateContentMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionUpdateContent.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateContent().execute(effectivePerson, id, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/** 与update方法同,为了兼容ntko对于附件上传只能设置post方法 */
	@JaxrsMethodDescribe(value = "更新附件.", action = ActionUpdate.class)
	@POST
	@Path("update/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void updatePost(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) byte[] bytes,
			@JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			if (StringUtils.isEmpty(extraParam)) {
				extraParam = this.request2Json(request);
			}
			if (bytes == null) {
				Map<String, List<FormDataBodyPart>> map = form.getFields();
				for (String key : map.keySet()) {
					FormDataBodyPart part = map.get(key).get(0);
					if ("application".equals(part.getMediaType().getType())) {
						bytes = part.getValueAs(byte[].class);
						break;
					}
				}
			}
			result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "由指定的工作拷贝附件.", action = ActionCopyToWorkCompleted.class)
	@POST
	@Path("copy/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void copyToWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
			JsonElement jsonElement) {
		ActionResult<List<ActionCopyToWorkCompleted.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCopyToWorkCompleted().execute(effectivePerson, workCompletedId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "拷贝附件到指定的工作(不拷贝真实存储附件，共用附件，此接口拷贝的附件删除时只删除记录不删附件).", action = ActionCopyToWorkSoft.class)
	@POST
	@Path("copy/work/{workId}/soft")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void copyToWorkSoft(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						   @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<List<ActionCopyToWorkSoft.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCopyToWorkSoft().execute(effectivePerson, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "拷贝附件到指定的工作(不拷贝真实存储附件，共用附件，此接口拷贝的附件删除时只删除记录不删附件).", action = ActionCopyToWorkCompletedSoft.class)
	@POST
	@Path("copy/workcompleted/{workCompletedId}/soft")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void copyToWorkCompletedSoft(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
									JsonElement jsonElement) {
		ActionResult<List<ActionCopyToWorkCompletedSoft.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCopyToWorkCompletedSoft().execute(effectivePerson, workCompletedId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "Mock Get To Delete.", action = ActionEdit.class)
	@POST
	@Path("edit/{id}/work/{workId}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void editMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置附件排序号.", action = ActionEdit.class)
	@GET
	@Path("{id}/work/{workId}/change/ordernumber/{orderNumber}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changeOrderNumber(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
			@PathParam("orderNumber") Integer orderNumber) {
		ActionResult<ActionChangeOrderNumber.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionChangeOrderNumber().execute(effectivePerson, id, workId, orderNumber);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "Mock Get To Delete.", action = ActionEditText.class)
	@POST
	@Path("edit/{id}/work/{workId}/text/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void exitTextMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionEditText.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEditText().execute(effectivePerson, id, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
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
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "将HTML版式公文转换成Word文件并添加在附件中.", action = ActionDocToWord.class)
	@POST
	@Path("doc/to/word/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void docToWord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
		ActionResult<ActionDocToWord.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDocToWord().execute(effectivePerson, workId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "将HTML版式公文转换成Word文件并添加在附件中,可以输入WorkId或者WorkCompletedId.", action = ActionDocToWordWorkOrWorkCompleted.class)
	@POST
	@Path("doc/to/word/workorworkcompleted/{workOrWorkCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void docToWordWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
			JsonElement jsonElement) {
		ActionResult<ActionDocToWordWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDocToWordWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted,
					jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "pdf格式预览文件,支持doc,docx.", action = ActionPreviewPdf.class)
	@GET
	@Path("{id}/preview/pdf")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void previewPdf(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionPreviewPdf.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPreviewPdf().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "pdf格式预览文件获取接口.", action = ActionPreviewPdfResult.class)
	@GET
	@Path("preview/pdf/{flag}/result")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void previewPdfResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("flag") String flag) {
		ActionResult<ActionPreviewPdfResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPreviewPdfResult().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "image格式预览文件,支持doc,docx", action = ActionPreviewImage.class)
	@GET
	@Path("{id}/preview/image/page/{page}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void previewImage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("页数") @PathParam("page") Integer page) {
		ActionResult<ActionPreviewImage.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPreviewImage().execute(effectivePerson, id, page);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "image格式预览文件获取接口.", action = ActionPreviewImageResult.class)
	@GET
	@Path("preview/image/{flag}/result")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void previewImageResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("flag") String flag) {
		ActionResult<ActionPreviewImageResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPreviewImageResult().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work或WorkCompleted批量下载附件并压缩,设定是否使用stream输出", action = ActionBatchDownloadWithWorkOrWorkCompletedStream.class)
	@GET
	@Path("batch/download/work/{workId}/site/{site}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void batchDownloadWithWorkOrWorkCompletedStream(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("*Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("*附件框分类,多值~隔开,(0)表示全部") @PathParam("site") String site,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName,
			@JaxrsParameterDescribe("通过uploadWorkInfo上传返回的工单信息存储id") @QueryParam("flag") String flag) {
		ActionResult<ActionBatchDownloadWithWorkOrWorkCompletedStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBatchDownloadWithWorkOrWorkCompletedStream().execute(effectivePerson, workId, site,
					fileName, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据Work或WorkCompleted批量下载附件并压缩", action = ActionBatchDownloadWithWorkOrWorkCompleted.class)
	@GET
	@Path("batch/download/work/{workId}/site/{site}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void batchDownloadWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("*Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("*附件框分类,多值~隔开,(0)表示全部") @PathParam("site") String site,
			@JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName,
			@JaxrsParameterDescribe("通过uploadWorkInfo上传返回的工单信息存储id") @QueryParam("flag") String flag) {
		ActionResult<ActionBatchDownloadWithWorkOrWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBatchDownloadWithWorkOrWorkCompleted().execute(effectivePerson, workId, site, fileName,
					flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传工单的表单、审批记录等html信息到缓存.", action = ActionUploadWorkInfo.class)
	@PUT
	@Path("upload/work/{workId}/save/as/{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void uploadWorkInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("另存为格式：(0)表示不转换|pdf表示转为pdf|word表示转为word") @PathParam("flag") String flag,
			JsonElement jsonElement) {
		ActionResult<ActionUploadWorkInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadWorkInfo().execute(effectivePerson, workId, flag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "Mock Get To Delete.", action = ActionUploadWorkInfo.class)
	@POST
	@Path("upload/work/{workId}/save/as/{flag}/mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void uploadWorkInfoMockPutToPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("另存为格式：(0)表示不转换|pdf表示转为pdf|word表示转为word") @PathParam("flag") String flag,
			JsonElement jsonElement) {
		ActionResult<ActionUploadWorkInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadWorkInfo().execute(effectivePerson, workId, flag, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "下载工单的表单、审批记录等html信息", action = ActionDownloadWorkInfo.class)
	@GET
	@Path("download/work/{workId}/att/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWorkInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("*Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
			@JaxrsParameterDescribe("*通过uploadWorkInfo上传返回的附件id") @PathParam("flag") String flag) {
		ActionResult<ActionDownloadWorkInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWorkInfo().execute(effectivePerson, workId, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "管理员批量上传附件.", action = ActionManageBatchUpload.class)
	@POST
	@Path("batch/upload/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void manageBatchUpload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识列表，多值逗号隔开") @FormDataParam("workIds") String workIds,
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("上传到指定用户") @FormDataParam("person") String person,
			@JaxrsParameterDescribe("附件排序号") @FormDataParam("orderNumber") Integer orderNumber,
			@JaxrsParameterDescribe("是否根据主工作软拷贝附件到其他工作，所有文档共享主文档的存储附件") @FormDataParam("isSoftUpload") Boolean isSoftUpload,
			@JaxrsParameterDescribe("主工作标志(isSoftUpload为true时必填)") @FormDataParam("mainWork") String mainWork,
			@JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionManageBatchUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageBatchUpload().execute(effectivePerson, workIds, site, fileName, bytes, disposition,
					extraParam, person, orderNumber, isSoftUpload, mainWork);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "管理员角色下载附件", action = ActionManageDownload.class)
	@GET
	@Path("download/{id}/manage")
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageDownload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionManageDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageDownload().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "管理员角色下载附件,stream", action = ActionManageDownloadStream.class)
	@GET
	@Path("download/{id}/manage/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageDownloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionManageDownloadStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageDownloadStream().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "html转pdf工具类,转换后通过downloadTransfer接口下载", action = ActionHtmlToPdf.class)
	@POST
	@Path("html/to/pdf")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void htmlToPdf(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionHtmlToPdf.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionHtmlToPdf().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "下载转换后的附件", action = ActionDownloadTransfer.class)
	@GET
	@Path("download/transfer/flag/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadTransfer(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("*转换后附件id") @PathParam("flag") String flag) {
		ActionResult<ActionDownloadTransfer.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadTransfer().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "管理员批量替换附件.", action = ActionManageBatchUpdate.class)
	@POST
	@Path("batch/update/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void manageBatchUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							 @JaxrsParameterDescribe("附件Id列表，多值逗号隔开") @FormDataParam("ids") String ids,
							 @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
							 @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
							 @FormDataParam(FILE_FIELD) final byte[] bytes,
							 @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionManageBatchUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageBatchUpdate().execute(effectivePerson, ids, fileName, bytes, disposition,
					extraParam);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "批量删除附件.", action = ActionManageBatchDelete.class)
	@POST
	@Path("batch/delete/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void manageBatchDelete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								  JsonElement jsonElement) {
		ActionResult<ActionManageBatchDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManageBatchDelete().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传附件", action = ActionUploadWithUrl.class)
	@POST
	@Path("upload/with/url")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void uploadWithUrl(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						  JsonElement jsonElement) {
		ActionResult<ActionUploadWithUrl.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadWithUrl().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "html转图片工具类，转换后如果工作不为空通过downloadWithWork接口下载，为空downloadTransfer接口下载.", action = ActionHtmlToImage.class)
	@POST
	@Path("html/to/image")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void htmlToImage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						  JsonElement jsonElement) {
		ActionResult<ActionHtmlToImage.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionHtmlToImage().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
