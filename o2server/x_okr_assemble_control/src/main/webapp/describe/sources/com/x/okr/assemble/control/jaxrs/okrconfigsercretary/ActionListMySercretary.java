package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionSercretaryConfigListByIds;
import com.x.okr.entity.OkrConfigSecretary;

import net.sf.ehcache.Element;

public class ActionListMySercretary extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListMySercretary.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		List<Wo> wraps = null;
		List<String> ids = null;
		List<OkrConfigSecretary> okrConfigSecretaryList = null;
		String cacheKey = catchNamePrefix + "." + effectivePerson.getDistinguishedName();
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wraps = (List<Wo>) element.getObjectValue();
			result.setData( wraps );
		}else{
			try {
				ids = okrConfigSecretaryService.listIdsByPerson( effectivePerson.getDistinguishedName() );
				if( ids != null && ids.size() > 0 ){
					okrConfigSecretaryList = okrConfigSecretaryService.listByIds( ids );
				}
				if( okrConfigSecretaryList != null ){
					wraps = Wo.copier.copy( okrConfigSecretaryList );
					cache.put( new Element( cacheKey, wraps ) );
					result.setData( wraps );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionSercretaryConfigListByIds( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrConfigSecretary  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		
		public static WrapCopier<OkrConfigSecretary, Wo> copier = WrapCopierFactory.wo( OkrConfigSecretary.class, Wo.class, null,JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}