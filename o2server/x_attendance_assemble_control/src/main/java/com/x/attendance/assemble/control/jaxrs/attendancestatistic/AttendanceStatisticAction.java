package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("statistic")
@JaxrsDescribe("数据统计执行服务（已弃用）")
public class AttendanceStatisticAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AttendanceStatisticAction.class);

	@JaxrsMethodDescribe(value = "驱动系统主动进行一次数据统计", action = ActionStatisticCaculate.class)
	@GET
	@Path("do")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void doStatistic(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionStatisticCaculate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionStatisticCaculate().execute(request, effectivePerson);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceSettingProcess(e, "驱动系统主动进行数据统计时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}