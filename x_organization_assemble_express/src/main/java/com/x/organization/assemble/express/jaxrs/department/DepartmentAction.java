package com.x.organization.assemble.express.jaxrs.department;

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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartment;

@Path("department")
public class DepartmentAction extends AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(DepartmentAction.class);

	@HttpMethodDescribe(value = "按名称查找部门.如果返回值不存在则此部门不存在。", response = WrapOutDepartment.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示所有部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListAll().execute();
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据名称查询人员所在的部门,只查询直接所在部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/person/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPerson(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPerson().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据名称查询人员所在的部门,并递归查找部门的上级部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/person/{name}/sup/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPersonSupNested(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPersonSupNested().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据身份名称获取所在部门.", response = WrapOutDepartment.class)
	@GET
	@Path("identity/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithIdentity(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithIdentity().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查询指定部门的上级部门.", response = WrapOutDepartment.class)
	@GET
	@Path("{name}/sup/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSupDirect(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetSupDirect().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "递归查询指定公司的上级部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/{name}/sup/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSupNested(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListSupNested().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查询指定部门的直接下级部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/{name}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubDirect(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListSubDirect().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查询指定部门的嵌套下级部门", response = WrapOutDepartment.class)
	@GET
	@Path("list/{name}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubNested(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListSubNested().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查找公司的顶层部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/company/{companyName}/top")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listTopWithCompany(@Context HttpServletRequest request,
			@PathParam("companyName") String companyName) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTopWithCompany().execute(companyName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查找公司的部门,包括下属部门和部门的部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/company/{companyName}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithCompanySubNested(@Context HttpServletRequest request,
			@PathParam("companyName") String companyName) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCompanySubNested().execute(companyName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列出指定部门属性值的所有部门", response = WrapOutDepartment.class)
	@GET
	@Path("list/departmentAttribute/{name}/{attribute}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithDepartmentAttribute(@Context HttpServletRequest request, @PathParam("name") String name,
			@PathParam("attribute") String attribute) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithDepartmentAttribute().execute(name, attribute);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取拼音首字母开始的Department.", response = WrapOutDepartment.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPinyinInitial().execute(key);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据拼音或者首字母搜索.", response = WrapOutDepartment.class)
	@GET
	@Path("list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListLikePinyin().execute(key);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "进行模糊查询.", response = WrapOutDepartment.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListLike().execute(key);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}