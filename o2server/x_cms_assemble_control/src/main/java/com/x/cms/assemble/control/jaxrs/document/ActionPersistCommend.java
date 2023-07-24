package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;

/**
 * 文档点赞
 * @author sword
 */
public class ActionPersistCommend extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistCommend.class);

	@AuditLog(operation = "文档点赞")
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Document document = null;
		Boolean check = true;

		if (check) {
			try {
				document = documentQueryService.get( id );
				if (null == document) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(id);
					result.error(exception);
					throw exception;
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
				DocumentCommend documentCommend = docCommendPersistService.create( effectivePerson.getDistinguishedName(), document, null);
				Wo wo = new Wo();
				wo.setId( documentCommend.getId() );
				result.setData( wo );
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "给文档点赞时时发生异常。Id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
				throw exception;
			}
		}

		CacheManager.notify( Document.class );

		return result;
	}

	public static class Wo extends WoId {

	}
}
