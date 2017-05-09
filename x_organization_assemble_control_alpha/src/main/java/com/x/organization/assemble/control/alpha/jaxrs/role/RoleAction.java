package com.x.organization.assemble.control.alpha.jaxrs.role;

import java.util.ArrayList;
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

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapin.WrapInRole;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutRole;

@Path("role")
public class RoleAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "根据ID获取Role对象.", response = WrapOutRole.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutRole> result = new ActionResult<>();
		WrapOutRole wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wrap = new ActionGet().execute(business, id);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建Role对象.", request = WrapInRole.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request, WrapInRole wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			wrap = new ActionCreate().execute(business, effectivePerson, wrapIn);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新Role对象.", request = WrapInRole.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request, @PathParam("id") String id, WrapInRole wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			wrap = new ActionUpdate().execute(business, effectivePerson, id, wrapIn);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除Role对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			wrap = new ActionDelete().execute(business, effectivePerson, id);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示Role对象,下一页.", response = WrapOutRole.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		try {
			result = this.standardListNext(ActionBase.outCopier, id, count, "sequence", null, null, null, null, null,
					null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示Role对象,上一页.", response = WrapOutRole.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		try {
			result = this.standardListPrev(ActionBase.outCopier, id, count, "sequence", null, null, null, null, null,
					null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Person ID,获取其拥有的角色对象.", response = WrapOutRole.class)
	@GET
	@Path("list/person/{personId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPerson(@Context HttpServletRequest request, @PathParam("personId") String personId) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<WrapOutRole>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListWithPerson().execute(business, personId);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Group ID,获取其拥有的角色对象.", response = WrapOutRole.class)
	@GET
	@Path("list/group/{groupId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithGroup(@Context HttpServletRequest request, @PathParam("groupId") String groupId) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListWithGroup().execute(business, groupId);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取拼音首字母开始的Person.", response = WrapOutRole.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListPinyinInitial().execute(business, key);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据名称进行模糊查询.", response = WrapOutRole.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListLike().execute(business, key);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据拼音或者首字母进行模糊查询.", response = WrapOutRole.class)
	@GET
	@Path("/list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListLikePinyin().execute(business, key);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}