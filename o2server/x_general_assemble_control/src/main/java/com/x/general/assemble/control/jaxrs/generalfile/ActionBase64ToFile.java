package com.x.general.assemble.control.jaxrs.generalfile;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.FileTools;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class ActionBase64ToFile extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBase64ToFile.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("effectivePerson: {}.", effectivePerson::getDistinguishedName);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isBlank(wi.getContent())){
			throw new ExceptionFieldEmpty("content");
		}
		if(StringUtils.isBlank(wi.getFileName())){
			throw new ExceptionFieldEmpty("fileName");
		}
		FileTools.verifyConstraint(10, wi.getFileName(), null);
		ActionResult<Wo> result = new ActionResult<>();
		byte[] bytes = Base64.decodeBase64(wi.getContent());
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String name = wi.getFileName();
			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(
					GeneralFile.class);
			GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name,
					effectivePerson.getDistinguishedName());
			generalFile.saveContent(gfMapping, bytes, name, Config.general().getStorageEncrypt());
			emc.beginTransaction(GeneralFile.class);
			emc.persist(generalFile, CheckPersistType.all);
			emc.commit();
			wo.setId(generalFile.getId());
		}

		result.setData(wo);
		return result;
	}

	public class Wi extends GsonPropertyObject {
		private static final long serialVersionUID = -670631145209495465L;
		@FieldDescribe("base64文本.")
		private String content;
		@FieldDescribe("转换文件名称，需带扩展名.")
		private String fileName;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}

	public class Wo extends WoId {
		private static final long serialVersionUID = -6210739068105920249L;

	}

}
