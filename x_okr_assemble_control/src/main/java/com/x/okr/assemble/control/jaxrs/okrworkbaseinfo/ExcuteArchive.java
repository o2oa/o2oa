package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;


import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkNotExistsException;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteArchive extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteArchive.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrUserCache  okrUserCache  = null;
		Boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
		}
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		}catch(Exception e){
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new WorkNotExistsException( id );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的具体工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				okrWorkBaseInfoOperationService.archive( id );
				result.setData( new WrapOutId( id ) );
				if( okrWorkBaseInfo != null ){
					okrWorkDynamicsService.workDynamic(
							okrWorkBaseInfo.getCenterId(),
							okrWorkBaseInfo.getId(), 
							okrWorkBaseInfo.getTitle(),
							"归档具体工作", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"归档具体工作：" + okrWorkBaseInfo.getTitle(), 
							"具体工作归档成功！"
					);
				}else{
					okrWorkDynamicsService.workDynamic(
							id, 
							null,
							"无",
							"归档中心工作", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"归档中心工作：" + id, 
							"中心工作归档成功！"
					);
				}
			}catch(Exception e){
				Exception exception = new WorkBaseInfoProcessException( e, "系统根据工作ID列表查询附件信息列表发生异常. ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}