package com.x.strategydeploy.assemble.control.measures.extra;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.measures.ActionListDeptsByYear;
import com.x.strategydeploy.assemble.control.measures.ActionsListRelatedStrategy;
import com.x.strategydeploy.assemble.control.measures.ActionsListRelatedStrategy.WoRelatedStrategy;
import com.x.strategydeploy.assemble.control.measures.BaseAction;

@Path("measuresextra")
@JaxrsDescribe("举措额外服务")
public class MeasuresActionExtra extends StandardJaxrsAction {
	private static  Logger logger = LoggerFactory.getLogger(MeasuresActionExtra.class);

	@JaxrsMethodDescribe(value = "根据年份列出举措和战略部署关系的JSON", action = ActionsListRelatedStrategy.class)
	@POST
	@Path("listbyyear")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listbyyear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("Json信息") JsonElement jsonElement) {
		ActionResult<List<WoRelatedStrategy>> result = new ActionResult<>();
		BaseAction.Wi wrapIn = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		boolean ispass = true;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wrapIn = this.convertToWrapIn(jsonElement, BaseAction.Wi.class);
		} catch (Exception e) {
			logger.warn("measuresactionextra listbyyear a error!");
			logger.error(e);
			result.error(e);
		}
		if (null == wrapIn.getMeasuresinfoyear() || wrapIn.getMeasuresinfoyear().isEmpty()) {
			Exception e = new Exception("measuresinfoyear can not be blank");
			result.error(e);
			ispass = false;
		} 
		if(ispass) {
			try {
				result = new ActionsListRelatedStrategy().execute(wrapIn);
			} catch (Exception e) {
				result.error(e);
				ispass = false;
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	
	@JaxrsMethodDescribe(value = "列出某一个年份,拥有举措的部门列表，去重。", action = StandardJaxrsAction.class)
	@GET
	@Path("listdept/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listdepartmentsbyyear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("年份") @PathParam("year") String year) {
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
}
