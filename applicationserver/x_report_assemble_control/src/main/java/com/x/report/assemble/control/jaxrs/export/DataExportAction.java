package com.x.report.assemble.control.jaxrs.export;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

@Path("export")
@JaxrsDescribe("汇报信息统计导出服务")
public class DataExportAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(DataExportAction.class);

	@JaxrsMethodDescribe(value = "按公司工作重点导出汇报状态统计表", action = ActionExportForStrategyDeploy.class)
	@GET
	@Path( "strategydeploy/{year}/stream" )
	@Consumes(MediaType.APPLICATION_JSON)
	public Response export_ST_StrategyDeploy(@Context HttpServletRequest request, @JaxrsParameterDescribe("导出数据年份") @PathParam( "year" ) String year) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionExportForStrategyDeploy.Wo> result = new ActionResult<>();
		try {
			result = new ActionExportForStrategyDeploy().execute( request, effectivePerson, year );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn( "系统按公司工作重点导出汇报状态统计表过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "按组织导出工作完成情况统计表", action = ActionExportForUnitStrategy.class)
	@GET
	@Path( "unitstrategy/{year}/stream" )
	@Consumes(MediaType.APPLICATION_JSON)
	public Response export_ST_UnitStrategy(@Context HttpServletRequest request, @JaxrsParameterDescribe("导出数据年份") @PathParam( "year" ) String year) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionExportForUnitStrategy.Wo> result = new ActionResult<>();
		try {
			result = new ActionExportForUnitStrategy().execute( request, effectivePerson, year );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn( "系统按组织导出工作完成情况统计表过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "按年份月份以及组织导出工作完成情况统计表", action = ActionExportForUnitReport.class)
	@PUT
	@Path( "unitreport/stream" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response export_ST_UnitReport(@Context HttpServletRequest request,  
			@JaxrsParameterDescribe("导出查询条件") JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionExportForUnitReport.Wo> result = new ActionResult<>();
		try {
			result = new ActionExportForUnitReport().execute( request, effectivePerson, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn( "按年份月份以及组织导出工作完成情况统计表过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@JaxrsMethodDescribe(value = "根据ID从服务器获取指定的导出文件内容（下载）", action = ActionDownLoadExportFile.class)
	@GET
	@Path( "file/{id}/stream" )
	@Consumes(MediaType.APPLICATION_JSON)
	public Response exportFileDownload(@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("文件ID") @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionDownLoadExportFile.Wo> result = new ActionResult<>();
		try {
			result = new ActionDownLoadExportFile().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn( "根据ID从服务器获取指定的导出文件内容（下载）过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}