package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrConfigWorkType;

public class ExcuteGet extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutOkrCenterWorkInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		WrapOutOkrCenterWorkInfo wrap = null;
		WrapOutOkrWorkType wrapOutType = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrUserCache okrUserCache = null;
		List<WrapOutOkrWorkType> wrapOutTypes = new ArrayList<>();
		List<String> operation = new ArrayList<>();
		List<OkrConfigWorkType> types = null;
		Boolean check = true;
		
		if( id != null && !id.isEmpty() ){
			try {
				okrCenterWorkInfo = okrCenterWorkQueryService.get( id );
			} catch (Throwable th) {
				Exception exception = new CenterWorkIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check && okrUserCache.getLoginUserName() == null ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			if( okrCenterWorkInfo != null ){				
				//判断中心工作的操作列表
				wrap = wrapout_copier.copy( okrCenterWorkInfo );
				if( wrap != null ){
					wrap.setIsNew( false );//不是新创建的草稿, 是已存在的
					if( "草稿".equals( wrap.getProcessStatus() )){
						//查询所有的工作类别，初始化选择框时需要
						types = okrConfigWorkTypeService.listAll();
						if( types != null && !types.isEmpty() ){
							for( OkrConfigWorkType type : types ){
								wrapOutType = new WrapOutOkrWorkType( type.getId(), type.getWorkTypeName(), type.getOrderNumber() );
								wrapOutTypes.add( wrapOutType );
							}
							SortTools.asc( wrapOutTypes, "orderNumber");
							wrap.setWorkTypes( wrapOutTypes );
						}
					}					
					operation = new ExcuteListOperationWithId().execute( request, effectivePerson, okrUserCache, wrap.getId() );
					wrap.setOperation( operation );
				}
				result.setData(wrap);
			}else{
				result = new ExcuteDraftNewCenter().execute( request, effectivePerson);
			}
		}
		return result;
	}
	
}