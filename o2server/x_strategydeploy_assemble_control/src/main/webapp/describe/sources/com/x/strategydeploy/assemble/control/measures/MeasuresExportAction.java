package com.x.strategydeploy.assemble.control.measures;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("measuresexport")
@JaxrsDescribe("战略管理导出工作纲要和举措")
public class MeasuresExportAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(MeasuresExportAction.class);
	private static String DEPT_SEPARATOR = "、";
	
	@JaxrsMethodDescribe(value = "根据年份获取导出工作纲要和举措.", action = StandardJaxrsAction.class)
	@GET
	@Path("export/year/{year}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("导入文件返回的结果标记") @PathParam("year") String year) {
		ActionResult<ActionExport.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExport().execute(effectivePerson,year);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	

}
