package com.x.organization.assemble.control.jaxrs.complex;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutCompany;
import com.x.organization.assemble.control.wrapout.WrapOutDepartment;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.Department;

@Path("complex")
public class ComplexAction extends StandardJaxrsAction {

	BeanCopyTools<Company, WrapOutCompany> companyOutCopier = BeanCopyToolsBuilder.create(Company.class,
			WrapOutCompany.class, null, WrapOutCompany.Excludes);

	BeanCopyTools<Department, WrapOutDepartment> departmentOutCopier = BeanCopyToolsBuilder.create(Department.class,
			WrapOutDepartment.class, null, WrapOutDepartment.Excludes);

	@HttpMethodDescribe(value = "根据Company ID获取该公司下属的直接分公司和直接部门.", response = WrapOutCompany.class)
	@GET
	@Path("company/{companyId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getCompanySubDirectCompanySubDirectDepartmentWithCompany(@Context HttpServletRequest request,
			@PathParam("companyId") String companyId) {
		ActionResult<WrapOutCompany> result = new ActionResult<>();
		WrapOutCompany wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wrap = new ActionGetCompanySubDirectCompanySubDirectDepartmentWithCompany().execute(business, companyId);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取部门的下属部门和下属Identity.并计算下级的下级数量，用于级联显示。", response = WrapOutDepartment.class)
	@GET
	@Path("department/{departmentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDepartmentSubDirectDepartmentSubDirectIdentity(@PathParam("departmentId") String departmentId) {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		WrapOutDepartment wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wrap = new ActionGetDepartmentSubDirectDepartmentSubDirectIdentity().execute(business, departmentId);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取个人，包括是否在线以及CompanyDuty,DepartmentDuty,Identity,可以通过Id或者个人的name来获取", response = WrapOutDepartment.class)
	@GET
	@Path("person/{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPersonCompanyDutyDepartmentDutyIdentity(@PathParam("flag") String flag) {
		ActionResult<WrapOutPerson> result = new ActionResult<>();
		WrapOutPerson wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wrap = new ActionGetPersonIdentityCompanyDutyDepartmentDuty().execute(business, flag);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}