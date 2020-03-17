package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.core.entity.AppInfo;

import net.sf.ehcache.Element;

public class ActionListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAll.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<AppInfo> appInfoList = null;
		Boolean check = true;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "all" );
		Element element = cache.get( cacheKey );
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wos = ( List<Wo> ) element.getObjectValue();
			result.setData( wos );
		} else {
			try {
				appInfoList = appInfoServiceAdv.listAll( null, "全部");
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess( e, "查询所有应用栏目信息对象时发生异常" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( check ){
				if( appInfoList != null && !appInfoList.isEmpty() ){
					try {
						wos = Wo.copier.copy( appInfoList );
						SortTools.desc( wos, "appInfoSeq");
						cache.put(new Element( cacheKey, wos ));
						result.setData( wos );
					} catch (Exception e) {
						Exception exception = new ExceptionAppInfoProcess( e, "将查询出来的应用栏目信息对象转换为可输出的数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		return result;
	}
}