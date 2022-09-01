package com.x.program.center.jaxrs.command;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

@JaxrsDescribe("命令")
@Path("command")
public class CommandAction<Wo> extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(CommandAction.class);

	@JaxrsMethodDescribe(value = "执行服务器命令", action = ActionCommand.class)
	@POST
	@Path("execute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void executeCommand(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionCommand.Wo> result = new ActionResult<>();
		try {

			result = new ActionCommand().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取所有节点信息.", action = ActionListNode.class)
	@GET
	@Path("list/node")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getNodeInfoList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionListNode.Wo> result = new ActionResult<>();
		try {
			result = new ActionListNode().execute(effectivePerson);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传customJar,customWar,storeJar,storeWar包并自动部署", action = ActionUploadFile.class)
	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void upload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) byte[] bytes,
			@JaxrsParameterDescribe("上传文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUploadFile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadFile().execute(effectivePerson, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "uninstall customWar 包并删除", action = ActionUninstall.class)
	@POST
	@Path("uninstall")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void uninstall(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("命令名称(customWar)") @FormDataParam("ctl") String ctl,
			@JaxrsParameterDescribe("服务器地址(*代表多台应用服务器)") @FormDataParam("nodeName") String nodeName,
			@JaxrsParameterDescribe("服务端口") @FormDataParam("nodePort") String nodePort,
			@JaxrsParameterDescribe("War名称") @FormDataParam(FILENAME_FIELD) String fileName) {
		ActionResult<ActionUninstall.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUninstall().execute(request, effectivePerson, ctl, nodeName, nodePort, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
