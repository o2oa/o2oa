package com.x.organization.assemble.control.jaxrs.departmentduty;

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

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutListString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInDepartmentDuty;
import com.x.organization.assemble.control.wrapout.WrapOutCompanyDuty;
import com.x.organization.assemble.control.wrapout.WrapOutDepartmentDuty;

@Path("departmentduty")
public class DepartmentDutyAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "根据ID获取DepartmentDuty.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutDepartmentDuty> result = new ActionResult<>();
		WrapOutDepartmentDuty wrap = null;
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

	@HttpMethodDescribe(value = "创建DepartmentDuty.", request = WrapInDepartmentDuty.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInDepartmentDuty wrapIn) {
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

	@HttpMethodDescribe(value = "更新DepartmentDuty.", request = WrapInDepartmentDuty.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request, @PathParam("id") String id,
			WrapInDepartmentDuty wrapIn) {
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

	@HttpMethodDescribe(value = "根据ID删除DepartmentDuty.", response = WrapOutId.class)
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

	@HttpMethodDescribe(value = "列示DepartmentDuty对象,下一页.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		try {
			result = this.standardListNext(ActionBase.outCopier, id, count, "sequence", null, null, null, null, null,
					null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示DepartmentDuty对象,上一页.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		try {
			result = this.standardListPrev(ActionBase.outCopier, id, count, "sequence", null, null, null, null, null,
					null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Identity ID获取其在部门中所拥有的DepartmentDuty.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/identity/{identityId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithIdentity(@Context HttpServletRequest request, @PathParam("identityId") String identityId) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		List<WrapOutDepartmentDuty> wraps = new ArrayList<WrapOutDepartmentDuty>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListWithIdentity().execute(business, identityId);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Department ID获取其所有的DepartmentDuty.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/department/{departmentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithDepartment(@Context HttpServletRequest request,
			@PathParam("departmentId") String departmentId) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		List<WrapOutDepartmentDuty> wraps = new ArrayList<WrapOutDepartmentDuty>();
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

	@HttpMethodDescribe(value = "获取去重的部门职务名称.", response = WrapOutListString.class)
	@GET
	@Path("distinct/name")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response distinctName(@Context HttpServletRequest request) {
		ActionResult<WrapOutListString> result = new ActionResult<>();
		try {
			result = new ActionDistinctName().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的部门职务名称列示公司部门.", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/name/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithName(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		try {
			result = new ActionListWithName().execute(name);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "模糊匹配部门职务的名称,匹配名称,拼音和首字母.", response = WrapOutListString.class)
	@GET
	@Path("distinct/name/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response distinctNameLike(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<WrapOutListString> result = new ActionResult<>();
		try {
			result = new ActionDistinctNameLike().execute(key);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}