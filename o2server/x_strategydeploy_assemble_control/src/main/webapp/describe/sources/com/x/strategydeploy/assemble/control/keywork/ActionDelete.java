package com.x.strategydeploy.assemble.control.keywork;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class ActionDelete extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	public static class Wo extends WoId {
	}

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String _id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			boolean IsExist = false;

			if (null == _id || _id.isEmpty()) {
				IsExist = false;
			} else {
				IsExist = business.keyworkInfoFactory().IsExistById(_id);
			}

			if (IsExist) {
				KeyworkInfo keyworkinfo = emc.find(_id, KeyworkInfo.class);
				emc.beginTransaction(KeyworkInfo.class);
				emc.remove(keyworkinfo);
				emc.commit();
				wo.setId(_id);
				result.setData(wo);
			} else {
				Exception exception = new Exception("keyworkinfo is not Exist !");
				result.error(exception);
			}

		}

		return result;

	}

}
