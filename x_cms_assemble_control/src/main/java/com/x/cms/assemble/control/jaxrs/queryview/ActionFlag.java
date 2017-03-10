package com.x.cms.assemble.control.jaxrs.queryview;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.QueryView;


public class ActionFlag extends ActionBase {
	
	private Logger logger = LoggerFactory.getLogger( ActionFlag.class );

	public ActionResult<WrapOutQueryView> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutQueryView> result = new ActionResult<>();
			Business business = new Business(emc);
			QueryView queryView = business.queryViewFactory().pick( flag );
			if( queryView == null ){
				Exception exception = new QueryViewNotExistsException( flag );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}else{
				if (!business.queryViewFactory().allowRead( effectivePerson, queryView )) {
					Exception exception = new InsufficientPermissionsException( flag );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}else{
					WrapOutQueryView wrap = outCopier.copy( queryView );
					result.setData(wrap);
				}
			}
			return result;
		}
	}

}