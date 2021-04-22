package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.DocumentPersistService;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.cms.core.entity.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ActionRefreshAllDocumentPermission extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRefreshAllDocumentPermission.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		Wo wo = new Wo();
		DocumentPersistService documentPersistService = new DocumentPersistService();

		if (check) {
			try {
				documentPersistService.refreshAllDocumentPermission();
				wo.setValue("权限处理完成！");
				result.setData(wo);
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentPermissionProcess();
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		CacheManager.notify(Document.class);
		return result;
	}

	public static class Wo extends WrapString {
	}
}
