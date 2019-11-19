package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.x.base.core.project.tools.ListTools;
import net.sf.ehcache.Element;

public class ActionListAllAppType extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAllAppType.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<String> appTypes = null;
		Boolean check = true;
		String personName = effectivePerson.getName();
		Boolean isAnonymous = effectivePerson.isAnonymous();
		Boolean isManager = effectivePerson.isManager() || effectivePerson.isCipher();

		String cacheKey = ApplicationCache.concreteCacheKey( "allType", personName, isAnonymous, isManager );
		Element element = cache.get( cacheKey );
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wos = ( List<Wo> ) element.getObjectValue();
			result.setData( wos );
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
								appIdsForType = permissionQueryService.listViewableAppIdByPerson( personName, isAnonymous, unitNames, groupNames, appIdsForType, null, null, appType, 99 );
							}
							if( appIdsForType == null ){
								appIdsForType = new ArrayList<>();
							}
							wos.add( new Wo( appType, Long.parseLong( appIdsForType.size() + "") ));
						}
					}
				}
				//查询所有的未分类的并且有权限查看的栏目列表
				appIdsForType = appInfoServiceAdv.listAppIdsWithOutAppType();
				if( !isManager && ListTools.isNotEmpty( appIdsForType ) ){
					List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
					List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
					appIdsForType = permissionQueryService.listViewableAppIdByPerson( personName, isAnonymous, unitNames, groupNames, appIdsForType, null, null, null, 99 );
				}
				if( appIdsForType == null ){
					appIdsForType = new ArrayList<>();
				}
				wos.add( new Wo( "未分类", Long.parseLong( appIdsForType.size() + "") ));

				cache.put(new Element( cacheKey, wos ));
				result.setData( wos );
			}
		}
		return result;
	}
	
	public static class Wo {
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