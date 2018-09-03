package com.x.strategydeploy.assemble.control.measures;

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
import com.x.strategydeploy.assemble.control.measures.ActionUpdateSort.SortWo;

@Path("measures")
@JaxrsDescribe("举措服务")
public class MeasuresAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(MeasuresAction.class);

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
		try {
			ActionListDeptsByYear.Wo wo = new ActionListDeptsByYear.Wo();
			ActionListDeptsByYear actionlistdeptbyyear = new ActionListDeptsByYear();
			wo = actionlistdeptbyyear.execute(request, effectivePerson, "2017");
			ActionResult<ActionListDeptsByYear.Wo> result1 = new ActionResult<>();
			result1.setData(wo);
			asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		wrap.setValue("measures iswork  is work!! Token:" + effectivePerson.getToken() + " Name:" + effectivePerson.getDistinguishedName() + " TokenType:" + effectivePerson.getTokenType());
		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "新建或者更新举措", action = ActionSave.class)
	@POST
	@Path("save")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("举措信息") JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if (check) {
			try {
				result = new ActionSave().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
				result.error(e);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取 举措 对象", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("举措对象ID") @PathParam("id") String id) {
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

	@JaxrsMethodDescribe(value = "根据ID删除 举措 对象，返回id", action = ActionGet.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("举措对象ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
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

	@JaxrsMethodDescribe(value = "列示满足过滤条件的举措,ordersymbol=ASC|DESC，第几页，每页几条", action = ActionListDescWithFilterPageCount.class)
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
			if (null == wrapIn.getMeasuresinfoyear() || wrapIn.getMeasuresinfoyear().isEmpty()) {
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

	@JaxrsMethodDescribe(value = "列示满足过滤条件的战略部署,降序排列，第几页，每页几条", action = ActionListDescWithFilterPageCount.class)
	@PUT
	@Path("filter/list/{page}/desc/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDescWithFilterPageCount(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("第几页（从1开始）") @PathParam("page") Integer page, @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
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
			if (null == wrapIn.getMeasuresinfoyear() || wrapIn.getMeasuresinfoyear().isEmpty()) {
				Exception exception = new Exception("过滤条件，年份必须填写.");
				result.error(exception);
				check = false;
			}
		}

		if (check) {
			try {
				result = new ActionListDescWithFilterPageCount().execute(request, effectivePerson, page, count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件的战略部署,升序排列，第几页，每页几条", action = ActionListDescWithFilterPageCount.class)
	@PUT
	@Path("filter/list/{page}/asc/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAscWithFilterPageCount(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("第几页（从1开始）") @PathParam("page") Integer page, @JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
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
			if (null == wrapIn.getMeasuresinfoyear() || wrapIn.getMeasuresinfoyear().isEmpty()) {
				Exception exception = new Exception("过滤条件，年份必须填写.");
				result.error(exception);
				check = false;
			}
		}

		if (check) {
			try {
				result = new ActionListAscWithFilterPageCount().execute(request, effectivePerson, page, count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件的举措,下一页", action = ActionListNextWithFilter.class)
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
			try {
				result = new ActionListNextWithFilter().execute(request, effectivePerson, id, count, wrapIn);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示满足过滤条件的举措,上一页", action = ActionListPrevWithFilter.class)
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

	@JaxrsMethodDescribe(value = "列出某一个年份的举措", action = StandardJaxrsAction.class)
	@GET
	@Path("filter/list/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByYear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			logger.info("listByYear:" + year);
			result = new ActionListByYear().execute(request, effectivePerson, year);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列出某一个ParentId的举措", action = StandardJaxrsAction.class)
	@GET
	@Path("filter/list/parentid/{parentid}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByParentId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("工作重点Id") @PathParam("parentid") String parentid) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListByParentId().execute(request, effectivePerson, parentid);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列出某一个年份、某一个部门的举措", action = StandardJaxrsAction.class)
	@PUT
	@Path("filter/list/{year}/{dept}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByYearAndDept(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year, @JaxrsParameterDescribe("部门") @PathParam("dept") String dept) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<BaseAction.Wo>> result = new ActionResult<>();

		String deptString = dept;

		try {
			result = new ActionListByYearAndDept().execute(request, effectivePerson, year, deptString);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列出某一个年份,拥有举措的部门列表，去重。", action = StandardJaxrsAction.class)
	@GET
	@Path("listdept/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDeptsByYear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionListDeptsByYear.Wo> result = new ActionResult<>();
		ActionListDeptsByYear.Wo wo = new ActionListDeptsByYear.Wo();

		try {
			wo = new ActionListDeptsByYear().execute(request, effectivePerson, year);
			result.setData(wo);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.setData(wo);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "列出某一个年份,拥有举措的部门列表，去重。", action = StandardJaxrsAction.class)
	@GET
	@Path("listdeptobjs/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDeptObjectsByYear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListDeptObjectsByYear.UnitML>> result = new ActionResult<List<ActionListDeptObjectsByYear.UnitML>>();
		List<ActionListDeptObjectsByYear.UnitML> wos = new ArrayList<ActionListDeptObjectsByYear.UnitML>();

		try {
			wos = new ActionListDeptObjectsByYear().execute(request, effectivePerson, year);
			result.setData(wos);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.setData(wos);
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
			wo = new ActionListYears.Wo();
			result.setData(wo);
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

	@JaxrsMethodDescribe(value = "更新SequenceNumber", action = ActionUpdateSort.class)
	@POST
	@Path("updatesequencenumber")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void UpdateSequenceNumber(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("战略部署信息") JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<SortWo> result = new ActionResult<>();
		Boolean check = true;
		ActionUpdateSort.SortWi wrapIn = null;
		logger.info("jsonElement:"+jsonElement.toString());
		try {
			wrapIn = this.convertToWrapIn(jsonElement, ActionUpdateSort.SortWi.class);
		} catch (Exception e) {
			logger.info("拖拽排序，输入数据错误。");
			check = false;
			Exception exception = new Exception(e);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		try {
			//result = new ActionUpdateSort().execute(request, effectivePerson, 1, 5, wrapIn);
			logger.info("2222222222222222222");
			result = new ActionUpdateSort().execute(request, effectivePerson, wrapIn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "校验SequenceNumber", action = ActionVerifySequenceNumber.class)
	@POST
	@Path("verifysn")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void VerifySequenceNumber(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("举措信息") JsonElement jsonElement) {
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

		if (null == wrapIn.getMeasuresinfoyear() || wrapIn.getMeasuresinfoyear().isEmpty()) {
			check = false;
			Exception exception = new Exception("measuresinfoyear can not be blank!");
			result.error(exception);
		}

		//if (null == wrapIn.getSequencenumber() || wrapIn.getSequencenumber() <= 0) {
		if (null == wrapIn.getSequencenumber()) {
			check = false;
			Exception exception = new Exception("sequencenumber can not be blank! begin form 1");
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

	@JaxrsMethodDescribe(value = "列出某一个ParentId的举措最大章节号", action = StandardJaxrsAction.class)
	@GET
	@Path("getmaxsn/parentid/{parentid}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void GetMaxNumberByParentId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("工作重点Id") @PathParam("parentid") String parentid) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<WrapOutString>();
		try {
			result = new ActionGetMaxNumberByParentId().execute(request, effectivePerson, parentid);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}