package com.x.okr.assemble.control.jaxrs.export;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

@Path("export")
@JaxrsDescribe("文件导出服务")
public class OkrExportAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrExportAction.class);

	@JaxrsMethodDescribe(value = "导出汇报内容统计表", action = ActionStatisticReportContentExport.class)
	@GET
	@Path("statisticreportcontent/{flag}/stream")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void statisticReportContent(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("文件标识") @PathParam("flag") String flag) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionStatisticReportContentExport.Wo> result = new ActionResult<>();
		try {
			result = new ActionStatisticReportContentExport().execute(request, effectivePerson, flag);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统根据中心工作ID获取中心工作所有附件信息过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "导出汇报状态统计表", action = ActionStatisticReportContentExport.class)
	@GET
	@Path("statisticreportstatus/{flag}/stream")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void statisticReportStatus(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文件标识") @PathParam("flag") String flag) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionStatisticReportContentExport.Wo> result = new ActionResult<>();
		try {
			result = new ActionStatisticReportContentExport().execute(request, effectivePerson, flag);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统根据中心工作ID获取中心工作所有附件信息过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
