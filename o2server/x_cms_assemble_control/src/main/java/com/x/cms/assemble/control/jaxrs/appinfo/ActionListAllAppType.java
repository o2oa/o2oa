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

import net.sf.ehcache.Element;

public class ActionListAllAppType extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAllAppType.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<String> appTypes = null;
		Boolean check = true;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "allType" );
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
				if( appTypes != null && !appTypes.isEmpty() ){
					for( String type : appTypes ) {
						if( !"未分类".equals( type )) {
							wos.add( new Wo( type, appInfoServiceAdv.countAppInfoWithAppType( type )));
						}
					}
				}
				
				Long outTypeCount = appInfoServiceAdv.countAppInfoWithOutAppType();
				if( outTypeCount != null && outTypeCount > 0 ) {
					wos.add( new Wo( "未分类", outTypeCount ));
				}
				
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