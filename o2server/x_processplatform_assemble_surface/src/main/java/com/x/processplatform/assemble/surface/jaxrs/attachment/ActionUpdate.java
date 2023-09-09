package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionUpdate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId, String fileName, byte[] bytes,
			FormDataContentDisposition disposition, String extraParam) throws Exception {
		LOGGER.debug("receive id:{}, workId:{}, fileName:{}, extraParam:{}.", id, workId, fileName, extraParam);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
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
			// 天谷印章扩展
			if (StringUtils.isNotEmpty(extraParam)) {
				WiExtraParam wiExtraParam = gson.fromJson(extraParam, WiExtraParam.class);
				if (StringUtils.isNotEmpty(wiExtraParam.getFileName())) {
					fileName = wiExtraParam.getFileName();
				}
			}

			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}
			if (!fileName.equalsIgnoreCase(attachment.getName())) {
				fileName = this.adjustFileName(business, work.getJob(), fileName);
			}
			// 统计待办数量判断用户是否可以上传附件
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}

			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPerson(effectivePerson);
			boolean canEdit = this.edit(attachment, effectivePerson, identities, units, business);
			if (!canEdit) {
				throw new ExceptionAccessDenied(effectivePerson, attachment);
			}

			this.verifyConstraint(bytes.length, fileName, null);

			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			emc.beginTransaction(Attachment.class);
			attachment.updateContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes, fileName));
			LOGGER.debug("filename:{}, file type:{}.", attachment.getName(), attachment.getType());
			if (BooleanUtils.isTrue(Config.query().getExtractImage())
					&& ExtractTextTools.supportImage(attachment.getName()) && ExtractTextTools.available(bytes)) {
				attachment.setText(ExtractTextTools.image(bytes));
				LOGGER.debug("filename:{}, file type:{}, text:{}.", attachment.getName(), attachment.getType(),
						attachment.getText());
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionUpdate$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 7301158712641170039L;

	}

}
