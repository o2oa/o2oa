package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

/**
 * @author sword
 */
public class ActionPersistPublishAndNotify extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistPublishAndNotify.class);

	@AuditLog(operation = "发布一个文档")
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<FileInfo> cloudPictures = null;
		Document document = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if (check) {
			try {
				document = documentQueryService.get(id);
				if (null == document) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(id);
					result.error(exception);
					throw exception;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"文档信息获取操作时发生异常。Id:" + id + ", Name:" + effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				modifyDocStatus( id, Document.DOC_STATUS_PUBLISH, effectivePerson.getDistinguishedName() );
				document.setDocStatus(Document.DOC_STATUS_PUBLISH);
				document.setPublishTime(new Date());

				document = documentPersistService.refreshDocInfoData( document );

				Wo wo = new Wo();
				wo.setId( document.getId() );
				result.setData( wo );

				//检查是否需要删除热点图片
				try {
					ThisApplication.queueDocumentUpdate.send( document );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}

			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统将文档状态修改为发布状态时发生异常。Id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
				throw exception;
			}
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				logService.log(emc, effectivePerson.getDistinguishedName(),
						document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(),
						document.getCategoryId(), document.getId(), "", "DOCUMENT", "发布");
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		//将读者以及作者信息持久化到数据库中
		if( !wi.getSkipPermission() ) {
			try {
				documentPersistService.refreshDocumentPermission( id, wi.getReaderList(), wi.getAuthorList() );
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在核对文档访问管理权限信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		CacheManager.notify( Document.class );

		return result;
	}

	public static class Wi {

		public static WrapCopier<Wi, Document> copier = WrapCopierFactory.wi( Wi.class, Document.class, null, JpaObject.FieldsUnmodify );

		@FieldDescribe( "文档读者." )
		private List<PermissionInfo> readerList = null;

		@FieldDescribe( "文档编辑者." )
		private List<PermissionInfo> authorList = null;

		@FieldDescribe( "不修改权限（跳过权限设置，保留原来的设置）." )
		private Boolean skipPermission  = false;

		public Boolean getSkipPermission() {
			return skipPermission;
		}

		public void setSkipPermission(Boolean skipPermission) {
			this.skipPermission = skipPermission;
		}

		public List<PermissionInfo> getReaderList() {
			return readerList;
		}

		public List<PermissionInfo> getAuthorList() {
			return authorList;
		}

		public void setReaderList(List<PermissionInfo> readerList) {
			this.readerList = readerList;
		}

		public void setAuthorList(List<PermissionInfo> authorList) {
			this.authorList = authorList;
		}

	}

	public static class Wo extends WoId {

	}
}
