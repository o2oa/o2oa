package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.GeneralFile;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.assemble.control.jaxrs.task.BaseAction;

class ActionExportResult extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExportResult.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			GeneralFile generalFile = emc.find(flag, GeneralFile.class);
			if(generalFile!=null){
				StorageMapping gfMapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
						generalFile.getStorage());
				Wo wo = new Wo(generalFile.readContent(gfMapping), this.contentType(true, generalFile.getName()),
						this.contentDisposition(true, generalFile.getName()));
				result.setData(wo);
			} else {
				throw new ExceptionExcelResultObject(flag);
			}
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
