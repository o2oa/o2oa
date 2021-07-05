package com.x.cms.assemble.control.jaxrs.fileinfo;


import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

class ActionChangeOrderNumber extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionChangeOrderNumber.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String docId, Integer seqNumber)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			FileInfo file = emc.find(id, FileInfo.class);
			if (null == file) {
				throw new ExceptionEntityNotExist(id, FileInfo.class);
			}
			Document doc = documentQueryService.get(docId);
			if (null == doc) {
				throw new ExceptionEntityNotExist(docId, Document.class);
			}
			CategoryInfo categoryInfo = categoryInfoServiceAdv.get( doc.getCategoryId() );
			if (null == categoryInfo) {
				throw new ExceptionEntityNotExist(doc.getCategoryId(), CategoryInfo.class);
			}
			AppInfo appInfo = appInfoServiceAdv.get( categoryInfo.getAppId() );
			if (null == appInfo) {
				throw new ExceptionEntityNotExist(categoryInfo.getAppId(), AppInfo.class);
			}
			if ( !documentQueryService.getFileInfoManagerAssess( effectivePerson, doc, categoryInfo, appInfo ) ) {
				throw new ExceptionDocumentAccessDenied(effectivePerson.getDistinguishedName(), doc.getTitle(), doc.getId());
			}
			emc.beginTransaction(FileInfo.class);
			file.setSeqNumber(seqNumber);
			emc.check(file, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(file.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
