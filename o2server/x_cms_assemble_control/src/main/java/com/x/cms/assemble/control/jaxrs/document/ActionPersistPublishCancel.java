package com.x.cms.assemble.control.jaxrs.document;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;

public class ActionPersistPublishCancel extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistPublishCancel.class);

	@AuditLog(operation = "取消文档发布状态")
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Document document = null;
		Boolean check = true;

		if (check) {
			try {
				document = documentQueryService.get(id);
				if (null == document) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "文档信息获取操作时发生异常。Id:" + id + ", Name:" + effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				modifyDocStatus(id, "draft", effectivePerson.getDistinguishedName());
				document.setDocStatus("draft");
				document.setPublishTime(new Date());
				document = documentPersistService.refreshDocInfoData( document );

				CacheManager.notify(Document.class);

				Wo wo = new Wo();
				wo.setId( document.getId() );
				result.setData( wo );
				
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统将文档状态修改为发布状态时发生异常。Id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				logService.log(emc, effectivePerson.getDistinguishedName(),
						document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(),
						document.getCategoryId(), document.getId(), "", "DOCUMENT", "取消发布");
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		if (check) {
			try {
				documentPersistService.refreshDocumentPermission( id, null, null );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在收回已发布文档访问管理权限信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}