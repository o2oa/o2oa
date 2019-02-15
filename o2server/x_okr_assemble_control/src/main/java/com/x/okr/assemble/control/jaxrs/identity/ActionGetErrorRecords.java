package com.x.okr.assemble.control.jaxrs.identity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.identity.entity.ErrorIdentityRecord;
import com.x.okr.assemble.control.jaxrs.identity.exception.ExceptionFilterIdentityEmpty;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionInsufficientPermissions;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionOkrSystemAdminCheck;
import com.x.okr.entity.OkrErrorIdentityRecords;

public class ActionGetErrorRecords extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGetErrorRecords.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		OkrErrorIdentityRecords errorIdentityRecords = new OkrErrorIdentityRecords();
		String identity = null;
		String content = null;
		List<Wo> errorRecordsList = null;
		Wi wrapIn = null;
		Boolean check = true;

		if (check) {
			try {
				wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
			} catch (Exception e) {
				wrapIn = new Wi();
			}
		}
		if (check) {
			try {
				if ( !okrUserInfoService.getIsOkrManager( effectivePerson.getDistinguishedName() ) ) {
					check = false;
					Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(),
							ThisApplication.OKRMANAGER);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionOkrSystemAdminCheck(e, effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			identity = wrapIn.getIdentity();
			if (identity == null || identity.isEmpty()) {
				check = false;
				Exception exception = new ExceptionFilterIdentityEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				errorIdentityRecords = okrSystemIdentityQueryService.getErrorIdentityRecords(identity);
				if (errorIdentityRecords != null) {
					content = errorIdentityRecords.getRecordsJson();
				} else {
					content = "{}";
				}
				if (content != null && !"{}".equals(content)) {
					Gson gson = XGsonBuilder.instance();
					errorRecordsList = (List<Wo>) gson.fromJson(content, new TypeToken<List<Wo>>() { }.getType());
				}
				result.setData(errorRecordsList);
			} catch (Exception e) {
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi {
		
		@FieldDescribe( "用于查询身份名称." )
		private String identity  = null;
		
		@FieldDescribe( "用于列表排序的属性." )
		private String sequenceField =  JpaObject.sequence_FIELDNAME;
		
		@FieldDescribe( "用于列表排序的方式." )
		private String order = "DESC";
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}
		public void setRank(Long rank) {
			this.rank = rank;
		}
		public String getSequenceField() {
			return sequenceField;
		}
		public void setSequenceField(String sequenceField) {
			this.sequenceField = sequenceField;
		}
		public String getOrder() {
			return order;
		}
		public void setOrder(String order) {
			this.order = order;
		}
		public String getIdentity() {
			return identity;
		}
		public void setIdentity(String identity) {
			this.identity = identity;
		}
		
	}
	
	public static class Wo {
		
		private String identity = null;
		
		private String recordType = "未知类别";
		
		private List<ErrorIdentityRecord> errorRecords = null;
		
		public String getIdentity() {
			return identity;
		}
		public String getRecordType() {
			return recordType;
		}
		public List<ErrorIdentityRecord> getErrorRecords() {
			return errorRecords;
		}
		public void setIdentity(String identity) {
			this.identity = identity;
		}
		public void setRecordType(String recordType) {
			this.recordType = recordType;
		}
		public void setErrorRecords(List<ErrorIdentityRecord> errorRecords) {
			this.errorRecords = errorRecords;
		}
	}
}