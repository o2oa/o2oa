package com.x.attendance.assemble.control.jaxrs.fileimport;

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

import com.x.attendance.assemble.control.jaxrs.ExceptionAttendanceProcess;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
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

@Path("fileimport")
@JaxrsDescribe("考勤数据导入服务（已弃用）")
public class AttendanceDetailFileImportAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AttendanceDetailFileImportAction.class);

	@JaxrsMethodDescribe(value = "获取指定导入文件的操作状态", action = ActionGetFileOptStatusWithFile.class)
	@GET
	@Path("getStatus/{file_id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getStatus(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("导入文件信息ID") @PathParam("file_id") String file_id) {
		ActionResult<StatusImportFileDetail> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionGetFileOptStatusWithFile().execute(request, effectivePerson, file_id);
				if (result.getData() != null) {
					result.getData().setDetailList(null);
				}
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceProcess(e, "获取所有节假日配置列表信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取指定导入文件的操作状态，包括检查数据列表", action = ActionGetFileOptStatusWithFile.class)
	@GET
	@Path("getStatus/{file_id}/detail")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getStatusDetail(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("导入文件信息ID") @PathParam("file_id") String file_id) {
		ActionResult<StatusImportFileDetail> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionGetFileOptStatusWithFile().execute(request, effectivePerson, file_id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceProcess(e, "获取所有节假日配置列表信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取系统的导入文件操作状态", action = ActionGetSystemImportOptStatus.class)
	@GET
	@Path("getStatus/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getStatusAll(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGetSystemImportOptStatus.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionGetSystemImportOptStatus().execute(request, effectivePerson);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceProcess(e, "获取所有节假日配置列表信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}