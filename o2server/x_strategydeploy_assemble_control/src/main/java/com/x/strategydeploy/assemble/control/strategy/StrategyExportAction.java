package com.x.strategydeploy.assemble.control.strategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

@Path("exportcontent")
@JaxrsDescribe("战略管理，战略信息配置导出")
public class StrategyExportAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(StrategyExportAction.class);

	@JaxrsMethodDescribe(value = "导出某一年的战略管理信息.", action = ActionExportExcelStreamXLSX.class)
	@GET
	@Path("export/year/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void export(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @PathParam("year") String year) {

		EffectivePerson effectivePerson = this.effectivePerson(request);
		//ActionResult<ActionExportExcelStreamXLSX.WoExcel> result = new ActionResult<>();
		//ActionResult<ActionExportExcelStreamXLSX2.WoExcel> result = new ActionResult<>();
		ActionResult<ActionExportExcelStreamXLSX2.WoCacheFileId> result = new ActionResult<>();
		try {
			//result = new ActionExportExcelStreamXLSX().execute(request, response, effectivePerson, year, true);
			result = new ActionExportExcelStreamXLSX2().execute(request, effectivePerson, year, true);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute StrategyExportAction-->export got an exception. ");
			logger.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	
	@JaxrsMethodDescribe(value = "获取导出结果.", action = ActionGetExportExcelResult.class)
	@GET
	@Path("result/flag/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("返回的结果标记") @PathParam("flag") String flag) {
		ActionResult<ActionGetExportExcelResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetExportExcelResult().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}
