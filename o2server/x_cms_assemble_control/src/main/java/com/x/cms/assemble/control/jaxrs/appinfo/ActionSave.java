package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AppInfo old_appInfo = null;
		AppInfo appInfo = null;
		List<String> ids = null;
		String identityName = null;
		String unitName = null;
		String topUnitName = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			identityName = wi.getIdentity();
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionAppInfoProcess(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			if ( StringUtils.isEmpty( wi.getAppName() ) ) {
				check = false;
				Exception exception = new ExceptionAppInfoNameEmpty();
				result.error(exception);
			}
		}
		
		if (check) {//栏目不允许重名
			try {
				ids = appInfoServiceAdv.listByAppName( wi.getAppName());
				if ( ListTools.isNotEmpty( ids ) ) {
					for( String _id : ids ) {
						if( !_id.equalsIgnoreCase( wi.getId() )) {
							check = false;
							Exception exception = new ExceptionAppInfoNameAlreadyExists( wi.getAppName());
							result.error(exception);
						}
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "系统根据应用栏目名称查询应用栏目信息对象时发生异常。AppName:" + wi.getAppName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if ( !"xadmin".equalsIgnoreCase( effectivePerson.getDistinguishedName()) ) {
				try {
					identityName = userManagerService.getPersonIdentity( effectivePerson.getDistinguishedName(), identityName );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAppInfoProcess( e, "系统获取人员身份名称时发生异常，指定身份：" + identityName );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}else {
				identityName = "xadmin";
				unitName = "xadmin";
				topUnitName = "xadmin";
			}
		}

		if (check && !"xadmin".equals(identityName)) {
			try {
				unitName = userManagerService.getUnitNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "系统在根据用户身份信息查询所属组织名称时发生异常。Identity:" + identityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && !"xadmin".equals(identityName)) {
			try {
				topUnitName = userManagerService.getTopUnitNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "系统在根据用户身份信息查询所属顶层组织名称时发生异常。Identity:" + identityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}		
		if (check) {
			if( StringUtils.isEmpty( wi.getDocumentType() ) ) {
				wi.setDocumentType( "信息" );
			}else {
				if( !"信息".equals(wi.getDocumentType()) && !"数据".equals( wi.getDocumentType() )) {
					wi.setDocumentType( "信息" );
				}
			}
		}
		
		if (check) {//栏目不允许重名
			if( StringUtils.isEmpty( wi.getId() )) {
				wi.setId( AppInfo.createId() );
			}
			try {
				old_appInfo = appInfoServiceAdv.get( wi.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "系统根据应用栏目ID查询应用栏目信息对象时发生异常。ID:" + wi.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			wi.setCreatorIdentity(identityName);
			wi.setCreatorPerson(effectivePerson.getDistinguishedName());
			wi.setCreatorUnitName( unitName );
			wi.setCreatorTopUnitName( topUnitName );

			if( StringUtils.equals( "信息", wi.getDocumentType() ) && wi.getSendNotify() == null ) {
				wi.setSendNotify( true );
			}
			
			try {
				appInfo = appInfoServiceAdv.save( wi, effectivePerson );
				Wo wo = new Wo();
				wo.setId( appInfo.getId() );
				result.setData( wo );
				
				if( old_appInfo != null ) {
					if( !old_appInfo.getAppName().equalsIgnoreCase( appInfo.getAppName() ) || 
						 !old_appInfo.getAppAlias().equalsIgnoreCase( appInfo.getAppAlias() )	) {
						//修改了栏目名称或者别名，增加删除栏目批量操作（对分类和文档）的信息
						new CmsBatchOperationPersistService().addOperation( 
								CmsBatchOperationProcessService.OPT_OBJ_APPINFO, 
								CmsBatchOperationProcessService.OPT_TYPE_UPDATENAME,  appInfo.getId(), old_appInfo.getAppName(), "更新栏目名称：ID=" + appInfo.getId() );
					}
					if(  permissionQueryService.hasDiffrentViewPermissionInAppInfo( old_appInfo, appInfo )) {
							//修改了栏目名称或者别名，增加删除栏目批量操作（对分类和文档）的信息
							new CmsBatchOperationPersistService().addOperation( 
									CmsBatchOperationProcessService.OPT_OBJ_APPINFO, 
									CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  appInfo.getId(), appInfo.getAppName(), "变更栏目可见权限：ID=" + appInfo.getId() );
					}
					new LogService().log(null, effectivePerson.getDistinguishedName(), appInfo.getAppName(), appInfo.getId(), "", "", "", "APPINFO", "更新");
				}else {
					new LogService().log(null, effectivePerson.getDistinguishedName(), appInfo.getAppName(), appInfo.getId(), "", "", "", "APPINFO", "新增");
				}
				
				// 更新缓存
				ApplicationCache.notify(AppInfo.class);
				ApplicationCache.notify(AppDict.class);
				ApplicationCache.notify(AppDictItem.class);
				ApplicationCache.notify(View.class);
				ApplicationCache.notify(ViewCategory.class);
				ApplicationCache.notify(ViewFieldConfig.class);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess(e, "应用栏目信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AppInfo {
		
		private static final long serialVersionUID = -6314932919066148113L;
		
		@FieldDescribe("指定用于操作的身份，可选参数")
		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
		public static WrapCopier<Wi, AppInfo> copier = WrapCopierFactory.wi( Wi.class, AppInfo.class, null, null );
	}

	public static class Wo extends WoId {

	}
	
}