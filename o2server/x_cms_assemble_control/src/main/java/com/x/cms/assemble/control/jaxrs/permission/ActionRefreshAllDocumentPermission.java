package com.x.cms.assemble.control.jaxrs.permission;

import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.DocumentPersistService;
import com.x.cms.core.entity.Document;

public class ActionRefreshAllDocumentPermission extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRefreshAllDocumentPermission.class);
	private static ReentrantLock lock = new ReentrantLock();

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		Wo wo = new Wo();
		DocumentPersistService documentPersistService = new DocumentPersistService();

		if (check) {
			try {
				if(lock.tryLock()) {
					logger.info("开始更新所有文档权限........");
					documentPersistService.refreshAllDocumentPermission(true);
					logger.info("完成更新所有文档权限........");
					wo.setValue("权限处理完成！");
					lock.unlock();
				}else{
					wo.setValue("正在处理中！");
				}
				result.setData(wo);
			} catch (Exception e) {
				lock.unlock();
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
