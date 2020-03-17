package com.x.cms.assemble.control.jaxrs.comment;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommentInfo;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		DocumentCommentInfo documentCommentInfo = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new ExceptionCommentIdForQueryEmpty();
			result.error( exception );
		}

		if (check) {
			try {
				documentCommentInfo = documentCommentInfoQueryService.get(flag);
				if ( documentCommentInfo == null) {
					check = false;
					Exception exception = new ExceptionCommentNotExists(flag);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCommentQuery(e, "根据指定flag查询评论信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				documentCommentInfoPersistService.delete(flag, effectivePerson );				
				// 更新缓存
				ApplicationCache.notify( Document.class );
				ApplicationCache.notify( DocumentCommentInfo.class );
				
				Wo wo = new Wo();
				wo.setId( documentCommentInfo.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCommentPersist(e, "根据指定flag删除评论信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {
	}
}