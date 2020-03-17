package com.x.okr.assemble.control.jaxrs.identity;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.identity.exception.ExceptionIdentityCheck;
import com.x.okr.assemble.control.jaxrs.identity.exception.ExceptionNewIdentityEmpty;
import com.x.okr.assemble.control.jaxrs.identity.exception.ExceptionNewIdentityNotExists;
import com.x.okr.assemble.control.jaxrs.identity.exception.ExceptionOldIdentityEmpty;
import com.x.okr.assemble.control.jaxrs.identity.exception.ExceptionRecordIdEmpty;
import com.x.okr.assemble.control.jaxrs.identity.exception.ExceptionRecordTypeEmpty;
import com.x.okr.assemble.control.jaxrs.identity.exception.ExceptionTableNameEmpty;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionWrapInConvert;

public class ActionChangeIdentity extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionChangeIdentity.class );
	
	protected ActionResult<WrapOutString> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<WrapOutString> result = new ActionResult<>();
		Wi wrapIn = null;
		Boolean identityExists = false;
		Boolean check = true;

		if (check) {
			try {
				wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWrapInConvert(e, jsonElement);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (wrapIn.getOldIdentity() == null || wrapIn.getOldIdentity().isEmpty()) {
				check = false;
				Exception exception = new ExceptionOldIdentityEmpty();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getNewIdentity() == null || wrapIn.getNewIdentity().isEmpty()) {
				check = false;
				Exception exception = new ExceptionNewIdentityEmpty();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getRecordType() == null || wrapIn.getRecordType().isEmpty()) {
				check = false;
				Exception exception = new ExceptionRecordTypeEmpty();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getTableName() == null || wrapIn.getTableName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionTableNameEmpty();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getRecordId() == null || wrapIn.getRecordId().isEmpty()) {
				check = false;
				Exception exception = new ExceptionRecordIdEmpty();
				result.error(exception);
			}
		}

		if (check) {
			try {
				identityExists = userManagerService.isIdentityExsits( wrapIn.getNewIdentity() );
				if (!identityExists) {
					check = false;
					Exception exception = new ExceptionNewIdentityNotExists(wrapIn.getNewIdentity());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionIdentityCheck(e, wrapIn.getNewIdentity());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				WrapOutString wrapOutString = new WrapOutString();
				okrSystemIdentityOperatorService.changeUserIdentity(wrapIn.getOldIdentity(), wrapIn.getNewIdentity(),
						wrapIn.getRecordType(), wrapIn.getTableName(), wrapIn.getRecordId());
				okrSystemIdentityOperatorService.checkAllAbnormalIdentityInSystem(wrapIn.getOldIdentity(), null);
				wrapOutString.setValue("数据身份信息变更完成");
				result.setData(wrapOutString);
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error(e);
				logger.warn("system check identity got an exception.");
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi {
		
		@FieldDescribe( "需要变更的无效的身份名称." )
		private String oldIdentity = "";
		
		@FieldDescribe( "用户更新的新的身份名称." )
		private String newIdentity = "";
		
		@FieldDescribe( "需要进行变更的数据类型：工作信息|工作汇报|待办已办|交流动态|系统配置|数据统计." )
		private String recordType = "";
		
		@FieldDescribe( "需要进行变更的数据所涉及的数据表中具体的数据的ID值" )
		private String recordId = "";
		
		@FieldDescribe( "需要进行变更的数据所涉及的数据表." )
		private String tableName = "";

		public String getOldIdentity() {
			return oldIdentity;
		}

		public String getNewIdentity() {
			return newIdentity;
		}

		public void setOldIdentity(String oldIdentity) {
			this.oldIdentity = oldIdentity;
		}

		public void setNewIdentity(String newIdentity) {
			this.newIdentity = newIdentity;
		}

		public String getRecordType() {
			return recordType;
		}

		public String getTableName() {
			return tableName;
		}

		public void setRecordType(String recordType) {
			this.recordType = recordType;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getRecordId() {
			return recordId;
		}

		public void setRecordId(String recordId) {
			this.recordId = recordId;
		}	

	}
}