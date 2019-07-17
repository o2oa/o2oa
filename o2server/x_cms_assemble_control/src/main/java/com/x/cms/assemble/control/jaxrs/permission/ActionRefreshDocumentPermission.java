package com.x.cms.assemble.control.jaxrs.permission;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.core.entity.Document;

public class ActionRefreshDocumentPermission extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRefreshDocumentPermission.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Document document = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if ( StringUtils.isEmpty(wi.getDocId())) {
				check = false;
				Exception exception = new ExceptionServiceLogic("文档ID为空，无法为文档添加权限。");
				result.error(exception);
			}
		}

		if (check) {
			if ( ListTools.isEmpty(wi.getPermissionList())) {
				check = false;
				Exception exception = new ExceptionServiceLogic("文档权限为空，该文档将没有任何用户可以访问。ID：" + wi.getDocId());
				result.error(exception);
			}
		}

		if (check) {
			try {
				document = documentQueryService.get(wi.getDocId());
				if (document == null) {
					check = false;
					Exception exception = new ExceptionServiceLogic("文档不存在。ID：" + wi.getDocId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionServiceLogic(e, "系统在根据文档ID查询文档信息时发生异常。ID：" + wi.getDocId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				documentPersistService.refreshDocumentPermission(document.getId(), wi.getPermissionList());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionServiceLogic(e, "系统在为文档设置用户访问权限过程中发生异常。ID：" + wi.getDocId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}

		}
		return result;
	}

	public static class Wi {

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		@FieldDescribe("文档ID.")
		private String docId = null;

		@FieldDescribe("文档权限列表：List<PermissionInfo>")
		private List<PermissionInfo> permissionList = null;

		public String getDocId() {
			return docId;
		}

		public List<PermissionInfo> getPermissionList() {
			return permissionList;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}

		public void setPermissionList(List<PermissionInfo> permissionList) {
			this.permissionList = permissionList;
		}
	}

	public static class Wo extends WoId {

	}
}