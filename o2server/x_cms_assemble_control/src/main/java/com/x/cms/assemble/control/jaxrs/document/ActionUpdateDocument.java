package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.assemble.control.wrapin.WiSiteFileInfo;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeCmsWo;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionUpdateWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.SiteTargetWi;
import java.util.List;
import java.util.Map;

/**
 * 更新文档
 *
 */
public class ActionUpdateDocument extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateDocument.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, docId:{}.", effectivePerson::getDistinguishedName, () -> docId);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Document document = documentQueryService.get(docId);
		if (document == null) {
			throw new ExceptionEntityNotExist(docId);
		}
		CategoryInfo categoryInfo = categoryInfoServiceAdv.get(document.getCategoryId());
		AppInfo appInfo = appInfoServiceAdv.get(document.getAppId());
		if (categoryInfo == null) {
			throw new ExceptionCategoryInfoNotExists(document.getCategoryId());
		}
		if (appInfo == null) {
			throw new ExceptionAppInfoNotExists(document.getAppId());
		}
		Business business = new Business(null);
		if (!business.isDocumentEditor(effectivePerson, appInfo, categoryInfo, document)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		this.updateDocument(document, wi, effectivePerson);
		CacheManager.notify(Document.class);
		Wo wo = new Wo();
		wo.setId(docId);
		result.setData(wo);
		return result;
	}

	private void updateDocument(Document document, Wi wi, EffectivePerson effectivePerson) throws Exception {
		String docId = document.getId();
		if(wi.getData() != null && !wi.getData().isJsonNull()){
			ThisApplication.context().applications().putQuery(x_cms_assemble_control.class,
							Applications.joinQueryUri("data", "document", docId), wi.getData())
					.getData(WoId.class);
		}
		if(ListTools.isNotEmpty(wi.getPermissionList())){
			Map<String, Object> map = Map.of("docId", docId, "permissionList", wi.getPermissionList());
			ThisApplication.context().applications().postQuery(x_cms_assemble_control.class,
							Applications.joinQueryUri("docpermission"), map)
					.getData(WoId.class);
		}
		if (ListTools.isNotEmpty(wi.getCorrelationTargetList())) {
			ActionUpdateWi req = new ActionUpdateWi();
			req.setSiteTargetList(wi.getCorrelationTargetList());
			req.setPerson(effectivePerson.getDistinguishedName());
			ThisApplication.context().applications()
					.postQuery(effectivePerson.getDebugger(), x_correlation_service_processing.class,
							Applications.joinQueryUri("correlation", "update", "type", "cms", "document", docId), req, docId)
					.getData(ActionCreateTypeCmsWo.class);
		}
		if(ListTools.isNotEmpty(wi.getSiteFileInfoList())){
			Map<String, Object> map = Map.of("person", effectivePerson.getDistinguishedName(), "siteFileInfoList", wi.getSiteFileInfoList());
			ThisApplication.context().applications().postQuery(x_cms_assemble_control.class,
							Applications.joinQueryUri("fileinfo", "replace", "to", "doc", docId), map)
					.getData(WrapBoolean.class);
		}
	}

	public static class Wi {
		@FieldDescribe("文档业务数据，覆盖业务数据，为空则不更新，数据格式为json对象，如：{\"subject\":\"修改标题\"}.")
		private JsonElement data;

		@FieldDescribe("文档权限列表，替换权限，为空则不操作.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "PermissionInfo", fieldValue = "{'permission':'权限类别：读者|作者|管理','permissionObjectType':'使用者类别：所有人|组织|人员|群组','permissionObjectCode':'使用者编码：所有人|组织编码|人员UID|群组编码','permissionObjectName':'使用者名称：所有人|组织名称|人员名称|群组名称'}")
		private List<PermissionInfo> permissionList;

		@FieldDescribe("关联文档列表，替换文档指定site的关联文档数据，为空则不操作.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "TargetWi", fieldValue = "{'site':'关联内容框标识','targetList':[{'type':'关联目标类型(cms或processPlatform)','bundle':'关联目标标识','view':'来源视图'}]}")
		private List<SiteTargetWi> correlationTargetList;

		@FieldDescribe("附件列表，替换文档指定site的附件列表，先删再增，为空则不操作.")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiSiteFileInfo", fieldValue = "{'site':'附件框分类','fileInfoList':[{'id':'附件id','name':'附件名称'}],'type':'附件来源(cms或processPlatform)'}")
		private List<WiSiteFileInfo> siteFileInfoList;

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

		public List<PermissionInfo> getPermissionList() {
			return permissionList;
		}

		public void setPermissionList(
				List<PermissionInfo> permissionList) {
			this.permissionList = permissionList;
		}

		public List<SiteTargetWi> getCorrelationTargetList() {
			return correlationTargetList;
		}

		public void setCorrelationTargetList(
				List<SiteTargetWi> correlationTargetList) {
			this.correlationTargetList = correlationTargetList;
		}

		public List<WiSiteFileInfo> getSiteFileInfoList() {
			return siteFileInfoList;
		}

		public void setSiteFileInfoList(
				List<WiSiteFileInfo> siteFileInfoList) {
			this.siteFileInfoList = siteFileInfoList;
		}
	}

	public static class Wo extends WoId {

	}
}
