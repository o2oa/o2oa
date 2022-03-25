package com.x.organization.assemble.control.jaxrs.inputperson;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.GeneralFile;
import com.x.organization.assemble.control.ThisApplication;

public class ActionGetResult extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetResult.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		logger.debug(effectivePerson, "flag:{}.", flag);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			GeneralFile generalFile = emc.find(flag, GeneralFile.class);
			if(generalFile!=null){
				if (!StringUtils.equals(effectivePerson.getDistinguishedName(), generalFile.getPerson())) {
					throw new ExceptionAccessDenied(effectivePerson);
				}
				StorageMapping gfMapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
						generalFile.getStorage());
				wo = new Wo(generalFile.readContent(gfMapping), this.contentType(true, generalFile.getName()),
						this.contentDisposition(true, generalFile.getName()));
				result.setData(wo);

				generalFile.deleteContent(gfMapping);
				emc.beginTransaction(GeneralFile.class);
				emc.delete(GeneralFile.class, generalFile.getId());
				emc.commit();
			} else {
				throw new ExceptionInputResultObject(flag);
			}

		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}