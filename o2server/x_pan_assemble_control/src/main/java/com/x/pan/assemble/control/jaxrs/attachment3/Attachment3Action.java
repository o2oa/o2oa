package com.x.pan.assemble.control.jaxrs.attachment3;

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
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * @author sword
 */
@Path("attachment3")
@JaxrsDescribe("共享区文件")
public class Attachment3Action extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(Attachment3Action.class);

	@JaxrsMethodDescribe(value = "获取指定文件.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取图片设定宽高后的base64内容(png格式).", action = ActionGetImageWidthHeightBase64.class)
	@GET
	@Path("{id}/image/width/{width}/height/{height}/binary/base64")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getImageWidthHeightBase64(@Suspended final AsyncResponse asyncResponse,
					  @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
					  @JaxrsParameterDescribe("宽度") @PathParam("width") Integer width,
					  @JaxrsParameterDescribe("高度") @PathParam("height") Integer height) {
		ActionResult<ActionGetImageWidthHeightBase64.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetImageWidthHeightBase64().execute(effectivePerson, id, width, height);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新文件名称", action = ActionUpdateName.class)
	@POST
	@Path("{id}/update/name")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateName(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("附件标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionUpdateName.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateName().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "转移文件（不能跨共享区转移）", action = ActionMove.class)
	@POST
	@Path("{id}/move")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void move(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					 @JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionMove.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionMove().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除附件.", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取指定文件夹下的文件.", action = ActionListWithFolder.class)
	@GET
	@Path("list/folder/{folderId}/order/by/{orderBy}/desc/{desc}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFolder(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("目录标识") @PathParam("folderId") String folderId,
			@JaxrsParameterDescribe("排序字段(name:名称；updateTime:最后修改时间；length:附件大小)") @PathParam("orderBy") String orderBy,
			@JaxrsParameterDescribe("是否倒叙(true:倒叙；false:升序)") @PathParam("desc") Boolean desc) {
		ActionResult<List<ActionListWithFolder.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithFolder().execute(effectivePerson, folderId, orderBy, desc);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据文件名称查找附件.", action = ActionListWithFilter.class)
	@GET
	@Path("list/filter/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   @JaxrsParameterDescribe("目录标识") @PathParam("name") String name) {
		ActionResult<List<ActionListWithFilter.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithFilter().execute(effectivePerson, name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据文件的md5值查找附件.", action = ActionCheckFileExist.class)
	@GET
	@Path("exist/file/{fileMd5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void checkFileExist(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   @JaxrsParameterDescribe("附件md5值") @PathParam("fileMd5") String fileMd5) {
		ActionResult<ActionCheckFileExist.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCheckFileExist().execute(effectivePerson, fileMd5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "校验附件是否符合上传条件.", action = ActionCheckUpload.class)
	@POST
	@Path("check/file/upload")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void checkFileUpload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								JsonElement jsonElement) {
		ActionResult<ActionCheckUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCheckUpload().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取附件内容,输出头信息", action = ActionDownload.class)
	@GET
	@Path("{id}/download")
	@Consumes(MediaType.APPLICATION_JSON)
	public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownload().execute(response, effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取附件内容.不输出头信息", action = ActionDownloadStream.class)
	@GET
	@Path("{id}/download/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionDownloadStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadStream().execute(response, effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取指定版本的附件内容,输出头信息", action = ActionDownloadVersion.class)
	@GET
	@Path("{id}/download/version/{version}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadVersion(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						 		@Context HttpServletResponse response,
						 		@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
								@JaxrsParameterDescribe("版本") @PathParam("version") Integer version) {
		ActionResult<ActionDownloadVersion.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadVersion().execute(effectivePerson, id, version);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "onlyOffice获取指定版本的差异文件", action = ActionDownloadVersionDiff.class)
	@GET
	@Path("{id}/download/version/{version}/diff")
	@Consumes(MediaType.APPLICATION_JSON)
	public void ActionDownloadVersionDiff(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								@Context HttpServletResponse response,
								@JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
								@JaxrsParameterDescribe("版本") @PathParam("version") Integer version) {
		ActionResult<ActionDownloadVersionDiff.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadVersionDiff().execute(effectivePerson, id, version);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/** 为IOS访问单独增加的方法 */
	@JaxrsMethodDescribe(value = "获取附件内容,输出头信息,使用POST方法访问.", action = ActionDownload.class)
	@POST
	@Path("{id}/download")
	@Consumes(MediaType.APPLICATION_JSON)
	public void postDownload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                             @Context HttpServletResponse response,
                             @JaxrsParameterDescribe("附件标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownload().execute(response, effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/** 为IOS访问单独增加的方法 */
	@JaxrsMethodDescribe(value = "获取附件内容.不输出头信息,使用POST方法访问.", action = ActionDownloadStream.class)
	@POST
	@Path("{id}/download/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void postDownloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                   @Context HttpServletResponse response,
                                   @JaxrsParameterDescribe("附件标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionDownloadStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadStream().execute(response, effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传文件", action = ActionUpload.class)
	@POST
	@Path("upload/folder/{folderId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void upload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("共享区或者目录ID") @PathParam("folderId") String folderId,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("附件md5值") @FormDataParam("fileMd5") String fileMd5,
			@JaxrsParameterDescribe("上传文件") @FormDataParam(FILE_FIELD) final FormDataBodyPart part) {
		ActionResult<ActionUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpload().execute(effectivePerson, folderId, fileName, fileMd5, part);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "创建空的office文件，仅支持docx、xlsx、pptx文件。", action = ActionCreate.class)
	@POST
	@Path("create/folder/{folderId}/name/{fileName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createOfficeFile(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						   @JaxrsParameterDescribe("共享区或者目录ID") @PathParam("folderId") String folderId,
						   @JaxrsParameterDescribe("文件名称") @PathParam("fileName") String fileName) {
		ActionResult<ActionCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreate().execute(effectivePerson, folderId, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "预览文件,输出df文件头信息，支持word、ppt、excel、pdf类型文件", action = ActionOfficePreview.class)
	@GET
	@Path("{id}/office/preview")
	@Consumes(MediaType.APPLICATION_JSON)
	public void officePreview(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							  @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionOfficePreview.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionOfficePreview().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页查找网盘文件(仅网盘管理员).", action = ActionManagerListPaging.class)
	@POST
	@Path("list/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void managerListWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
							   @JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size,
							   JsonElement jsonElement) {
		ActionResult<List<ActionManagerListPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionManagerListPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "onlyOffice预览文件回调", action = ActionPreviewFileInfo.class)
	@GET
	@Path("preview/file/info")
	@Consumes(MediaType.APPLICATION_JSON)
	public void previewFileInfoByOnlyOffice(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							  @JaxrsParameterDescribe("附件标识") @QueryParam("fileId") String fileId) {
		String result = "{'result':10001, 'msg':'服务异常'}";
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionPreviewFileInfo().execute(effectivePerson, fileId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(Response.ok(result).build());
	}

	@JaxrsMethodDescribe(value = "onlyOffice在线编辑回调获取文件信息", action = ActionEditFileInfo.class)
	@GET
	@Path("edit/file/info/only/office")
	@Consumes(MediaType.APPLICATION_JSON)
	public void editFileInfoByOnlyOffice(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
										@JaxrsParameterDescribe("附件标识") @QueryParam("fileId") String fileId) {
		String result = "{'result':10001, 'msg':'服务异常'}";
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEditFileInfo().execute(effectivePerson, fileId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(Response.ok(result).build());
	}

	@JaxrsMethodDescribe(value = "onlyOffice在线编辑回调保存文件", action = ActionSaveByOnlyOffice.class)
	@POST
	@Path("3rd/file/save/only/office")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveFileByOnlyOffice(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									 @JaxrsParameterDescribe("附件标识") @QueryParam("fileId") String fileId,
									 JsonElement jsonElement) {
		String result = "{'result':10001, 'msg':'服务异常'}";
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSaveByOnlyOffice().execute(effectivePerson, fileId, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
		}
		asyncResponse.resume(Response.ok(result).build());
	}

	@JaxrsMethodDescribe(value = "获取指定文件的历史版本.", action = ActionListHistory.class)
	@GET
	@Path("{id}/list/history")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listHistory(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   @JaxrsParameterDescribe("文件标识") @PathParam("id") String id) {
		ActionResult<List<ActionListHistory.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListHistory().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取指定附件的在线编辑信息.", action = ActionOnlineInfo.class)
	@GET
	@Path("{id}/online/info")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getOnlineInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionOnlineInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionOnlineInfo().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "第三方在线编辑回调保存文件", action = ActionSaveByOnlyOffice.class)
	@POST
	@Path("third/file/save")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveFileByThird(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		String result = "{'result':10001, 'msg':'服务异常'}";
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSaveByThird().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
		}
		asyncResponse.resume(Response.ok(result).build());
	}

}
