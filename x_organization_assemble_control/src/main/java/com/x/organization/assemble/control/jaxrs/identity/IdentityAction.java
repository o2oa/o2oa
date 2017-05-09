package com.x.organization.assemble.control.jaxrs.identity;

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
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInIdentity;
import com.x.organization.assemble.control.wrapout.WrapOutIdentity;

@Path("identity")
public class IdentityAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "根据ID获取Identity对象.", response = WrapOutIdentity.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutIdentity> result = new ActionResult<>();
		WrapOutIdentity wrap = null;
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

	@HttpMethodDescribe(value = "创建Identity对象.", request = WrapInIdentity.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request, WrapInIdentity wrapIn) {
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

	@HttpMethodDescribe(value = "更新Identity对象.", request = WrapInIdentity.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInIdentity wrapIn) {
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

	@HttpMethodDescribe(value = "删除Identity对象,同时删除CompanyDuty和DepartmentDuty中的IdentityList成员.", response = WrapOutId.class)
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

	@HttpMethodDescribe(value = "列示Identity对象,下一页.", response = WrapOutIdentity.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		try {
			result = this.standardListNext(ActionBase.outCopier, id, count, "sequence", null, null, null, null, null,
					null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示Identity对象,上一页.", response = WrapOutIdentity.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		try {
			result = this.standardListPrev(ActionBase.outCopier, id, count, "sequence", null, null, null, null, null,
					null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Person ID,列示其所有的Identity对象.", response = WrapOutIdentity.class)
	@GET
	@Path("list/person/{personId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPerson(@Context HttpServletRequest request, @PathParam("personId") String personId) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
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

	@HttpMethodDescribe(value = "根据给定的Department ID,列示其所有的部门成员即Identity对象.", response = WrapOutIdentity.class)
	@GET
	@Path("list/department/{departmentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubDirectWithDepartment(@Context HttpServletRequest request,
			@PathParam("departmentId") String departmentId) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListWithDepartment().execute(business, departmentId);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取拼音首字母开始的身份.", response = WrapOutIdentity.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
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

	@HttpMethodDescribe(value = "根据拼音或者首字母搜索.", response = WrapOutIdentity.class)
	@GET
	@Path("list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
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

	@HttpMethodDescribe(value = "进行模糊查询.", response = WrapOutIdentity.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
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

	@HttpMethodDescribe(value = "在指定范围内(公司和部门)进行模糊查询.", response = WrapOutIdentity.class)
	@POST
	@Path("list/company/sub/nest/department/sub/nest/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikeWithCompanySubNestedDepartmentSubNested(@Context HttpServletRequest request,
			@PathParam("key") String key, WrapInCompanySubNestedDepartmentSubNested wrapIn) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListLikeWithCompanySubNestedDepartmentSubNested().execute(business, key, wrapIn);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}