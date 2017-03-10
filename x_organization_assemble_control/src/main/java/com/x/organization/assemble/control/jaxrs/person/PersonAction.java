package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapInString;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.assemble.control.wrapin.WrapInPerson;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;

@Path("person")
public class PersonAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "根据ID获取Person对象.", response = WrapOutPerson.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutPerson> result = new ActionResult<>();
		try {
			result = new ActionGet().execute(id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建Person对象.", request = WrapInPerson.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request, WrapInPerson wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionCreate().execute(effectivePerson, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新Person对象.", response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInPerson wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionUpdate().execute(effectivePerson, id, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除Person对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDelete().execute(effectivePerson, id);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示Person对象,下一页.", response = WrapOutPerson.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		try {
			result = new ActionListNext().execute(id, count);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示Person对象,上一页.", response = WrapOutPerson.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		try {
			result = new ActionListPrev().execute(id, count);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Group ID,列示直接个人成员.", response = WrapOutPerson.class)
	@GET
	@Path("list/group/{groupId}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubDirectWithGroup(@Context HttpServletRequest request, @PathParam("groupId") String groupId) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		try {
			result = new ActionListSubDirectWithGroup().execute(groupId);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Group ID,列示嵌套的个人成员.", response = WrapOutPerson.class)
	@GET
	@Path("list/group/{groupId}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubNestedWithGroup(@Context HttpServletRequest request, @PathParam("groupId") String groupId) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		try {
			result = new ActionListSubNestedWithGroup().execute(groupId);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取拼音首字母开始的Person.", response = WrapOutPerson.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		try {
			result = new ActionListPinyinInitial().execute(key);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据名称进行模糊查询.", response = WrapOutPerson.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		try {
			result = new ActionListLike().execute(key);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据拼音或者首字母进行模糊查询.", response = WrapOutPerson.class)
	@GET
	@Path("/list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		try {
			result = new ActionListLikePinyin().execute(key);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定Person的Password.", request = WrapInString.class, response = WrapOutId.class)
	@PUT
	@Path("{name}/set/password")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setPassword(@Context HttpServletRequest request, @PathParam("name") String name,
			WrapInString wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionSetPassword().execute(effectivePerson, name, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}