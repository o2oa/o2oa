package com.x.cms.assemble.control.jaxrs.comment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;

public class ActionPersistUnCommend extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistUnCommend.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String commentId, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;

		if (check) {
			try {				
				List<String> ids = commentCommendPersistService.deleteWithCommentId( commentId, effectivePerson.getDistinguishedName() );		
				Wo wo = new Wo();
				wo.setIds( ids );
				result.setData( wo );				
			} catch (Exception e) {
				Exception exception = new ExceptionCommentPersist(e, "系统根据个人和文档评论ID删除点赞信息时发生异常。commentId:" + commentId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
				throw exception;
			}
		}

		ApplicationCache.notify( Document.class );		
		
		return result;
	}
	
	public static class Wo {
		
		private List<String> ids = null;

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}
		
		
	}
}