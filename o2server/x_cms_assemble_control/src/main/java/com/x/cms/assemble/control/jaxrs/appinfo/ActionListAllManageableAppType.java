package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionListAllManageableAppType extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAllManageableAppType.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<String> appTypes = null;
		Boolean check = true;
		String personName = effectivePerson.getDistinguishedName();
		Boolean isAnonymous = effectivePerson.isAnonymous();
		Boolean isManager = false;

		try {
			isManager = userManagerService.isManager( effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionAppInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName, isAnonymous, isManager );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			wos = new ArrayList<>();
			try {
				appTypes = appInfoServiceAdv.listAllAppType();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess( e, "查询所有应用栏目信息对象时发生异常" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( check ){
				//查询每个类别的应用ID列表，查询出ID列表后，根据ID列表查询用户可访问的栏目对象
				List<String> appIdsForType = null;
				if(ListTools.isNotEmpty( appTypes ) ){
					for( String appType : appTypes ) {
						if( !"未分类".equals( appType )) {
							appIdsForType = appInfoServiceAdv.listAppIdsWithAppType( appType );
							if( !isManager && ListTools.isNotEmpty( appIdsForType ) ){
								List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
								List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
								appIdsForType = permissionQueryService.listManageableAppIdsByPerson(personName, unitNames, groupNames, null, null, appType, null, 99 );
							}
							if( appIdsForType == null ){
								appIdsForType = new ArrayList<>();
							}
							if( appIdsForType.size() > 0 ) {
								wos.add( new Wo( appType, Long.parseLong( appIdsForType.size() + "") ));
							}
						}
					}
				}
				//查询所有的未分类的并且有权限查看的栏目列表
				appIdsForType = appInfoServiceAdv.listAppIdsWithOutAppType();
				if( !isManager && ListTools.isNotEmpty( appIdsForType ) ){
					List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
					List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
					appIdsForType = permissionQueryService.listManageableAppIdsByPerson(personName, unitNames, groupNames, appIdsForType, null, null, null, 99 );
				}

				if( appIdsForType == null ){
					appIdsForType = new ArrayList<>();
				}
				
				if( appIdsForType.size() > 0 ) {
					wos.add( new Wo( "未分类", Long.parseLong( appIdsForType.size() + "") ));
				}

				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData( wos );
			}
		}
		return result;
	}
	
	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("栏目类别名称")
		private String appType;
		
		@FieldDescribe("栏目数量")
		private Long count;
		
		public Wo( String _appType, Long _count ) {
			this.appType = _appType;
			this.count = _count;
		}
		
		public String getAppType() {
			return appType;
		}
		public void setAppType(String appType) {
			this.appType = appType;
		}
		public Long getCount() {
			return count;
		}
		public void setCount(Long count) {
			this.count = count;
		}
	}
	
}