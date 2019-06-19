package com.x.cms.assemble.control.jaxrs.comment;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommentInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		DocumentCommentInfo documentCommentInfo = null;
		Document document = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionCommentPersist(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if( check ) {
			try {
				document = documentInfoServiceAdv.get( wi.getId() );
				if (document == null) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists( wi.getId() );
					result.error(exception);
				}else {
					wi.setAppId( document.getAppId() );
					wi.setAppName( document.getAppName() );
					wi.setCategoryId( document.getCategoryId() );
					wi.setCategoryName( document.getCategoryName() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCommentPersist(e, "文档信息获取操作时发生异常。ID:" + wi.getId() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				documentCommentInfo = documentCommentInfoPersistService.save( wi, effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( Document.class );
				ApplicationCache.notify( DocumentCommentInfo.class );
				
				Wo wo = new Wo();
				wo.setId( documentCommentInfo.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCommentPersist(e, "评论信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi extends DocumentCommentInfo {
		
		private static final long serialVersionUID = -6314932919066148113L;
		
		public static WrapCopier<Wi, DocumentCommentInfo> copier = WrapCopierFactory.wi( Wi.class, DocumentCommentInfo.class, null, null );		
	}

	public static class Wo extends WoId {
	}
	
}