package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionUpdateContent extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdateContent.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug("receive id:{}, workId:{}, fileName:{}, extraParam:{}.", id, workId, jsonElement.toString());
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			/* 后面要重新保存 */
			Work work = emc.find(workId, Work.class);
			/** 判断work是否存在 */
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			String fileName = wi.getFileName();
			if (StringUtils.isEmpty(fileName)) {
				throw new Exception("fileName can not empty!");
			}
			fileName = fileName + "." + attachment.getExtension();
			if(!fileName.equalsIgnoreCase(attachment.getName())){
				fileName = this.adjustFileName(business, work.getJob(), fileName);
			}
			/* 统计待办数量判断用户是否可以上传附件 */
			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			emc.beginTransaction(Attachment.class);
			attachment.setName(fileName);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi {
		@FieldDescribe("文件名称,不带扩展名的文件名.")
		private String fileName;


		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}

	public static class Wo extends WoId {

	}

	public static class WoControl extends WorkControl {
	}

}
