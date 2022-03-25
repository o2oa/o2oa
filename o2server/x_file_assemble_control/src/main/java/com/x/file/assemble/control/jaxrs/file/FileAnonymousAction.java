package com.x.file.assemble.control.jaxrs.file;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * @author sword
 */
@Path("anonymous/file")
@JaxrsDescribe("File操作")
public class FileAnonymousAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(FileAnonymousAction.class);

	@JaxrsMethodDescribe(value = "下载文件,以附件方式,不输出contentType头.", action = ActionDownloadStream.class)
	@GET
	@Path("{id}/download/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文件标识") @PathParam("id") String id) {
		ActionResult<ActionDownloadStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadStream().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "下载文件,输出contentType头.", action = ActionDownload.class)
	@GET
	@Path("{id}/download")
	@Consumes(MediaType.APPLICATION_JSON)
	public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文件标识") @PathParam("id") String id) {
		ActionResult<ActionDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownload().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/** 为IOS访问单独增加的方法 */
	@JaxrsMethodDescribe(value = "下载文件,以附件方式,不输出contentType头,使用POST方法访问.", action = ActionDownloadStream.class)
	@POST
	@Path("{id}/download/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void postDownloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文件标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionDownloadStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadStream().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/** 为IOS访问单独增加的方法 */
	@JaxrsMethodDescribe(value = "下载文件,输出contentType头,使用POST方法访问.", action = ActionDownload.class)
	@POST
	@Path("{id}/download")
	@Consumes(MediaType.APPLICATION_JSON)
	public void postDownload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文件标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownload().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
