package com.x.organization.assemble.express.jaxrs.departmentduty;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentDuty;

@Path("departmentduty")
public class DepartmentDutyAction extends AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(DepartmentDutyAction.class);

	@HttpMethodDescribe(value = "按名称和部门名称查找公司职务.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("{name}/department/{departmentName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithNameWithDepartment(@Context HttpServletRequest request, @PathParam("name") String name,
			@PathParam("departmentName") String departmentName) {
		ActionResult<WrapOutDepartmentDuty> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithNameWithDepartment().execute(name, departmentName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列出指定名称的属性.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithName(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithName().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Identity，列示其所有的DepartmentDuty", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/identity/{identityName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithIdentity(@Context HttpServletRequest request,
			@PathParam("identityName") String identityName) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithIdentity().execute(identityName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的person列示其所有的DepartmentDuty", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPerson(@Context HttpServletRequest request, @PathParam("personName") String personName) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPerson().execute(personName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查找部门所有职务.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/department/{departmentName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithDepartment(@Context HttpServletRequest request,
			@PathParam("departmentName") String departmentName) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithDepartment().execute(departmentName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Person和Department，列示其所有的DepartmentDuty", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/person/{personName}/department/{departmentName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPersonWithDepartment(@Context HttpServletRequest request,
			@PathParam("personName") String personName, @PathParam("departmentName") String departmentName) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonWithDepartment().execute(personName, departmentName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}