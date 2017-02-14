package com.x.meeting.assemble.control.jaxrs.openmeeting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.meeting.assemble.control.wrapout.WrapOutOpenMeeting;

@Path("openmeeting")
public class OpenMeetingAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "获取所有Building 同时获取Building下的Room 和 Room下的将来Meeting.", response = WrapOutOpenMeeting.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutOpenMeeting>> result = new ActionResult<>();
		List<WrapOutOpenMeeting> wraps = new ArrayList<>();
		try {
			wraps = new ActionList().execute();
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}