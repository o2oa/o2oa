package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplicationDict;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<WrapOutApplicationDict> execute(EffectivePerson effectivePerson, String applicationDictFlag,
			String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutApplicationDict> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			String id = business.applicationDict().getWithApplicationWithUniqueName(application.getId(),
					applicationDictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionApplicationDictNotExist(applicationFlag);
			}
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			WrapOutApplicationDict wrap = copier.copy(dict);
			wrap.setData(this.get(business, dict));
			result.setData(wrap);
			return result;
		}
	}

}
