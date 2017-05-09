package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;


@Path("statistic")
public class AttendanceStatisticAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticAction.class );
	private AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
	
	
	@HttpMethodDescribe(value = "驱动系统主动进行一次数据统计", response = WrapOutId.class)
	@GET
	@Path("do")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doStatistic(@Context HttpServletRequest request ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		attendanceStatisticServiceAdv.doStatistic();
		logger.info( "system do attendance statistic completed for user！" );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}