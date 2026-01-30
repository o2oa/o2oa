package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.file;

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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("onlyofficefile")
@JaxrsDescribe("在线获取officefile")
public class OnlyofficeFileAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(OnlyofficeFileAction.class);

    @JaxrsMethodDescribe(value = "获取文件正文内容", action =ActionGetFile.class)
 	@GET
 	@Path("file/{flag}/{version}")
 	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
 	public void getFile(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						 @JaxrsParameterDescribe("文档id") @PathParam("flag") String flag,
						 @JaxrsParameterDescribe("文档版本(0待办最新版本)") @PathParam("version") Integer version,
						 @JaxrsParameterDescribe("文档名称(可选)") @QueryParam("fileName") String fileName) {
 		ActionResult<ActionGetFile.Wo> result = new ActionResult<>();
 		EffectivePerson effectivePerson = this.effectivePerson(request);
 		try {
 			result = new ActionGetFile().execute(request,effectivePerson, flag, version, fileName);
 		} catch (Exception e) {
 			logger.error(e, effectivePerson, request, null);
 			result.error(e);
 		}
 		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
 	}

    @JaxrsMethodDescribe(value = "获取文件正文内容的changes.zip", action =ActionGetFileDiff.class)
 	@GET
 	@Path("file/diff/{flag}/{version}")
 	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
 	public void getFileDiff(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
 			@JaxrsParameterDescribe("文档id") @PathParam("flag") String flag,@JaxrsParameterDescribe("文档版本") @PathParam("version") Integer version) {
 		ActionResult<ActionGetFileDiff.Wo> result = new ActionResult<>();
 		EffectivePerson effectivePerson = this.effectivePerson(request);
 		try {
 			result = new ActionGetFileDiff().execute(request,effectivePerson, flag,version);
 		} catch (Exception e) {
 			logger.error(e, effectivePerson, request, null);
 			result.error(e);
 		}
 		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
 	}

	@JaxrsMethodDescribe(value = "获取文件正文内容的changes.zip", action =ActionGetFileDiff.class)
	@GET
	@Path("file/diff/{flag}/{version}/changes.zip")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void getFileDiffWithName(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							@JaxrsParameterDescribe("文档id") @PathParam("flag") String flag,@JaxrsParameterDescribe("文档版本") @PathParam("version") Integer version) {
		ActionResult<ActionGetFileDiff.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetFileDiff().execute(request,effectivePerson, flag,version);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

    @JaxrsMethodDescribe(value = "获取文件正文内容", action =ActionGetFileToken.class)
  	@GET
  	@Path("file/{flag}/{version}/{token}")
  	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
  	public void getFileToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
  			@JaxrsParameterDescribe("文档id") @PathParam("flag") String flag,@JaxrsParameterDescribe("文档版本") @PathParam("version") Integer version,
  			@JaxrsParameterDescribe("文档访问token") @PathParam("token") String token) {
  		ActionResult<ActionGetFileToken.Wo> result = new ActionResult<>();
  		EffectivePerson effectivePerson = this.effectivePerson(request);
  		try {
  			result = new ActionGetFileToken().execute(request,effectivePerson, flag,version,token);
  		} catch (Exception e) {
  			logger.error(e, effectivePerson, request, null);
  			result.error(e);
  		}
  		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
  	}

  	@JaxrsMethodDescribe(value = "获取文件正文内容的diff.zip", action =ActionGetFileDiffToken.class)
  	@GET
  	@Path("file/diff/{flag}/{version}/{token}")
  	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
  	public void getFileDiffToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
  			@JaxrsParameterDescribe("文档id") @PathParam("flag") String flag,
  			@JaxrsParameterDescribe("文档版本") @PathParam("version") Integer version,
  			@JaxrsParameterDescribe("文档访问token") @PathParam("token") String token) {
  		ActionResult<ActionGetFileDiffToken.Wo> result = new ActionResult<>();
  		EffectivePerson effectivePerson = this.effectivePerson(request);
  		try {
  			result = new ActionGetFileDiffToken().execute(request,effectivePerson, flag,version,token);
  		} catch (Exception e) {
  			logger.error(e, effectivePerson, request, null);
  			result.error(e);
  		}
  		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
  	}

}
