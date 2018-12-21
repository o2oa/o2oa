package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigNotExists;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigQueryById;
import com.x.okr.entity.OkrConfigWorkType;

import net.sf.ehcache.Element;

public class ExcuteGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrConfigWorkType okrConfigWorkType = null;

		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionWorkTypeConfigIdEmpty();
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
					okrConfigWorkType = okrConfigWorkTypeService.get( id );
					if( okrConfigWorkType != null ){
						wrap = Wo.copier.copy( okrConfigWorkType );
						cache.put( new Element( cacheKey, wrap ) );						
						result.setData(wrap);
					}else{
						Exception exception = new ExceptionWorkTypeConfigNotExists( id );
						result.error( exception );
					}
				} catch (Exception e) {
					Exception exception = new ExceptionWorkTypeConfigQueryById( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}		
		return result;
	}
	
	public static class Wo extends OkrConfigWorkType{

		private static final long serialVersionUID = -5076990764713538973L;

		
		public static WrapCopier<OkrConfigWorkType, Wo> copier = WrapCopierFactory.wo( OkrConfigWorkType.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long centerCount = 0L;

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Long getCenterCount() {
			return centerCount;
		}

		public void setCenterCount(Long centerCount) {
			this.centerCount = centerCount;
		}
	}
}