package com.x.okr.assemble.control.jaxrs.okrworkdynamics;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.ExceptionWorkDynamicsIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.ExceptionWorkDynamicsNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.ExceptionWorkDynamicsQueryById;
import com.x.okr.entity.OkrWorkDynamics;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrWorkDynamics okrWorkDynamics = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionWorkDynamicsIdEmpty();
			result.error( exception );
		}else{
			try {
				okrWorkDynamics = okrWorkDynamicsService.get( id );
				if( okrWorkDynamics != null ){
					wrap = Wo.copier.copy( okrWorkDynamics );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionWorkDynamicsNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionWorkDynamicsQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrWorkDynamics{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrWorkDynamics, Wo> copier = WrapCopierFactory.wo( OkrWorkDynamics.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}


}