package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

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
import com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception.ExceptionWorkLevelConfigListAll;
import com.x.okr.entity.OkrConfigWorkLevel;

public class ActionListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAll.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request,EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<OkrConfigWorkLevel> okrConfigWorkLevelList = null;
		try {
			okrConfigWorkLevelList = okrConfigWorkLevelService.listAll();
			if( okrConfigWorkLevelList != null ){
				wraps = Wo.copier.copy( okrConfigWorkLevelList );
				result.setData(wraps);
			}
		} catch (Exception e) {
			Exception exception = new ExceptionWorkLevelConfigListAll( e );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return result;
	}
	
	public static class Wo extends OkrConfigWorkLevel{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrConfigWorkLevel, Wo> copier = WrapCopierFactory.wo( OkrConfigWorkLevel.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}