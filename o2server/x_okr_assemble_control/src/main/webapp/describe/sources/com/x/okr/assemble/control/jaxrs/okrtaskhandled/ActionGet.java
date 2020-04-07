package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

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
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.ExceptionTaskHandledIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.ExceptionTaskHandledNotExists;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.ExceptionTaskHandledQueryById;
import com.x.okr.entity.OkrTaskHandled;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrTaskHandled okrTaskHandled = null;
		Boolean check = true;
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionTaskHandledIdEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				okrTaskHandled = okrTaskHandledService.get( id );
				if( okrTaskHandled != null ){
					wrap = Wo.copier.copy( okrTaskHandled );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionTaskHandledNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionTaskHandledQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrTaskHandled{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrTaskHandled, Wo>copier = WrapCopierFactory.wo( OkrTaskHandled.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}