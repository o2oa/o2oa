package com.x.pan.assemble.control.jaxrs.attachment2;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.core.entity.open.OriginFile;
import com.x.pan.assemble.control.Business;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

class ActionCheckUpload extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(FilenameUtils.getExtension(wi.getFileName()))) {
				throw new ExceptionEmptyExtension(wi.getFileName());
			}
			ActionResult<Wo> result = new ActionResult<>();
			this.verifyConstraint(business, effectivePerson.getDistinguishedName(), wi.getFileSize(), wi.getFileName());
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

	public static class Wi extends GsonPropertyObject{
		@FieldDescribe("附件大小.")
		private Long fileSize;

		@FieldDescribe("附件名称.")
		private String fileName;

		public Long getFileSize() {
			return fileSize == null ? 0L : fileSize;
		}

		public void setFileSize(Long fileSize) {
			this.fileSize = fileSize;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}
}
