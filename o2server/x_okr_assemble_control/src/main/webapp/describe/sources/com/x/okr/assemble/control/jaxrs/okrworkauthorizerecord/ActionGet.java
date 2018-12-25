package com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord;

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
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.exception.ExceptionAuthorizeRecordIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.exception.ExceptionAuthorizeRecordNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.exception.ExceptionAuthorizeRecordQueryById;
import com.x.okr.entity.OkrWorkAuthorizeRecord;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionAuthorizeRecordIdEmpty();
			result.error( exception );
		}else{
			try {
				okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.get( id );
				if( okrWorkAuthorizeRecord != null ){
					wrap = Wo.copier.copy( okrWorkAuthorizeRecord );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionAuthorizeRecordNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionAuthorizeRecordQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrWorkAuthorizeRecord{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkAuthorizeRecord, Wo> copier = WrapCopierFactory.wo( OkrWorkAuthorizeRecord.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}

}