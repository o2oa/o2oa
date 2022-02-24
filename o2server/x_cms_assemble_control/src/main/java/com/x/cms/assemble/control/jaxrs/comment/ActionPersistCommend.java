package com.x.cms.assemble.control.jaxrs.comment;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;
import com.x.cms.core.entity.DocumentCommentInfo;

/**
 * 评论点赞
 * @author sword
 */
public class ActionPersistCommend extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistCommend.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		logger.debug(request.getRequestURI());
		ActionResult<Wo> result = new ActionResult<>();
		DocumentCommentInfo comment;
		Document document;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			comment = emc.fetch(id, DocumentCommentInfo.class);
			if (comment == null) {
				throw new ExceptionEntityNotExist(id, DocumentCommentInfo.class);
			}
			document = emc.fetch(comment.getDocumentId(), Document.class);
			if (document == null) {
				throw new ExceptionEntityNotExist(comment.getDocumentId(), Document.class);
			}
		}

		DocumentCommend commentCommend = docCommendPersistService.create( effectivePerson.getDistinguishedName(), document, comment);
		Wo wo = new Wo();
		wo.setId(commentCommend.getId() );
		result.setData( wo );

		CacheManager.notify( Document.class );

		return result;
	}

	public static class Wo extends WoId {

	}
}
