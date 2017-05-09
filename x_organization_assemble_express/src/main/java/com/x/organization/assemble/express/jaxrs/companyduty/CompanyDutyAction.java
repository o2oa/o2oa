package com.x.organization.assemble.express.jaxrs.companyduty;

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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;

@Path("companyduty")
public class CompanyDutyAction extends AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(CompanyDutyAction.class);

	@HttpMethodDescribe(value = "按名称和公司名称查找公司职务.", response = WrapOutCompanyDuty.class)
	@GET
	@Path("{name}/company/{companyName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithNameWithCompany(@Context HttpServletRequest request, @PathParam("name") String name,
			@PathParam("companyName") String companyName) {
		ActionResult<WrapOutCompanyDuty> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithNameWithCompany().execute(name, companyName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查找公司所有职务.", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/company/{companyName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithCompany(@Context HttpServletRequest request, @PathParam("companyName") String companyName) {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCompany().execute(companyName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列出指定名称的属性。", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithName(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithName().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Identity列示其所有的CompanyDuty", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/identity/{identityName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithIdentity(@Context HttpServletRequest request,
			@PathParam("identityName") String identityName) {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithIdentity().execute(identityName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Person列示其所有的CompanyDuty", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPerson(@Context HttpServletRequest request, @PathParam("personName") String personName) {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPerson().execute(personName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据给定的Person和Company，列示其所有的CompanyDuty", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/person/{personName}/company/{companyName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPersonWithCompany(@Context HttpServletRequest request,
			@PathParam("personName") String personName, @PathParam("companyName") String companyName) {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonWithCompany().execute(personName, companyName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}