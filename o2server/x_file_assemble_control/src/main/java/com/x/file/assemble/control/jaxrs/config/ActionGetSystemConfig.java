package com.x.file.assemble.control.jaxrs.config;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.FileConfig;

class ActionGetSystemConfig extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			/* 判断当前用户是否有权限访问 */
			if(!business.controlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			FileConfig config = emc.firstEqual(FileConfig.class, FileConfig.person_FIELDNAME, Business.SYSTEM_CONFIG);
			if(config==null){
				config = new FileConfig();
				config.setPerson(Business.SYSTEM_CONFIG);
				config.setCapacity(0);
			}

			Wo wo = Wo.copier.copy(config);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends FileConfig {

		private static final long serialVersionUID = -5752008990171522428L;

		static WrapCopier<FileConfig, Wo> copier = WrapCopierFactory.wo(FileConfig.class, Wo.class, null,
				FieldsInvisible);


	}
}
