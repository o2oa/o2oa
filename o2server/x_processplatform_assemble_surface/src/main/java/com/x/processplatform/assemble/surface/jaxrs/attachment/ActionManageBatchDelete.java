package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;

import java.util.List;

class ActionManageBatchDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionManageBatchDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			logger.print("manageBatchDelete receive id:{}, effectivePerson:{}.", wi.getIdList(), effectivePerson.getDistinguishedName());
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if(!business.canManageApplication(effectivePerson, null)){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if(ListTools.isNotEmpty(wi.getIdList())){
				for (String id : wi.getIdList()){
					Attachment attachment = emc.find(id.trim(), Attachment.class);
					if(attachment!=null){
						logger.print("manageBatchDelete attachment:{}——{}", attachment.getId(), attachment.getName());
						StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
								attachment.getStorage());
						attachment.deleteContent(mapping);
						emc.beginTransaction(Attachment.class);
						emc.remove(attachment);
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

	public static class Wo extends WrapBoolean {

	}

	public static class Wi extends GsonPropertyObject{
		@FieldDescribe("待删除附件ID列表")
		private List<String> idList;

		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}
	}

}
