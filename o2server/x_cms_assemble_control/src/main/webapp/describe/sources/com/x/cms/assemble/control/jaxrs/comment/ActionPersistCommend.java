package com.x.cms.assemble.control.jaxrs.comment;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommentCommend;
import com.x.cms.core.entity.DocumentCommentInfo;

public class ActionPersistCommend extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistCommend.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		DocumentCommentInfo comment = null;
		Boolean check = true;

		if (check) {
			try {
				comment = documentCommentInfoQueryService.get( id );
				if (null == comment) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(id);
					result.error(exception);
					throw exception;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCommentQuery(e, "文档评论信息获取操作时发生异常。Id:" + id + ", Name:" + effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {				
				DocumentCommentCommend commentCommend = commentCommendPersistService.create( effectivePerson.getDistinguishedName(), id );
				Wo wo = new Wo();
				wo.setId( commentCommend.getId() );
				result.setData( wo );				
			} catch (Exception e) {
				Exception exception = new ExceptionCommentPersist(e, "给文档评论点赞时时发生异常。Id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
				throw exception;
			}
		}

		ApplicationCache.notify( Document.class );

		return result;
	}
	
	public static class Wo extends WoId {

	}
}