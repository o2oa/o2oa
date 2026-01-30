package com.x.teamwork.assemble.control.jaxrs.extfield;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Task;

public class ActionGetNextUseableExtFieldName extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionGetNextUseableExtFieldName.class);
	
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String correlationId, String fieldType ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Project project = null;
		Task task = null;
		Boolean check = true;
		Wo wo = new Wo();
		
		if( Boolean.TRUE.equals( check ) ){
			if( StringUtils.isEmpty( correlationId )) {
				check = false;
				Exception exception = new CustomExtFieldReleFlagForQueryEmptyException();
				result.error( exception );
			}
		}
		
		if( Boolean.TRUE.equals( check ) ){
			if( StringUtils.isEmpty( fieldType )) {
				fieldType = "TEXT";
			}
		}
		
		/*if( Boolean.TRUE.equals( check ) ){
			try {
				project = projectQueryService.get( correlationId );
				if ( project == null) {
					check = false;
					Exception exception = new ProjectNotExistsException( correlationId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new CustomExtFieldRelePersistException(e, "根据指定flag查询应用项目信息对象时发生异常。flag:" +  correlationId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}*/
		
		if( Boolean.TRUE.equals( check ) ){
			String fieldName = customExtFieldReleQueryService.getNextUseableExtFieldName( correlationId, fieldType );
			if( StringUtils.isNotEmpty( fieldName )) {
				wo.setFieldName(fieldName);
			}else {
				Exception exception = new CustomExtFieldReleQueryException( "当前关联ID无可用扩展属性。" );
				result.error(exception);
			}
		}
		
		result.setData(wo);	
		return result;
	}

	public static class Wo {
 
		private String fieldName = null;

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
	}
}