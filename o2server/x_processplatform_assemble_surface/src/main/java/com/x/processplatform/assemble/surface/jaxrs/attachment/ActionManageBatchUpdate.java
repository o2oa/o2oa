package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageBatchUpdate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageBatchUpdate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String ids, String fileName, byte[] bytes,
			FormDataContentDisposition disposition, String extraParam) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			LOGGER.print("manageBatchUpdate receive id:{}, fileName:{}, effectivePerson:{}.", ids, fileName,
					effectivePerson.getDistinguishedName());
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (BooleanUtils.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "",""))) {
				throw new ExceptionAccessDenied(effectivePerson);
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
			if (StringUtils.isNotEmpty(ids) && bytes != null && bytes.length > 0) {
				String[] idArray = ids.split(",");
				for (String id : idArray) {
					Attachment attachment = emc.find(id.trim(), Attachment.class);
					if (attachment != null) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
								attachment.getStorage());
						emc.beginTransaction(Attachment.class);
						attachment.updateContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
						if (BooleanUtils.isTrue(Config.query().getExtractImage())
								&& ExtractTextTools.supportImage(attachment.getName())
								&& ExtractTextTools.available(bytes)) {
							attachment.setText(ExtractTextTools.image(bytes));
							LOGGER.debug("filename:{}, file type:{}, text:{}.", attachment.getName(),
									attachment.getType(), attachment.getText());
						}
						emc.commit();

					}
				}
			}

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionManageBatchUpdate$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -7244253863926345214L;

	}

}
