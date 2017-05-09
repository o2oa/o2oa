package com.x.meeting.assemble.control.jaxrs.openmeeting;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.bean.NameIdPair;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.meeting.assemble.control.wrapout.WrapOutOpenMeeting;

@Path("openmeeting")
public class OpenMeetingAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "获取openmeeting配置.", response = WrapOutOpenMeeting.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request) {
		ActionResult<WrapOutOpenMeeting> result = new ActionResult<>();
		try {
			result = new ActionGet().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取openmeeting的room.", response = WrapOutOpenMeeting.class)
	@GET
	@Path("list/room")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listRoom(@Context HttpServletRequest request) {
		ActionResult<List<NameIdPair>> result = new ActionResult<>();
		try {
			result = new ActionListRoom().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}