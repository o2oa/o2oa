package com.x.strategydeploy.assemble.control.configsys;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.configsys.exception.ExceptionConfigSysNameEmpty;
import com.x.strategydeploy.assemble.control.configsys.exception.ExceptionWrapInConvert;
import com.x.strategydeploy.core.entity.StrategyConfigSys;

public class ActionSave extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	//出
	public static class Wo extends WoId {
	}

	//执行保存
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		StrategyConfigSys strategyconfigsys = new StrategyConfigSys();
		//Wo wrapOutId = null;
		Boolean IsPass = true;
		Wi wrapIn = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			IsPass = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		logger.info(wrapIn.toString());

		if (IsPass) {
			//标题不能为空
			if (null == wrapIn.getTitle() || wrapIn.getTitle().isEmpty()) {
				IsPass = false;
				Exception exception = new ExceptionConfigSysNameEmpty();
				result.error(exception);
			}
		}

		if (IsPass) {
			//标题不能超过70
			if (wrapIn.getTitle().length() > 70) {
				IsPass = false;
				Exception exception = new ExceptionConfigSysNameEmpty();
				result.error(exception);
			}
		}

		
		if (IsPass) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//				WrapCopier<Wi, StrategyConfigSys> beanCopyTools = WrapCopierFactory.wi(Wi.class, StrategyConfigSys.class, null,excludes);
//				beanCopyTools.copy(wrapIn, strategyconfigsys);
				strategyconfigsys = Wi.copier.copy(wrapIn);
				Business business = new Business(emc);
				emc.beginTransaction(StrategyConfigSys.class);
				emc.persist(strategyconfigsys,CheckPersistType.all);
				emc.commit();
			}

			//strategyconfigsys = strategyDeployOperationService.save(wrapIn);
			logger.info("strategyconfigsys:" + strategyconfigsys.getId());
			Wo wo = new Wo();
			wo.setId(strategyconfigsys.getId());
			result.setData(wo);
			//result.setData(wrapOutId);
		}

		return result;
	}
}
