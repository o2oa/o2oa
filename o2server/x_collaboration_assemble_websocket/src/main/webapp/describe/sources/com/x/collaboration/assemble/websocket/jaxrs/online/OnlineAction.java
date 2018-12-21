package com.x.collaboration.assemble.websocket.jaxrs.online;

import java.util.ArrayList;
import java.util.List;

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

import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapInStringList;
import com.x.base.core.project.http.WrapOutOnline;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

@Path("online")
public class OnlineAction extends StandardJaxrsAction {

	@JaxrsMethodDescribe(value = "查看指定人员是否在线。", action = ActionGetOnline.class)
	@GET
	@Path("person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getOnline(@Context HttpServletRequest request, @PathParam("person") String person) {
		ActionResult<WrapOutOnline> result = new ActionResult<>();
		WrapOutOnline wrap = null;
		try {
			wrap = new ActionGetOnline().execute(person);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "查看指定人员是否在本服务器。", action = ActionGetOnlineLocal.class)
	@GET
	@Path("person/{person}/local")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getOnlineLocal(@Context HttpServletRequest request, @PathParam("person") String person) {
		ActionResult<WrapOutOnline> result = new ActionResult<>();
		WrapOutOnline wrap = null;
		try {
			wrap = new ActionGetOnlineLocal().execute(person);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "查看指定人员列表是否在线，同时也呼叫其他服务器进行查找。", action = ActionListOnline.class)
	@PUT
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listOnline(@Context HttpServletRequest request, WrapInStringList wrapIn) {
		ActionResult<List<WrapOutOnline>> result = new ActionResult<>();
		List<WrapOutOnline> wraps = null;
		try {
			wraps = new ActionListOnline().execute(wrapIn);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "查看指定人员列表是否在线，仅查找本服务器。", action = ActionListOnlineLocal.class)
	@PUT
	@Path("list/local")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listOnlineLocal(@Context HttpServletRequest request, WrapInStringList wrapIn) {
		ActionResult<List<WrapOutOnline>> result = new ActionResult<>();
		List<WrapOutOnline> wraps = null;
		try {
			wraps = new ActionListOnlineLocal().execute(wrapIn);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "查找所有在线的人员，同时呼叫其他服务器进行查找。", action = ActionListOnlineAll.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listOnlineAll(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutOnline>> result = new ActionResult<>();
		List<WrapOutOnline> wraps = new ArrayList<>();
		try {
			wraps = new ActionListOnlineAll().execute();
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "查找所有在本地服务器的人员", action = ActionListOnlineAllLocal.class)
	@GET
	@Path("list/all/local")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listOnlineAllLocal(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutOnline>> result = new ActionResult<>();
		List<WrapOutOnline> wraps = new ArrayList<>();
		try {
			wraps = new ActionListOnlineAllLocal().execute();
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}