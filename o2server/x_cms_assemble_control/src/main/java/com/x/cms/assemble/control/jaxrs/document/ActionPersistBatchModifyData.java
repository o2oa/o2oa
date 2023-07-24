package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;

public class ActionPersistBatchModifyData extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistBatchModifyData.class);

	@AuditLog(operation = "批量修改文档数据")
	protected ActionResult<Wo> execute(HttpServletRequest request, JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		Wo wo = null;
		Wi wi = null;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。");
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if (check) {
			try {
				wo = documentPersistService.changeData( wi.getDocIds(), wi.getDataChanges() );

				CacheManager.notify(Document.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据ID批量修改数据时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wi{
		
		@FieldDescribe( "需要批量修改数据的文档ID列表" )
		private List<String> docIds = null;
		
		@FieldDescribe( "需要批量修改数据定义" )
		private List<WiDataChange> dataChanges = null;

		public List<String> getDocIds() {
			return docIds;
		}

		public void setDocIds(List<String> docIds) {
			this.docIds = docIds;
		}

		public List<WiDataChange> getDataChanges() {
			return dataChanges;
		}

		public void setDataChanges(List<WiDataChange> dataChanges) {
			this.dataChanges = dataChanges;
		}
	}
	
	public static class WiDataChange{
		
		@FieldDescribe( "数据路径或者属性名." )
		private String dataPath = null;
		
		@FieldDescribe( "数据类型: String | Integer | Boolean | Date." )
		private String dataType = "String";
		
		@FieldDescribe( "数据值:" )
		private String dataString = null;
		
		@FieldDescribe( "数据值:" )
		private Integer dataInteger = null;
		
		@FieldDescribe( "数据值:" )
		private Integer dataBoolean = null;
		
		@FieldDescribe( "数据值:" )
		private Integer dataDate = null;
	
		public String getDataPath() {
			return dataPath;
		}
	
		public String getDataType() {
			return dataType;
		}
	
		public String getDataString() {
			return dataString;
		}
	
		public Integer getDataInteger() {
			return dataInteger;
		}
	
		public Integer getDataBoolean() {
			return dataBoolean;
		}
	
		public Integer getDataDate() {
			return dataDate;
		}
		
		public void setDataPath(String dataPath) {
			this.dataPath = dataPath;
		}
	
		public void setDataType(String dataType) {
			this.dataType = dataType;
		}
	
		public void setDataString(String dataString) {
			this.dataString = dataString;
		}
	
		public void setDataInteger(Integer dataInteger) {
			this.dataInteger = dataInteger;
		}
	
		public void setDataBoolean(Integer dataBoolean) {
			this.dataBoolean = dataBoolean;
		}
	
		public void setDataDate(Integer dataDate) {
			this.dataDate = dataDate;
		}	
	}

	public static class Wo extends GsonPropertyObject {
		private List<String> errors = null;
		private Integer total = 0;
		private Integer error_count = 0;
		private Integer success_count = 0;
		public List<String> getErrors() {
			return errors;
		}
		public Integer getTotal() {
			return total;
		}
		public Integer getError_count() {
			return error_count;
		}
		public Integer getSuccess_count() {
			return success_count;
		}
		public void setErrors(List<String> errors) {
			this.errors = errors;
		}
		public void setTotal(Integer total) {
			this.total = total;
		}
		public void setError_count(Integer error_count) {
			this.error_count = error_count;
		}
		public void setSuccess_count(Integer success_count) {
			this.success_count = success_count;
		}
		
		public void increaseError_count(Integer count) {
			if( count == null ) {
				count = 1;
			}
			this.total = total + count;
			this.error_count = error_count + count;
		}
		
		public void increaseSuccess_count(Integer count) {
			if( count == null ) {
				count = 1;
			}
			this.total = total + count;
			this.success_count = success_count + count;
		}
		
		public void appendErorrId(String id) {
			if( errors == null ) {
				errors = new ArrayList<>();
			}
			errors.add( id );
		}
	}
}