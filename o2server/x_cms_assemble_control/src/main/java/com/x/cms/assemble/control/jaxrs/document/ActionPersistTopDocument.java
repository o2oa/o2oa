package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.core.entity.Document;

public class ActionPersistTopDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistTopDocument.class);

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
				documentPersistService.topDocument( document.getId() );
				
				Wo wo = new Wo();
				wo.setId( document.getId() );
				result.setData( wo );				
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "文档置顶时发生异常。Id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
				throw exception;
			}
		}
		
		if (check) {
			new CmsBatchOperationPersistService().addOperation( 
					CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT, 
					CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  document.getId(),  document.getId(), "刷新文档权限：ID=" +  document.getId() );
		}

		ApplicationCache.notify( Document.class );

		return result;
	}
	
	public static class Wo extends WoId {

	}
}