package com.x.okr.assemble.control.jaxrs.okrtask;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskNotExists;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskQueryById;
import com.x.okr.entity.OkrTask;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrTask okrTask = null;
		Boolean check = true;
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionTaskIdEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				okrTask = okrTaskService.get( id );
				if( okrTask != null ){
					wrap = Wo.copier.copy( okrTask );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionTaskNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionTaskQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}	
		return result;
	}
	
	public static class Wo extends OkrTask{

		private static final long serialVersionUID = -5076990764713538973L;

		
		public static WrapCopier<OkrTask, Wo> copier = WrapCopierFactory.wo( OkrTask.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}

}