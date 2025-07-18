package com.x.program.center.jaxrs.deploy;

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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("deploy")
@JaxrsDescribe("部署")
public class DeployAction extends StandardJaxrsAction {

	private static final Logger logger = LoggerFactory.getLogger(DeployAction.class);

	@JaxrsMethodDescribe(value = "部署前端静态资源文件", action = ActionDeployWebRes.class)
	@POST
	@Path("web/resource/as/new/{asNew}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void deployWebResource(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("覆盖类型：true删除原文件然后上传，false覆盖原文件") @PathParam("asNew") Boolean asNew,
			@JaxrsParameterDescribe("附件存放目录(zip文件可以为空，其他不能为空)") @FormDataParam("filePath") String filePath,
			@JaxrsParameterDescribe("标题") @FormDataParam("title") String title,
			@JaxrsParameterDescribe("版本") @FormDataParam("version") String version,
			@JaxrsParameterDescribe("更新升级描述") @FormDataParam("remark") String remark,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("附件标识") @FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("上传zip文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionDeployWebRes.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			BaseAction.Wi wi = new BaseAction.Wi();
			wi.setAsNew(asNew);
			wi.setName(fileName);
			wi.setFilePath(filePath);
			wi.setTitle(title);
			wi.setVersion(version);
			wi.setRemark(remark);
			result = new ActionDeployWebRes().execute(effectivePerson,wi, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "部署服务端war包和jar包，包括平台的应用和自定义应用", action = ActionDeployServerRes.class)
	@POST
	@Path("server/resource")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void deployServerResource(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标题") @FormDataParam("title") String title,
			@JaxrsParameterDescribe("版本") @FormDataParam("version") String version,
			@JaxrsParameterDescribe("更新升级描述") @FormDataParam("remark") String remark,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) byte[] bytes,
			@JaxrsParameterDescribe("上传文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionDeployServerRes.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			BaseAction.Wi wi = new BaseAction.Wi();
			wi.setName(fileName);
			wi.setTitle(title);
			wi.setVersion(version);
			wi.setRemark(remark);
			result = new ActionDeployServerRes().execute(effectivePerson, wi, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "升级o2server", action = ActionDeployO2Server.class)
	@POST
	@Path("server/o2")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void deployO2Server(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标题") @FormDataParam("title") String title,
			@JaxrsParameterDescribe("版本") @FormDataParam("version") String version,
			@JaxrsParameterDescribe("更新升级描述") @FormDataParam("remark") String remark,
			@JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("上传o2server升级包") @FormDataParam(FILE_FIELD) final FormDataBodyPart part) {
		ActionResult<ActionDeployO2Server.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			BaseAction.Wi wi = new BaseAction.Wi();
			wi.setName(fileName);
			wi.setTitle(title);
			wi.setVersion(version);
			wi.setRemark(remark);
			result = new ActionDeployO2Server().execute(effectivePerson, wi, part);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页列示部署日志信息.", action = ActionListPaging.class)
	@POST
	@Path("list/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size) {
		ActionResult<List<ActionListPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPaging().execute(effectivePerson, page, size);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取指定部署日志.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
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

}
