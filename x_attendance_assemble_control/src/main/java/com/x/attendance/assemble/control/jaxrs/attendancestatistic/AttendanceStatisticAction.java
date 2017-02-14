package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;


@Path("statistic")
public class AttendanceStatisticAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticAction.class );
	private AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
	
	
	@HttpMethodDescribe(value = "驱动系统进行数据统计", response = WrapOutMessage.class)
	@GET
	@Path("do")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doStatistic(@Context HttpServletRequest request ) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		WrapOutMessage wrapOutMessage = new WrapOutMessage();
		attendanceStatisticServiceAdv.doStatistic();
		logger.debug( "system do attendance statistic completed for user！" );
		result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}