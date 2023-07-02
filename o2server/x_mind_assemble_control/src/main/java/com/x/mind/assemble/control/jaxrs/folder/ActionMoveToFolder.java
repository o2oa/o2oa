package com.x.mind.assemble.control.jaxrs.folder;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderMoveToFolder;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderWrapInConvert;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindFolderQuery;
import com.x.mind.assemble.control.jaxrs.mind.BaseAction;
import com.x.mind.entity.MindFolderInfo;

/**
 * 移动一组脑图和文件夹到指定的文件夹
 * @author O2LEE
 *
 */
public class ActionMoveToFolder extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMoveToFolder.class );

	@AuditLog(operation = "移动文件目录")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String folderId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Wo wo = new Wo();
		MindFolderInfo folderInfo = null;
		Boolean check = true;
		
		if( check ){
			try {
				folderInfo = mindFolderInfoService.getWithId( folderId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindFolderQuery( e, "系统在根据ID查询所有的脑图文件夹信息时发生异常。" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( folderInfo == null ) {
				check = false;
				Exception exception = new ExceptionFolderNotExists( folderId );
				result.error(exception);
			}
		}
		
		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFolderWrapInConvert(e, jsonElement == null?"None":XGsonBuilder.instance().toJson(jsonElement));
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				//移动指定的脑图文件夹
				mindFolderInfoService.moveToFolder( wi.getMindIds(), wi.getFolderIds(), folderId );
				wo.setValue( true );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionFolderMoveToFolder(e, jsonElement == null?"None":XGsonBuilder.instance().toJson(jsonElement));
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}
	
	public static class Wi {
		
		@FieldDescribe( "需要移动的脑图列表" )
		private List<String> mindIds = null;
		
		@FieldDescribe( "需要移动的文件夹列表" )
		private List<String> folderIds = null;

		public List<String> getMindIds() {
			return mindIds;
		}

		public List<String> getFolderIds() {
			return folderIds;
		}

		public void setMindIds(List<String> mindIds) {
			this.mindIds = mindIds;
		}

		public void setFolderIds(List<String> folderIds) {
			this.folderIds = folderIds;
		}		
	}

	public static class Wo extends WrapBoolean {

	}
}