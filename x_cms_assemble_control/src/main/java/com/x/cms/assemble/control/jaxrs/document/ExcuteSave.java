package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.document.exception.AppInfoNotExistsException;
import com.x.cms.assemble.control.jaxrs.document.exception.CategoryFormIdEmptyException;
import com.x.cms.assemble.control.jaxrs.document.exception.CategoryInfoNotExistsException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentCategoryIdEmptyException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentInfoProcessException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentTitleEmptyException;
import com.x.cms.assemble.control.jaxrs.document.exception.FormForEditNotExistsException;
import com.x.cms.assemble.control.jaxrs.document.exception.FormForReadNotExistsException;
import com.x.cms.assemble.control.jaxrs.document.exception.PersonHasNoIdentityException;
import com.x.cms.assemble.control.jaxrs.document.exception.PersonIdentityInvalidException;
import com.x.cms.assemble.control.jaxrs.document.exception.PersonIdentityQueryException;
import com.x.cms.assemble.control.jaxrs.documentpermission.element.PermissionInfo;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.Form;
import com.x.organization.core.express.wrap.WrapIdentity;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, WrapInDocument wrapIn, EffectivePerson effectivePerson) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		List<WrapIdentity> identities = null;
		List<PermissionInfo> permissionList = null;
		PermissionInfo permissionInfo = null;
		WrapIdentity wrapIdentity = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Form form = null;
		Boolean check = true;
		String identity = wrapIn.getIdentity();
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
				Business business = new Business(emc);
				if( !"xadmin".equalsIgnoreCase( effectivePerson.getName()) ){
					//先查询用户所有的身份，再根据身份查询用户的部门信息
					identities = business.organization().identity().listWithPerson( effectivePerson.getName() );
					if ( identities.size() == 0 ) {//该员工目前没有分配身份
						check = false;
						Exception exception = new PersonHasNoIdentityException( effectivePerson.getName() );
						result.error( exception );
					} else if (identities.size() == 1) {
						wrapIdentity = identities.get(0);
					} else {
						wrapIdentity = this.findIdentity( identities, identity );
						if ( null == wrapIdentity ) {
							check = false;
							Exception exception = new PersonIdentityInvalidException( identity );
							result.error( exception );
						}
					}
				}			
			} catch ( Exception e ) {
				check = false;
				Exception exception = new PersonIdentityQueryException( e, identity );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
				check = false;
				Exception exception = new DocumentTitleEmptyException();
				result.error( exception );
			}
		}
		
		if( check ){
			if( wrapIn.getCategoryId() == null || wrapIn.getCategoryId().isEmpty() ){
				check = false;
				Exception exception = new DocumentCategoryIdEmptyException();
				result.error( exception );
			}
		}
		
		if( check ){
			try{
				appInfo = appInfoServiceAdv.get( wrapIn.getAppId() );
				if( appInfo == null ){
					check = false;
					Exception exception = new AppInfoNotExistsException( wrapIn.getAppId() );
					result.error( exception );
				}
			}catch(Exception e){
				check = false;
				Exception exception = new DocumentInfoProcessException( e, "系统在根据ID查询应用栏目信息时发生异常！ID：" + wrapIn.getAppId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				categoryInfo = categoryInfoServiceAdv.get( wrapIn.getCategoryId() );
				if( categoryInfo == null ){
					check = false;
					Exception exception = new CategoryInfoNotExistsException(  wrapIn.getCategoryId() );
					result.error( exception );
				}
			}catch(Exception e){
				check = false;
				Exception exception = new DocumentInfoProcessException( e, "系统在根据ID查询分类信息时发生异常！ID：" + wrapIn.getCategoryId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//查询分类设置的编辑表单
		if( check ){
			if( categoryInfo.getFormId() == null || categoryInfo.getFormId().isEmpty() ){
				check = false;
				Exception exception = new CategoryFormIdEmptyException();
				result.error( exception );
			}
		}
		
		if( check ){			
			try {
				form = formServiceAdv.get( categoryInfo.getFormId() );
				if( form == null ){
					check = false;
					Exception exception = new FormForEditNotExistsException( categoryInfo.getFormId() );
					result.error( exception );
				}else{
					wrapIn.setForm( form.getId() );
					wrapIn.setFormName( form.getName() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new DocumentInfoProcessException( e, "系统在根据ID查询编辑表单时发生异常！ID：" + categoryInfo.getFormId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( categoryInfo.getReadFormId() != null && !categoryInfo.getReadFormId().isEmpty() ){
				try {
					form = formServiceAdv.get( categoryInfo.getReadFormId() );
					if( form == null ){
						check = false;
						Exception exception = new FormForReadNotExistsException( categoryInfo.getReadFormId() );
						result.error( exception );
					}else{
						wrapIn.setReadFormId( form.getId() );
						wrapIn.setReadFormName( form.getName() );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "系统在根据ID查询阅读表单时发生异常！ID：" + categoryInfo.getReadFormId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			wrapIn.setAppId( categoryInfo.getAppId() );
			wrapIn.setAppName( appInfo.getAppName() );
			wrapIn.setCategoryName( categoryInfo.getCategoryName() );
			wrapIn.setCategoryId( categoryInfo.getId() );
			wrapIn.setCategoryAlias( categoryInfo.getCategoryAlias() );
			if( wrapIn.getPictureList() != null && !wrapIn.getPictureList().isEmpty() ){
				wrapIn.setHasIndexPic( true );
			}
		}
		
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business( emc );
				if( wrapIdentity != null){
					wrapIn.setCreatorIdentity( business.organization().identity().getWithName( wrapIdentity.getName() ).getName() );
					wrapIn.setCreatorPerson( business.organization().person().getWithIdentity( wrapIdentity.getName() ).getName() );
					wrapIn.setCreatorDepartment( business.organization().department().getWithIdentity( wrapIdentity.getName() ).getName() );
					wrapIn.setCreatorCompany( business.organization().company().getWithIdentity( wrapIdentity.getName() ).getName() );
				}else{
					if( "xadmin".equalsIgnoreCase( effectivePerson.getName()) ){
						wrapIn.setCreatorIdentity( "xadmin" );
						wrapIn.setCreatorPerson( "xadmin" );
						wrapIn.setCreatorDepartment( "xadmin" );
						wrapIn.setCreatorCompany( "xadmin" );
					}else{
						Exception exception = new PersonHasNoIdentityException( effectivePerson.getName() );
						result.error( exception );
					}
				}
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		if( check ){
			try{
				document = documentServiceAdv.save( wrapIn );
				ApplicationCache.notify( Document.class );
				result.setData( new WrapOutId( document.getId() ) );
			}catch(Exception e){
				check = false;
				Exception exception = new DocumentInfoProcessException( e, "系统在创建文档信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//文档保存成功，再保存一下文档的数据信息
		if( check ){
			Map<?, ?> data = null;
			String[] paths = wrapIn.getDataPaths();
			if( wrapIn.getDocData() != null ){
				data = documentServiceAdv.getDocumentData( document );
				JsonElement json = XGsonBuilder.instance().toJsonTree( wrapIn.getDocData(), Map.class);	
				if( data != null && !data.isEmpty() ){
					try{
						documentServiceAdv.updateDataItem( paths, json, document );
					}catch(Exception e){
						check = false;
						Exception exception = new DocumentInfoProcessException( e, "系统在更新文档数据信息时发生异常！" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					try{
						documentServiceAdv.saveDataItem( paths, json, document );
					}catch(Exception e){
						check = false;
						Exception exception = new DocumentInfoProcessException( e, "系统在保存文档数据信息时发生异常！" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		
		//如果是已经发布的文档，需要限时再计算一次文档权限
		if( check ){			
			if( "published".equals( document.getDocStatus() ) ){
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
				
				try{
					documentPermissionServiceAdv.refreshDocumentPermission( document, permissionList );
				}catch(Exception e){
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "系统在核对文档访问管理权限信息时发生异常！" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		return result;
	}
	
}