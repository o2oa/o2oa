package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInFilterCenterWorkInfo;
import com.x.okr.entity.OkrConfigWorkType;

public class ExcuteListTypeCount extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListTypeCount.class );
	
	protected ActionResult<List<WrapOutOkrConfigWorkType>> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInFilterCenterWorkInfo wrapIn ) throws Exception {
		ActionResult<List<WrapOutOkrConfigWorkType>> result = new ActionResult<>();
		List<WrapOutOkrConfigWorkType> wraps = null;
		List<OkrConfigWorkType> okrConfigWorkTypeList = null;
		List<String> workTypes = new ArrayList<>();
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;

		if( wrapIn == null ){
			wrapIn = new WrapInFilterCenterWorkInfo();
		}
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}		
		if( check ){
			try {
				okrConfigWorkTypeList = okrConfigWorkTypeService.listAll();
				if( okrConfigWorkTypeList != null && !okrConfigWorkTypeList.isEmpty() ){
					wraps = wrapout_copier.copy( okrConfigWorkTypeList );
					for( WrapOutOkrConfigWorkType wrap : wraps ){
						//统计用户可以看到的每一个类别的中心工作数量
						workTypes.clear();
						if( wrap.getWorkTypeName() != null && !wrap.getWorkTypeName().isEmpty() ){
							workTypes.add( wrap.getWorkTypeName() );
						}
						wrapIn.setDefaultWorkTypes( workTypes );
						if( !okrUserCache.isOkrSystemAdmin() ){
							wrapIn.setIdentity( okrUserCache.getLoginIdentityName() );
							total = okrWorkPersonSearchService.getCenterCountWithFilter( wrapIn );
						}else{
							WrapInFilterCenterWorkInfo wrpaIn_admin = new WrapInFilterCenterWorkInfo();
							wrpaIn_admin.setDefaultWorkTypes( workTypes );
							total = okrCenterWorkQueryService.getCountWithFilter( wrpaIn_admin );
						}
						wrap.setCenterCount( total );
					}
					result.setData( wraps );
				}
			} catch ( Exception e) {
				Exception exception = new WorkTypeConfigListTypeCountException(e);
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}	
		return result;
	}
	
}