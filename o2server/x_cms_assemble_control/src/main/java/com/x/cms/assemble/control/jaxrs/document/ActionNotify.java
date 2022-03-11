package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.query.DocumentNotify;

/**
 * 文档发送消息通知
 * @author sword
 */
public class ActionNotify extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionNotify.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );

		Document document = documentQueryService.get(id);
		if(document == null){
			throw new ExceptionEntityNotExist(id);
		}
		if(!Document.DOC_STATUS_PUBLISH.equals(document.getDocStatus())){
			throw new IllegalStateException("文档未发布");
		}
		wi.setDocumentId(id);

		AppInfo appInfo = appInfoServiceAdv.get(document.getAppId());
		if(appInfo == null){
			if(document == null){
				throw new ExceptionEntityNotExist(document.getAppId(), AppInfo.class);
			}
		}

		Business business = new Business(null);
		if(!effectivePerson.getDistinguishedName().equals(document.getCreatorPerson()) && !business.isAppInfoManager(effectivePerson, appInfo)){
			throw new ExceptionAccessDenied(effectivePerson);
		}

		ThisApplication.queueSendDocumentNotify.send(wi);
		result.setData(new Wo(true));
		return result;
	}

	public static class Wi extends DocumentNotify {

	}

	public static class Wo extends WrapBoolean {
		public Wo(Boolean value){
			super(value);
		}
	}
}
