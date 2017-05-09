package com.x.organization.assemble.control.alpha.jaxrs.inputperson;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.FileWo;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

@Path("inputperson")
public class InputPersonAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(InputPersonAction.class);

	@HttpMethodDescribe(value = "导入人员.", response = FileWo.class)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void input(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<FileWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionInput().execute(effectivePerson, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "获取导入人员模版.", response = FileWo.class)
	@GET
	@Path("template")
	@Consumes(MediaType.APPLICATION_JSON)
	public void template(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<FileWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionTemplate().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}