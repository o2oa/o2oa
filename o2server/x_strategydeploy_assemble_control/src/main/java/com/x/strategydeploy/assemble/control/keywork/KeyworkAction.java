package com.x.strategydeploy.assemble.control.keywork;

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
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Unit;
import com.x.strategydeploy.assemble.control.keywork.ActionListAllAndRelatedWithFilter.WoKeyworkWithMeasures;

import org.apache.commons.lang3.StringUtils;

@Path("keywork")
@JaxrsDescribe("重点工作服务")
public class KeyworkAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(KeyworkAction.class);

	@JaxrsMethodDescribe(value = "测试", action = StandardJaxrsAction.class)
	@GET
	@Path("iswork")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void iswork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("Json信息") JsonElement jsonElement) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		logger.debug(effectivePerson.getDistinguishedName());
		wrap.setValue("measures iswork  is work!! Token:" + effectivePerson.getToken() + " Name:" + effectivePerson.getDistinguishedName() + " TokenType:" + effectivePerson.getTokenType());
		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "新建或者更新重点工作", action = ActionSave.class)
	@POST
	@Path("save")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("Json信息") JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if (check) {
			try {
				result = new ActionSave().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取 重点工作 对象", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("重点工作对象ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据ID删除 五项重点工作 对象，返回id", action = ActionGet.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("举措对象ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		BaseAction.Wi wrapIn = null;
		Boolean check = true;
		if (check) {
			try {
				result = new ActionDelete().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件的五项重点工作,ordersymbol=ASC|DESC，第几页，每页几条", action = ActionListWithFilterPageCount.class)
	@PUT
	@Path("filter/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithFilterPageCount(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("第几页（从1开始）") @PathParam("page") Integer page, @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListNextWithFilter.Wo>> result = new ActionResult<>();
		BaseAction.Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, BaseAction.Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new Exception(e);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (null == wrapIn.getKeyworkyear() || wrapIn.getKeyworkyear().isEmpty()) {
				Exception exception = new Exception("过滤条件，年份必须填写.");
				result.error(exception);
				check = false;
			}
		}

		if (check) {
			try {
				result = new ActionListWithFilterPageCount().execute(request, effectivePerson, page, count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件的重点工作,下一页", action = ActionListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id, @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListNextWithFilter.Wo>> result = new ActionResult<>();
		BaseAction.Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, BaseAction.Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new Exception(e);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (null == wrapIn.getKeyworkyear() || wrapIn.getKeyworkyear().isEmpty()) {
				Exception exception = new Exception("过滤条件，年份必须填写.");
				result.error(exception);
				check = false;
			}
		}

		if (check) {
			try {
				result = new ActionListNextWithFilter().execute(request, effectivePerson, id, count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件的重点工作,上一页", action = ActionListPrevWithFilter.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrevWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id, @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListPrevWithFilter.Wo>> result = new ActionResult<>();

		BaseAction.Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, BaseAction.Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new Exception(e);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				result = new ActionListPrevWithFilter().execute(request, effectivePerson, id, count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列出某一个年份的所有五项重点工作", action = StandardJaxrsAction.class)
	@GET
	@Path("filter/list/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByYear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListByYear().execute(request, effectivePerson, year);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列出某一个年份，某一个部门的五项重点工作", action = StandardJaxrsAction.class)
	@GET
	@Path("filter/list/{year}/{dept}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByYearAndDept(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year, @JaxrsParameterDescribe("部门") @PathParam("dept") String dept) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListByYearAndDept().execute(request, effectivePerson, year, dept);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "输入一个日期，如果部门五项重点工作的有效期包含这个日期。 返回部门列表。", action = StandardJaxrsAction.class)
	@GET
	@Path("dept/deptlist/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDepartmentsBydate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("日期") @PathParam("date") String dateString) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		//		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		//		try {
		//			result = new ActionListByYearAndDept().execute(request, effectivePerson, year,dept);
		//		} catch (Exception e) {
		//			result = new ActionResult<>();
		//			logger.error(e, effectivePerson, request, null);
		//		}
		dateString = "2017-08-30";

		ActionResult<List<BaseAction.WoStringList>> result = new ActionResult<>();
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列出所有部门", action = StandardJaxrsAction.class)
	@GET
	@Path("dept/listdepts")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDepts(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<Unit>> result = new ActionResult<>();
		List<Unit> wos = new ArrayList<>();
		//ActionListDepts.Wo wo = new ActionListDepts.Wo();
		//Unit unit = new Unit();
		try {
			wos = new ActionListDepts().execute(request, effectivePerson);
			result.setData(wos);
		} catch (Exception e) {
			//wo = new ActionListYears.Wo();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列出所有年份", action = StandardJaxrsAction.class)
	@GET
	@Path("listyears")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listYears(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionListYears.Wo> result = new ActionResult<>();
		ActionListYears.Wo wo = new ActionListYears.Wo();

		try {
			wo = new ActionListYears().execute(request, effectivePerson);
			result.setData(wo);
		} catch (Exception e) {
			//wo = new ActionListYears.Wo();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据部门列出所有年份", action = StandardJaxrsAction.class)
	@POST
	@Path("listyearsbydept")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listYearsByDept(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionListYearsByDept.Wo> result = new ActionResult<>();
		ActionListYearsByDept.Wo wo = new ActionListYearsByDept.Wo();
		BaseAction.Wi wrapIn = new BaseAction.Wi();
		boolean ispass = true;
		try {
			wrapIn = this.convertToWrapIn(jsonElement, BaseAction.Wi.class);
			if (null == wrapIn.getKeyworkunit() || wrapIn.getKeyworkunit().isEmpty()) {
				Exception e = new Exception("keyworkunit can not be blank!");
				result.error(e);
				ispass = false;
			}
			if (ispass) {
				wo = new ActionListYearsByDept().execute(request, effectivePerson, wrapIn.getKeyworkunit());
				result.setData(wo);
			}

		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据年份、举措id、组织id，列出五项重点工作。", action = ActionListBy_Year_MeasuresId_Unit.class)
	@GET
	@Path("listbyyearmeasuresidunit/year/{year}/measuresid/{measuresid}/unit/{unit}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ActionListBy_Year_MeasuresId_Unit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year, @JaxrsParameterDescribe("举措id") @PathParam("measuresid") String measuresid,
			@JaxrsParameterDescribe("组织") @PathParam("unit") String unit) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		//List<BaseAction.wo> wos = new ArrayList<BaseAction.wo>();
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		BaseAction.Wi wrapIn = new BaseAction.Wi();
		boolean ispass = true;
		try {
			result = new ActionListBy_Year_MeasuresId_Unit().execute(request, effectivePerson, year, measuresid, unit);
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "是否有新增权限。", action = ActionIsAdd.class)
	@GET
	@Path("isadd")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void isAdd(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionIsAdd.Wo> result = new ActionResult<>();

		try {
			result = new ActionIsAdd().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "校验SequenceNumber", action = ActionVerifySequenceNumber.class)
	@POST
	@Path("verifysn")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void VerifySequenceNumber(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("部门五项重点工作") JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionVerifySequenceNumber.Wo> result = new ActionResult<>();
		Boolean check = true;

		BaseAction.Wi wrapIn = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, BaseAction.Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new Exception(e);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (null == wrapIn.getKeyworkyear() || wrapIn.getKeyworkyear().isEmpty()) {
			check = false;
			Exception exception = new Exception("keyworkyear can not be blank!");
			result.error(exception);
		}

		if (null == wrapIn.getSequencenumber() || wrapIn.getSequencenumber() <= 0) {
			check = false;
			Exception exception = new Exception("sequencenumber can not be blank! begin from 1.");
			result.error(exception);
		}

		if (check) {
			try {
				result = new ActionVerifySequenceNumber().excute(wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件的五项重点工作,ordersymbol=ASC|DESC，第几页，每页几条", action = ActionListWithFilterPageCount.class)
	@PUT
	@Path("filter/listrelated/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListAllAndRelatedWithFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("第几页（从1开始）") @PathParam("page") Integer page, @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListAllAndRelatedWithFilter.WoKeyworkWithMeasures>> result = new ActionResult<>();
		BaseAction.Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, BaseAction.Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new Exception(e);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (null == wrapIn.getKeyworkyear() || wrapIn.getKeyworkyear().isEmpty()) {
				Exception exception = new Exception("过滤条件，年份必须填写.");
				result.error(exception);
				check = false;
			}
		}

		if (check) {
			try {
				//result = new ActionListWithFilterPageCount().execute(request, effectivePerson, page, count, wrapIn);
				ActionListAllAndRelatedWithFilter _ActionListAllAndRelatedWithFilter = new ActionListAllAndRelatedWithFilter();
				List<WoKeyworkWithMeasures> objs = _ActionListAllAndRelatedWithFilter.execute(request, effectivePerson, page, count, wrapIn);
				result.setData(objs);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}