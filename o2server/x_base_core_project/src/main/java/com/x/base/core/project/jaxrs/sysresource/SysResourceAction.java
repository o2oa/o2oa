package com.x.base.core.project.jaxrs.sysresource;

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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("sysresource")
@JaxrsDescribe("系统资源")
public class SysResourceAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(SysResourceAction.class);

	@JaxrsMethodDescribe(value = "上传静态资源(仅上传到当前服务器)", action = ActionUploadResource.class)
	@POST
	@Path("upload/resource/as/new/{asNew}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void uploadResource(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							   @JaxrsParameterDescribe("覆盖类型：true删除原文件然后上传，false覆盖原文件") @PathParam("asNew") Boolean asNew,
							   @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
							   @JaxrsParameterDescribe("附件存放目录(可以为空)") @FormDataParam("filePath") String filePath,
							   @JaxrsParameterDescribe("附件标识") @FormDataParam(FILE_FIELD) final byte[] bytes,
							   @JaxrsParameterDescribe("上传文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUploadResource.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadResource().execute(effectivePerson, asNew, fileName, filePath, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取静态资源信息.", action = ActionListResource.class)
	@GET
	@Path("filePath/{filePath}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listResource(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					@JaxrsParameterDescribe("查找路径(根路径:(0))") @PathParam("filePath") String filePath) {
		ActionResult<ActionListResource.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListResource().execute(effectivePerson, filePath);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}