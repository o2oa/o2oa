package com.x.cms.assemble.control.jaxrs.comment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;

/**
 * 取消评论点赞
 * @author sword
 */
public class ActionPersistUnCommend extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistUnCommend.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String commentId, EffectivePerson effectivePerson ) throws Exception {
		logger.debug(request.getRequestURI());
		ActionResult<Wo> result = new ActionResult<>();
		List<String> ids = docCommendPersistService.delete(commentId, effectivePerson.getDistinguishedName(), DocumentCommend.COMMEND_TYPE_COMMENT);
		Wo wo = new Wo();
		wo.setIds( ids );
		result.setData( wo );
		CacheManager.notify( Document.class );

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
