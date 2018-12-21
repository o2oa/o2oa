package com.x.okr.assemble.control.jaxrs.okrconfigsystem;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionSystemConfigListAll;
import com.x.okr.entity.OkrConfigSystem;

import net.sf.ehcache.Element;

public class ActionListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAll.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request,EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		List<Wo> wraps = null;
		List<OkrConfigSystem> okrConfigSystemList = null;
		
		String cacheKey = catchNamePrefix + ".all";
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wraps = ( List<Wo> ) element.getObjectValue();
			result.setData( wraps );
			result.setCount( Long.parseLong( wraps.size() +"" ) );
		}else{
			try {
				okrConfigSystemList = okrConfigSystemService.listAll();
				if( okrConfigSystemList != null ){
					wraps = Wo.copier.copy( okrConfigSystemList );
					SortTools.asc( wraps, true, "orderNumber");
					cache.put( new Element( cacheKey, wraps ) );
					result.setCount( Long.parseLong( wraps.size() +"" ) );
					result.setData( wraps );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionSystemConfigListAll( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrConfigSystem{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

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