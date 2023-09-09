package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCallback;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

@Deprecated
class ActionUpdateCallback extends BaseAction {
	ActionResult<Wo<WoObject>> execute(EffectivePerson effectivePerson, String id, String workId, String callback,
			String fileName, byte[] bytes, FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo<WoObject>> result = new ActionResult<>();
			Business business = new Business(emc);
			// 后面要重新保存
			Work work = emc.find(workId, Work.class);
			// 判断work是否存在
			if (null == work) {
				throw new ExceptionWorkNotExistCallback(callback, workId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExistCallback(callback, id);
			}
			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}
			if (!fileName.equalsIgnoreCase(attachment.getName())) {
				fileName = this.adjustFileName(business, work.getJob(), fileName);
			}
			this.verifyConstraint(bytes.length, fileName, null);

			// 统计待办数量判断用户是否可以上传附件
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowProcessing().build();
			if (BooleanUtils.isNotTrue(control.getAllowProcessing())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			emc.beginTransaction(Attachment.class);
			attachment.updateContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes, fileName));
			emc.commit();
			WoObject woObject = new WoObject();
			woObject.setId(attachment.getId());
			Wo<WoObject> wo = new Wo<>(callback, woObject);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo<T> extends WoCallback<T> {
		public Wo(String callbackName, T t) {
			super(callbackName, t);
		}
	}

	public static class WoObject extends WoId {

		private static final long serialVersionUID = 6522273856576987533L;
	}

}
