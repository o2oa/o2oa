package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.WiAttachment;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionCopyToWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCopyToWorkCompleted.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workCompletedId, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, workCompletedId:{}.", effectivePerson::getDistinguishedName, () -> workCompletedId);

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		WorkCompleted workCompleted = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			workCompleted = emc.flag(workCompletedId, WorkCompleted.class);
			if ((null == workCompleted)) {
				throw new ExceptionEntityNotExist(workCompletedId, WorkCompleted.class);
			}
			if (BooleanUtils.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson,
					workCompleted.getApplication(), workCompleted.getProcess()))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}

		if (ListTools.isNotEmpty(wi.getAttachmentList())) {
			for (WiAttachment w : wi.getAttachmentList()) {
				OnlineInfo onlineInfo = this.getOnlineInfo(effectivePerson, w.getCopyFrom(), w.getId());
				if(BooleanUtils.isNotTrue(onlineInfo.getCanRead())){
					throw new ExceptionAccessDenied(effectivePerson, w.getId());
				}
				if(StringUtils.isNotBlank(w.getName())){
					this.verifyConstraint(0, w.getName(), null);
				}else{
					w.setName(onlineInfo.getName());
				}
				w.setName(this.checkName(workCompleted.getJob(), w.getName()));
			}
			wos = ThisApplication.context().applications()
					.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
							Applications.joinQueryUri("attachment", "copy", "workcompleted", workCompleted.getId()), wi, workCompleted.getJob())
					.getDataAsList(Wo.class);
		}

		result.setData(wos);
		return result;
	}

	private String checkName(String job, String fileName) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return this.adjustFileName(business, job, fileName);
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -455300115594765428L;

		@FieldDescribe("附件对象列表.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiAttachment",
				fieldValue = "{'id':'附件id','name':'附件名称','site':'附件框分类','copyFrom':'附件来源(cms|内容管理附件、processPlatform|流程平台附件、x_pan_assemble_control|企业网盘附件，默认为processPlatform)'}")
		private List<WiAttachment> attachmentList = new ArrayList<>();

		public List<WiAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WiAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 6235554869680662821L;

	}

}
