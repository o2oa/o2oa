package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
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
import com.x.cms.assemble.control.jaxrs.documentpermission.element.PermissionInfo;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.Form;

public class ExcutePublishContent extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcutePublishContent.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, WrapInDocument wrapIn, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		List<PermissionInfo> permissionList = null;
		PermissionInfo permissionInfo = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Form form = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
				System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>title is empty!" );
				check = false;
				Exception exception = new DocumentTitleEmptyException();
				result.error( exception );
			}
		}
		
		if( check ){
			if( wrapIn.getCategoryId() == null || wrapIn.getCategoryId().isEmpty() ){
				System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>categoryId is empty!" );
				check = false;
				Exception exception = new DocumentCategoryIdEmptyException();
				result.error( exception );
			}
		}
		
		if( check ){
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to query appInfo by id:" + wrapIn.getAppId() );
			try{
				appInfo = appInfoServiceAdv.get( wrapIn.getAppId() );
				if( appInfo == null ){
					System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>appInfo is not exists!" );
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
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to query category by id:" + wrapIn.getCategoryId() );
			try{
				categoryInfo = categoryInfoServiceAdv.get( wrapIn.getCategoryId() );
				if( categoryInfo == null ){
					System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>categoryInfo is not exists!" );
					check = false;
					Exception exception = new CategoryInfoNotExistsException( wrapIn.getCategoryId() );
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
				System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>> categoryInfo.getFormId() is empty!" );
				check = false;
				Exception exception = new CategoryFormIdEmptyException();
				result.error( exception );
			}
		}
		
		if( check ){
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to query form by formId:" + categoryInfo.getFormId() );
			try {
				form = formServiceAdv.get( categoryInfo.getFormId() );
				if( form == null ){
					System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>edit form is not exists!" );
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
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to query form by formId:" + categoryInfo.getReadFormId() );
			if( categoryInfo.getReadFormId() != null && !categoryInfo.getReadFormId().isEmpty() ){
				try {
					form = formServiceAdv.get( categoryInfo.getReadFormId() );
					if( form == null ){
						System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>read form is not exists!" );
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
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to query creator organization......" );
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business( emc );
				if( wrapIn.getCreatorIdentity() != null){
					wrapIn.setCreatorPerson( business.organization().person().getWithIdentity( wrapIn.getCreatorIdentity() ).getName() );
					wrapIn.setCreatorDepartment( business.organization().department().getWithIdentity( wrapIn.getCreatorIdentity() ).getName() );
					wrapIn.setCreatorCompany( business.organization().company().getWithIdentity( wrapIn.getCreatorIdentity() ).getName() );
				}else{
					if( "xadmin".equalsIgnoreCase( effectivePerson.getName()) ){
						wrapIn.setCreatorIdentity( "xadmin" );
						wrapIn.setCreatorPerson( "xadmin" );
						wrapIn.setCreatorDepartment( "xadmin" );
						wrapIn.setCreatorCompany( "xadmin" );
					}else{
						Exception exception = new PersonHasNoIdentityException( wrapIn.getCreatorIdentity() );
						result.error( exception );
					}
				}
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		if( check ){
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to save document info......" );
			try{
				document = documentServiceAdv.save( wrapIn );
			}catch(Exception e){
				check = false;
				Exception exception = new DocumentInfoProcessException( e, "系统在创建文档信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//文档保存成功，再保存一下文档的数据信息
		if (check) {
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to save document data......" );
			Map<?, ?> data = null;
			String[] paths = wrapIn.getDataPaths();
			if ( wrapIn.getDocData() != null ) {
				JsonElement json = XGsonBuilder.instance().toJsonTree( wrapIn.getDocData(), Map.class);				
				data = documentServiceAdv.getDocumentData( document );
				if (data != null && !data.isEmpty() ) {
					try {
						documentServiceAdv.updateDataItem( paths, json, document );
					} catch (Exception e) {
						check = false;
						Exception exception = new DocumentInfoProcessException(e, "系统在更新文档数据信息时发生异常！");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				} else {
					try {
						documentServiceAdv.saveDataItem( paths, json, document );
					} catch (Exception e) {
						check = false;
						Exception exception = new DocumentInfoProcessException(e, "系统在保存文档数据信息时发生异常！");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		
		if( check ){
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to save document status to 'published'......" );
			try {
				modifyDocStatus( document.getId(), "published", effectivePerson.getName() );
				document.setDocStatus( "published" );
				document.setPublishTime( new Date() );
				result.setData( new WrapOutId( document.getId() ));
			} catch (Exception e) {
				Exception exception = new DocumentInfoProcessException( e, "系统将文档状态修改为发布状态时发生异常。Id:" + document.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
				throw exception;
			}			
		}
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				logService.log( emc, wrapIn.getCreatorIdentity(), document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "发布" );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		if( check ){
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to check document permissions......" );
			if( wrapIn.getPermissionList() == null || wrapIn.getPermissionList().isEmpty() ){
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
		
		ApplicationCache.notify( Document.class );
		if( result.getData() != null ){
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system publish document completed. result.getData().toString():" + result.getData().toString() );
		}else{
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>result.getData() is null!" );
		}
		return result;
	}
	
}