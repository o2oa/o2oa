package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionSystemConfigIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionSystemConfigNotExists;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionSystemConfigQueryById;
import com.x.okr.entity.OkrConfigSystem;

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrConfigSystem okrConfigSystem = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionSystemConfigIdEmpty();
			result.error( exception );
		}else{
			String cacheKey = catchNamePrefix + "." + id;
			Element element = null;
			element = cache.get( cacheKey );
			if( element != null ){
				wrap = (Wo) element.getObjectValue();
				result.setData( wrap );
			}else{
				try {
					okrConfigSystem = okrConfigSystemService.get( id );
					if( okrConfigSystem != null ){
						wrap = Wo.copier.copy( okrConfigSystem );						
						cache.put( new Element( cacheKey, wrap ) );						
						result.setData(wrap);
					}else{
						Exception exception = new ExceptionSystemConfigNotExists( id );
						result.error( exception );
					}
				} catch (Exception e) {
					Exception exception = new ExceptionSystemConfigQueryById( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
	public static class Wo extends OkrConfigSystem{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrConfigSystem, Wo> copier = WrapCopierFactory.wo( OkrConfigSystem.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}