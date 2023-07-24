package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.exception.ExceptionFileNameInvalid;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.ActionUpdateContentWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionUpdateContent extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateContent.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			LOGGER.debug("receive id:{}, workId:{}, fileName:{}, extraParam:{}.", id, workId, jsonElement.toString());
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			// 后面要重新保存
			Work work = emc.find(workId, Work.class);
			// 判断work是否存在
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			String fileName = wi.getFileName();
			if (StringUtils.isEmpty(fileName)) {
				throw new ExceptionFieldEmpty("fileName");
			}
			fileName = fileName + "." + attachment.getExtension();
			if (!StringTools.isFileName(fileName)) {
				throw new ExceptionFileNameInvalid(fileName);
			}
			if (!fileName.equalsIgnoreCase(attachment.getName())) {
				fileName = this.adjustFileName(business, work.getJob(), fileName);
			}
			// 统计待办数量判断用户是否可以上传附件
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
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

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionUpdateContent$Wi")
	public static class Wi extends ActionUpdateContentWi {

		private static final long serialVersionUID = 5219291599575309241L;

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 370663344764335319L;

	}

}
