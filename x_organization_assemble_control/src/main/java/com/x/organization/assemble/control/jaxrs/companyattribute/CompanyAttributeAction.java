package com.x.organization.assemble.control.jaxrs.companyattribute;

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
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInCompanyAttribute;
import com.x.organization.assemble.control.wrapout.WrapOutCompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute;

@Path("companyattribute")
public class CompanyAttributeAction extends StandardJaxrsAction {

	BeanCopyTools<CompanyAttribute, WrapOutCompanyAttribute> outCopier = BeanCopyToolsBuilder
			.create(CompanyAttribute.class, WrapOutCompanyAttribute.class, null, WrapOutCompanyAttribute.Excludes);

	BeanCopyTools<WrapInCompanyAttribute, CompanyAttribute> inCopier = BeanCopyToolsBuilder
			.create(WrapInCompanyAttribute.class, CompanyAttribute.class, null, WrapInCompanyAttribute.Excludes);

	@HttpMethodDescribe(value = "获取CompanyAttribute对象.", response = WrapOutCompanyAttribute.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutCompanyAttribute> result = new ActionResult<>();
		WrapOutCompanyAttribute wrap = null;
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

	@HttpMethodDescribe(value = "创建CompanyAttribute对象.", request = WrapInCompanyAttribute.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request, WrapInCompanyAttribute wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			wrap = new ActionCreate().execute(business, effectivePerson, wrapIn);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新CompanyAttribute对象。", request = WrapInCompanyAttribute.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request, @PathParam("id") String id,
			WrapInCompanyAttribute wrapIn) {
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

	@HttpMethodDescribe(value = "根据ID删除CompanyAttribute对象.", response = WrapOutId.class)
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

	@HttpMethodDescribe(value = "列示CompanyAttribute对象,下一页.", response = WrapOutCompanyAttribute.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutCompanyAttribute>> result = new ActionResult<>();
		try {
			result = this.standardListNext(outCopier, id, count, "sequence", null, null, null, null, null, null, null,
					true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示CompanyAttribute对象,上一页.", response = WrapOutCompanyAttribute.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListPrev(@PathParam("id") String id, @PathParam("count") Integer count) {
		ActionResult<List<WrapOutCompanyAttribute>> result = new ActionResult<>();
		try {
			result = this.standardListPrev(outCopier, id, count, "sequence", null, null, null, null, null, null, null,
					true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Company ID,列示其所有的CompanyAttribute对象.", response = WrapOutCompanyAttribute.class)
	@GET
	@Path("list/company/{companyId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithCompany(@Context HttpServletRequest request, @PathParam("companyId") String companyId) {
		ActionResult<List<WrapOutCompanyAttribute>> result = new ActionResult<>();
		List<WrapOutCompanyAttribute> wraps = new ArrayList<WrapOutCompanyAttribute>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListWithCompany().execute(business, companyId);
			result.setData(wraps);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}