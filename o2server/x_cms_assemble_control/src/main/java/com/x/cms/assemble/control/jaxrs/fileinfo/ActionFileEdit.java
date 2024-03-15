package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

/**
 * 对附件的访问权限进行调整
 * 
 * @author O2LEE
 *
 */
class ActionFileEdit extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionFileEdit.class);

	@AuditLog(operation = "编辑附件")
	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id, String docId,
			JsonElement jsonElement) {
		logger.debug("receive id:{}, jsonElement:{}.", id, jsonElement);
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		Wi wi = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		FileInfo file = null;
		Document doc = null;

		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionFileInfoProcess(e, "系统在将JSON信息转换为对象时发生异常。");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				doc = documentQueryService.get(docId);
				if (null == doc) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(docId);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFileInfoProcess(e, "文档信息获取操作时发生异常。Id:" + docId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				categoryInfo = categoryInfoServiceAdv.get(doc.getCategoryId());
				if (categoryInfo == null) {
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists(doc.getCategoryId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFileInfoProcess(e, "系统在根据ID查询分类信息时发生异常！ID：" + doc.getCategoryId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				appInfo = appInfoServiceAdv.get(categoryInfo.getAppId());
				if (appInfo == null) {
					check = false;
					Exception exception = new ExceptionAppInfoNotExists(categoryInfo.getAppId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFileInfoProcess(e,
						"系统在根据ID查询应用栏目信息时发生异常！ID：" + categoryInfo.getAppId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				if (!documentQueryService.getFileInfoManagerAssess(effectivePerson, doc, categoryInfo, appInfo)) {
					check = false;
					Exception exception = new ExceptionDocumentAccessDenied(effectivePerson.getDistinguishedName(),
							doc.getTitle(), doc.getId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFileInfoProcess(e, "系统在文档附件操作权限时发生异常！ID：" + doc.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				file = fileInfoServiceAdv.updateAttachmentInfo(id, Wi.copier.copy(wi));
				Wo wo = new Wo();
				wo.setId(file.getId());
				result.setData(wo);

				CacheManager.notify(FileInfo.class);
				CacheManager.notify(Document.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFileInfoProcess(e, "系统在更新文档附件信息时发生异常！ID：" + wi.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends FileInfo {

		private static final long serialVersionUID = 4243967432624425952L;
		static WrapCopier<Wi, FileInfo> copier = WrapCopierFactory.wi(Wi.class, FileInfo.class,
				Arrays.asList(FileInfo.readIdentityList_FIELDNAME, FileInfo.readUnitList_FIELDNAME,
						FileInfo.editIdentityList_FIELDNAME, FileInfo.editUnitList_FIELDNAME,
						FileInfo.controllerIdentityList_FIELDNAME, FileInfo.controllerUnitList_FIELDNAME,
						FileInfo.OBJECTSECURITYCLEARANCE_FIELDNAME),
				null);

	}

	public static class Wo extends WoId {

	}
}
