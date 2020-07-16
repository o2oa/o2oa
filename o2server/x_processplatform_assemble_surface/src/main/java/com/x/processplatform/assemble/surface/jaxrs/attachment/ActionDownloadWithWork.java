package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import org.apache.commons.lang3.StringUtils;

class ActionDownloadWithWork extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId, String fileName) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Work work = null;
		Attachment attachment = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(workId, Work.class);
			/** 判断work是否存在 */
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			/** 判断attachment是否存在 */
			attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			/** 生成当前用户针对work的权限控制,并判断是否可以访问 */
			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
		}
		StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
				attachment.getStorage());
		if(StringUtils.isBlank(fileName)){
			fileName = attachment.getName();
		}else{
			String extension = FilenameUtils.getExtension(fileName);
			if(StringUtils.isEmpty(extension)){
				fileName = fileName+ "." + attachment.getExtension();
			}
		}
		Wo wo = new Wo(attachment.readContent(mapping), this.contentType(false, fileName),
				this.contentDisposition(false, fileName));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

	public static class WoControl extends WorkControl {
	}
}
