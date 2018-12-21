package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.ExceptionWorkDetailSave;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Wi wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			if( wrapIn.getId() == null ){
				check = false;
				Exception exception = new ExceptionWorkIdEmpty();
				result.error( exception );
			}
			if( check ){
				//查询工作信息，补充工作详细信息的ID
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getId() );
					if( okrWorkBaseInfo == null ){
						check = false;
						Exception exception = new ExceptionWorkNotExists( wrapIn.getId() );
						result.error( exception );
					}else{
						wrapIn.setCenterId( okrWorkBaseInfo.getCenterId() ); //ID需要查询确认一下，数据一定要有效
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionWorkQueryById( e, wrapIn.getId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}			
			try {
				okrWorkDetailInfoService.save( wrapIn );
				result.setData( new Wo(wrapIn.getId()) );
			} catch (Exception e) {
				Exception exception = new ExceptionWorkDetailSave( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends OkrWorkDetailInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

	}


	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}