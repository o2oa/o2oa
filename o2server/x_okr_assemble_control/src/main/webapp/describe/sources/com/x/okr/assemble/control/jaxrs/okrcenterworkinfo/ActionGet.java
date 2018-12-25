package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionUserNoLogin;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrConfigWorkType;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		WoOkrWorkType wrapOutType = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrUserCache okrUserCache = null;
		List<WoOkrWorkType> wrapOutTypes = new ArrayList<>();
		List<String> operation = new ArrayList<>();
		List<OkrConfigWorkType> types = null;
		Boolean check = true;
		
		if( id != null && !id.isEmpty() ){
			try {
				okrCenterWorkInfo = okrCenterWorkQueryService.get( id );
			} catch (Exception e) {
				Exception exception = new ExceptionCenterWorkQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		if( check && okrUserCache.getLoginUserName() == null ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		if( check ){
			if( okrCenterWorkInfo != null ){				
				//判断中心工作的操作列表
				wrap = Wo.copier.copy( okrCenterWorkInfo );
				if( wrap != null ){
					wrap.setIsNew( false );//不是新创建的草稿, 是已存在的
					if( "草稿".equals( wrap.getProcessStatus() )){
						//查询所有的工作类别，初始化选择框时需要
						types = okrConfigWorkTypeService.listAll();
						if( types != null && !types.isEmpty() ){
							for( OkrConfigWorkType type : types ){
								wrapOutType = new WoOkrWorkType( type.getId(), type.getWorkTypeName(), type.getOrderNumber() );
								wrapOutTypes.add( wrapOutType );
							}
							SortTools.asc( wrapOutTypes, "orderNumber");
							wrap.setWorkTypes( wrapOutTypes );
						}
					}					
					operation = new ActionListOperationWithId().execute( request, effectivePerson, okrUserCache, wrap.getId() );
					wrap.setOperation( operation );
				}
				result.setData(wrap);
			}else{
				result = new ActionDraftNewCenter().execute( request, effectivePerson);
			}
		}
		return result;
	}
	
}