package com.x.teamwork.assemble.control.jaxrs.extfield;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Project;

public class ActionGetNextUseableExtFieldName extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionGetNextUseableExtFieldName.class);
	
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String projectId, String fieldType ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Project project = null;
		Boolean check = true;
		Wo wo = new Wo();
		
		if (check) {
			if( StringUtils.isEmpty( projectId )) {
				check = false;
				Exception exception = new ProjectFlagForQueryEmptyException();
				result.error( exception );
			}
		}
		
		if (check) {
			if( StringUtils.isEmpty( fieldType )) {
				fieldType = "TEXT";
			}
		}
		
		if (check) {
			try {
				project = projectQueryService.get( projectId );
				if ( project == null) {
					check = false;
					Exception exception = new ProjectNotExistsException( projectId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectExtFieldRelePersistException(e, "根据指定flag查询应用项目信息对象时发生异常。flag:" +  projectId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			String fieldName = projectExtFieldReleQueryService.getNextUseableExtFieldName( projectId, fieldType );
			if( StringUtils.isNotEmpty( fieldName )) {
				wo.setFieldName(fieldName);
			}else {
				Exception exception = new ProjectExtFieldReleQueryException( "当前项目无可用扩展属性。" );
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