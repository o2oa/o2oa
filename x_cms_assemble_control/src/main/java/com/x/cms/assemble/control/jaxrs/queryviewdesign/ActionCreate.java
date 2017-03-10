package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.QueryView;


class ActionCreate extends ActionBase {
	
	private Logger logger = LoggerFactory.getLogger( ActionCreate.class );
	
	ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInQueryView wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		
		if( wrapIn != null ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				AppInfo appInfo = null;
				if( wrapIn.getAppId() != null && !wrapIn.getAppId().isEmpty() ){
					appInfo = emc.find( wrapIn.getAppId(), AppInfo.class );
					if( appInfo != null ){
						emc.beginTransaction( QueryView.class );
						QueryView queryView = new QueryView();
						createCopier.copy( wrapIn, queryView );
						queryView.setAppId( appInfo.getId() );
						queryView.setAppName( appInfo.getAppName() );
						queryView.setCreatorPerson(effectivePerson.getName());
						queryView.setLastUpdatePerson(effectivePerson.getName());
						queryView.setLastUpdateTime(new Date());
						this.transQuery(queryView);
						emc.persist( queryView, CheckPersistType.all );
						emc.commit();
						ApplicationCache.notify( QueryView.class );
						WrapOutId wrap = new WrapOutId(queryView.getId());
						result.setData(wrap);
					}else{
						Exception exception = new AppInfoNotExistsException( wrapIn.getAppId() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}else{
					Exception exception = new QueryViewAppIdEmptyException();
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
				
			}
		}
//		else{
//			logger.error( "wrapIn is null, can not save view!" );
//			result.error( new Exception("wrapIn is null, can not save view!"));
//			result.setUserMessage( "系统未获取到需要保存的视图信息内容!" );
//		}
		return result;
	}
}
