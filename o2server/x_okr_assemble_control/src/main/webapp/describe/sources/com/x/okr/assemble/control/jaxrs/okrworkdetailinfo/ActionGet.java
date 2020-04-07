package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo;

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
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.ExceptionWorkQueryById;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		
		Wo wrap = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionWorkIdEmpty();
			result.error( exception );
		}
		try {
			okrWorkDetailInfo = okrWorkDetailInfoService.get( id );
			if( okrWorkDetailInfo != null ){
				wrap = Wo.copier.copy( okrWorkDetailInfo );
				result.setData(wrap);
			}else{
				Exception exception = new ExceptionWorkNotExists( id );
				result.error( exception );
			}
		} catch (Exception e) {
			Exception exception = new ExceptionWorkQueryById( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return result;
	}
	
	public static class Wo extends OkrWorkDetailInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkDetailInfo, Wo> copier = WrapCopierFactory.wo( OkrWorkDetailInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}

}