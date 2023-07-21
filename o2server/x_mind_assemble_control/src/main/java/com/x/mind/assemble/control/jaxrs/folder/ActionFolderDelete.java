package com.x.mind.assemble.control.jaxrs.folder;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderDelete;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindFolderQuery;
import com.x.mind.entity.MindFolderInfo;

/**
 * 删除文件夹信息
 * @author O2LEE
 *
 */
public class ActionFolderDelete extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionFolderDelete.class );

	@AuditLog(operation = "删除文件目录")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String folderId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
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
		//查询文件夹下是否仍有脑图信息存在
		if( check ){
			Long count = mindFolderInfoService.countChildWithFolder(folderId);
			if( count > 0 ) {
				check = false;
				Exception exception = new ExceptionFolderDelete( "文件夹下仍有" +count+ "个子文件夹，暂时无法删除文件夹！" );
				result.error(exception);
			}
		}
		//查询文件夹下是否仍有脑图信息存在
		if( check ){
			Long count = mindInfoService.countMindWithFolder(folderId);
			if( count > 0 ) {
				check = false;
				Exception exception = new ExceptionFolderDelete( "文件夹下仍有" +count+ "个脑图文件，暂时无法删除文件夹！" );
				result.error(exception);
			}
		}
		if( check ){
			try {
				mindFolderInfoService.delete(folderId);
				wo.setId( folderId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionFolderDelete( e, "{‘id’:'"+folderId+"'}" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}