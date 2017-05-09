package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentInfoProcessException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentNotExistsException;
import com.x.cms.assemble.control.jaxrs.documentpermission.element.PermissionInfo;
import com.x.cms.core.entity.Document;

public class ExcutePublish extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcutePublish.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson, WrapInDocument wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		List<PermissionInfo> permissionList = null;
		PermissionInfo permissionInfo = null;
		Document document = null;
		Boolean check = true;
		
		if( check ){
			try {
				document = documentServiceAdv.get( id );
				if ( null == document ) {
					check = false;
					Exception exception = new DocumentNotExistsException( id );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
					throw exception;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new DocumentInfoProcessException( e, "文档信息获取操作时发生异常。Id:" + id + ", Name:" + effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				modifyDocStatus( id, "published", effectivePerson.getName() );
				document.setDocStatus( "published" );
				document.setPublishTime( new Date() );
				result.setData(new WrapOutId( document.getId() ));
			} catch (Exception e) {
				Exception exception = new DocumentInfoProcessException( e, "系统将文档状态修改为发布状态时发生异常。Id:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
				throw exception;
			}			
		}
		
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				logService.log( emc, effectivePerson.getName(), document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "发布" );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		if( check ){
			try{
				if( wrapIn.getPermissionList() == null ){
					permissionList = new ArrayList<>();
					permissionInfo = new PermissionInfo();
					permissionInfo.setPermission( "阅读" );
					permissionInfo.setPermissionObjectCode( "所有人"  );
					permissionInfo.setPermissionObjectName( "所有人" );
					permissionInfo.setPermissionObjectType( "所有人" );
					permissionList.add( permissionInfo );
				}else{
					permissionList = wrapIn.getPermissionList();
				}				
				documentPermissionServiceAdv.refreshDocumentPermission( document, permissionList );
			}catch(Exception e){
				check = false;
				Exception exception = new DocumentInfoProcessException( e, "系统在核对文档访问管理权限信息时发生异常！" );
				result.error( exception );
				logger.error( e , effectivePerson, request, null);
			}
		}
		
		ApplicationCache.notify( Document.class );

		return result;
	}

}