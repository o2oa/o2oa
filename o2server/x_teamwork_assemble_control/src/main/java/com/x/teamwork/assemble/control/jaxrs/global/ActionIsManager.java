package com.x.teamwork.assemble.control.jaxrs.global;

import javax.servlet.http.HttpServletRequest;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.Business;



public class ActionIsManager extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionIsManager.class);

	protected ActionResult<String> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<String> result = new ActionResult<>();
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()){
			Business business = new Business(emc);
			if(business.isManager(effectivePerson)){
				result.setData( "yes" );
			}else{
				result.setData( "no" );
			}
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		
		return result;
	}
	public static class Wo extends WoId {

	}
}