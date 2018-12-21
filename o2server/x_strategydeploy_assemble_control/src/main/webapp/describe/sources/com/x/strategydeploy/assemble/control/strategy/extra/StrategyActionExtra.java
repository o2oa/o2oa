package com.x.strategydeploy.assemble.control.strategy.extra;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
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
import com.x.strategydeploy.assemble.control.strategy.ActionListIncludeMeasures;
import com.x.strategydeploy.assemble.control.strategy.ActionListIncludeMeasures.WoIncludeMeasures;
import com.x.strategydeploy.assemble.control.strategy.BaseAction;

@Path("strategydeployextra")
@JaxrsDescribe("战略部署额外服务")
public class StrategyActionExtra extends StandardJaxrsAction {
	private static  Logger logger = LoggerFactory.getLogger(StrategyActionExtra.class);

	@JaxrsMethodDescribe(value = "根据年份列出战略部署和举措关系的JSON", action = ActionListIncludeMeasures.class)
	@PUT
	@Path("listbyyear")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listbyyear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("Json信息") JsonElement jsonElement) {
		ActionResult<List<WoIncludeMeasures>> result = new ActionResult<>();
		BaseAction.Wi wrapIn = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		boolean ispass = true;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wrapIn = this.convertToWrapIn(jsonElement, BaseAction.Wi.class);
			//result = new ActionListIncludeMeasures().execute(wrapIn);
		} catch (Exception e) {
			logger.warn("strategydeployextra iswork a error!");
			logger.error(e);
			result.error(e);
		}
		if (null == wrapIn.getStrategydeployyear() || wrapIn.getStrategydeployyear().isEmpty()) {
			Exception e = new Exception("strategydeployyear can not be blank");
			result.error(e);
			ispass = false;
		} 
		if(ispass) {
			try {
				result = new ActionListIncludeMeasures().execute(wrapIn);
			} catch (Exception e) {
				result.error(e);
				ispass = false;
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}
