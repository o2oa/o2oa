package com.x.cms.assemble.control.jaxrs.permission;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.DocumentPersistService;
import com.x.cms.core.entity.Document;

public class ActionRefreshCategoryDocPermission extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRefreshCategoryDocPermission.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String categoryId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		Wo wo = new Wo();
		DocumentPersistService documentPersistService = new DocumentPersistService();

		if (check) {
			try {
				boolean flag = documentPersistService.refreshDocumentPermissionByCategory(categoryId);
				if(flag) {
					wo.setValue("权限处理完成！");
				}else{
					wo.setValue("权限刷新失败，可能有其他分类正在刷新权限！");
				}
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
