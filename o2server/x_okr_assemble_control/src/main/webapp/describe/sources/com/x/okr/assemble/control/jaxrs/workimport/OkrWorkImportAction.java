package com.x.okr.assemble.control.jaxrs.workimport;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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

@Path("import")
@JaxrsDescribe("工作信息导入服务")
public class OkrWorkImportAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkImportAction.class);

	@JaxrsMethodDescribe(value = "进行工作信息导入", action = ActionWorkImport.class)
	@POST
	@Path("center/{centerId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void importWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("中心工作ID") @PathParam("centerId") String centerId,
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<Object> result = new ActionResult<>();
		try {
			result = new ActionWorkImport().execute(request, effectivePerson, centerId, site, bytes, disposition);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统根据中心工作ID获取中心工作所有附件信息过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
