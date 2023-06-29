package com.x.processplatform.assemble.surface.jaxrs.sign;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.core.entity.content.DocSign;

class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			DocSign docSign = emc.find(id, DocSign.class);
			if (null == docSign) {
				throw new ExceptionEntityNotExist(id, DocSign.class);
			}
			if (BooleanUtils.isNotTrue(new JobControlBuilder(effectivePerson, business, docSign.getJob())
					.enableAllowVisit().build().getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, id);
			}
			Wo wo = Wo.copier.copy(docSign);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends DocSign {

		private static final long serialVersionUID = -5697087829473068711L;

		static WrapCopier<DocSign, Wo> copier = WrapCopierFactory.wo(DocSign.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
