package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkCanNotDeleteException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	/**
	 * 删除工作服务
	 * @param effectivePerson
	 * @param id
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		List<String> ids = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		boolean check = true;
		
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName()  );
			result.error( exception );
		}
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
		}
		if( check ){
			try {
				ids = okrWorkBaseInfoService.getSubNormalWorkBaseInfoIds( id );
			} catch (Exception e ) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "根据指定工作ID查询所有下级工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty()){
				check = false;
				Exception exception = new WorkCanNotDeleteException( ids );
				result.error( exception );
			}
		}
		if( check ){
			try{
				okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
			}catch(Exception e){
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的具体工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				okrWorkBaseInfoOperationService.deleteByWorkId( id );
				result.setData( new WrapOutId(id) );
			}catch(Exception e){
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "工作删除过程中发生异常。"+id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( okrWorkBaseInfo != null ){
				try{
					okrWorkDynamicsService.workDynamic(
							okrWorkBaseInfo.getCenterId(), 
							okrWorkBaseInfo.getId(),
							okrWorkBaseInfo.getTitle(),
							"删除具体工作", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"删除具体工作：" + okrWorkBaseInfo.getTitle(), 
							"具体工作删除成功！"
					);
				}catch(Exception e){
					logger.warn( "system save work dynamic got an exception." );
					logger.error( e );
				}
			}else{
				try{
					okrWorkDynamicsService.workDynamic(
							"0000-0000-0000-0000", 
							id,
							"未知",
							"删除具体工作", 
							effectivePerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"删除具体工作：未知", 
							"具体工作删除成功！"
					);
				}catch(Exception e){
					logger.warn( "system save work dynamic got an exception." );
					logger.error( e );
				}
			}
		}
		return result;
	}
	
}