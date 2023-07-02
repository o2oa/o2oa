package com.x.cms.assemble.control.jaxrs.comment;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
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
			documentCommentInfo = Wi.copier.copy(wi);
			documentCommentInfo.setId( wi.getId());;
			documentCommentInfo.setCreatorName( effectivePerson.getDistinguishedName() );
			Business business = new Business(null);
			if(StringUtils.isNoneBlank(wi.getCommentUser()) && business.isManager(effectivePerson)){
				String person = business.organization().person().get(wi.getCommentUser());
				if(StringUtils.isNoneBlank(person)){
					documentCommentInfo.setCreatorName( person );
				}
			}
			documentCommentInfo.setAuditorName( "" );
			documentCommentInfo.setCommentAuditStatus( "通过" );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionCommentPersist(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if( check ) {
			try {
				document = documentInfoServiceAdv.get( documentCommentInfo.getDocumentId() );
				if (document == null) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists( documentCommentInfo.getDocumentId() );
					result.error(exception);
				}else {
					documentCommentInfo.setAppId( document.getAppId() );
					documentCommentInfo.setAppName( document.getAppName() );
					documentCommentInfo.setCategoryId( document.getCategoryId() );
					documentCommentInfo.setCategoryName( document.getCategoryName() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCommentPersist(e, "文档信息获取操作时发生异常。ID:" + wi.getDocumentId() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				documentCommentInfo = documentCommentInfoPersistService.save( documentCommentInfo, wi.getContent(),  effectivePerson );

				// 更新缓存
				CacheManager.notify( Document.class );
				CacheManager.notify( DocumentCommentInfo.class );

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

	public static class Wi {

		@FieldDescribe("评论ID")
		private String id = "";

		@FieldDescribe("文档ID")
		private String documentId = "";

		@FieldDescribe("评论标题：如果没有则与主题相同")
		private String title = "";

		@FieldDescribe("上级评论ID")
		private String parentId = "";

		@FieldDescribe("内容")
		private String content = "";

		@FieldDescribe("是否私信评论")
		private Boolean isPrivate = false;

		@FieldDescribe("评论用户(仅管理员可指定)")
		private String commentUser = "";

		public static final WrapCopier<Wi, DocumentCommentInfo> copier = WrapCopierFactory.wi( Wi.class, DocumentCommentInfo.class, null, null );

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDocumentId() {
			return documentId;
		}

		public void setDocumentId(String documentId) {
			this.documentId = documentId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public Boolean getIsPrivate() {
			return isPrivate;
		}

		public void setIsPrivate(Boolean isPrivate) {
			this.isPrivate = isPrivate;
		}

		public String getCommentUser() {
			return commentUser;
		}

		public void setCommentUser(String commentUser) {
			this.commentUser = commentUser;
		}
	}

	public static class Wo extends WoId {
	}

}
