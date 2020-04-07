package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentDelete;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentNotExists;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionAttachmentQueryById;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionCenterWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionInsufficientPermissions;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionUserNoLogin;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ActionDeleteCenterAttachment extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDeleteCenterAttachment.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		StorageMapping mapping = null;
		boolean hasDeletePermission = false;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new ExceptionAttachmentIdEmpty();
			result.error( exception );
		}
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrAttachmentFileInfo = emc.find( id, OkrAttachmentFileInfo.class );
				if (null == okrAttachmentFileInfo) {
					check = false;
					Exception exception = new ExceptionAttachmentNotExists( id );
					result.error( exception );
				}
			}catch(Exception e){
				check = false;
				Exception exception = new ExceptionAttachmentQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrCenterWorkInfo = emc.find( okrAttachmentFileInfo.getKey(), OkrCenterWorkInfo.class );
				if ( null == okrCenterWorkInfo ) {
					hasDeletePermission = true;
					logger.warn( "okrCenterWorkInfo{id:" + okrAttachmentFileInfo.getKey() + "} is not exists, anyone can delete the attachments." );
				}else{
					//根据工作信息查询工作信息的干系人信息，判断是否有权限删除附件信息。
					//判断是否有权限删除附件
					if( !okrCenterWorkInfo.getDeployerName().equalsIgnoreCase( okrUserCache.getLoginUserName())){
						hasDeletePermission = false;
					}else{
						hasDeletePermission = true;
					}
				}
			}catch(Exception e){
				check = false;
				Exception exception = new ExceptionCenterWorkQueryById( e, okrAttachmentFileInfo.getKey() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ){
			if( hasDeletePermission ){
				if( okrAttachmentFileInfo != null ){
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						mapping = ThisApplication.context().storageMappings().get(OkrAttachmentFileInfo.class, okrAttachmentFileInfo.getStorage());
						okrAttachmentFileInfo.deleteContent( mapping );
						okrAttachmentFileInfo = emc.find( id, OkrAttachmentFileInfo.class );
						okrCenterWorkInfo = emc.find( okrAttachmentFileInfo.getKey(), OkrCenterWorkInfo.class );
						emc.beginTransaction( OkrAttachmentFileInfo.class );
						emc.beginTransaction( OkrCenterWorkInfo.class);
						if( okrCenterWorkInfo != null && okrCenterWorkInfo.getAttachmentList() != null ){
							okrCenterWorkInfo.getAttachmentList().remove( okrAttachmentFileInfo.getId() );
							emc.check( okrCenterWorkInfo, CheckPersistType.all );
						}
						emc.remove( okrAttachmentFileInfo, CheckRemoveType.all );
						emc.commit();

						Wo wo = new Wo();
						wo.setId( id );
						result.setData( wo );
						
					}catch(Exception e){
						check = false;
						Exception exception = new ExceptionAttachmentDelete( e, okrAttachmentFileInfo.getId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}else{
				Exception exception = new ExceptionInsufficientPermissions( effectivePerson.getDistinguishedName(), okrAttachmentFileInfo.getId() );
				result.error( exception );
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {

	}
}