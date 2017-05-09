package com.x.okr.assemble.control.jaxrs.okrtask;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ReadProcessException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.TaskIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.TaskInfoIsNotForReadException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.TaskNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.TaskProcessPermissionException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.TaskQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.UserNoLoginException;
import com.x.okr.entity.OkrTask;

public class ExcuteReadProcess extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteReadProcess.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult< WrapOutId > result = new ActionResult<>();
		OkrTask okrTask = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new TaskIdEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if(check){
			//判断待阅信息是否存在
			try{
				okrTask = okrTaskService.get(id);
			}catch(Exception e){
				check = false;
				Exception exception = new TaskQueryByIdException( e , id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if(check){
			if( okrTask == null ){
				check = false;
				Exception exception = new TaskNotExistsException( id );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if(check){
			//判断是否为待阅信息
			if( !"READ".equals( okrTask.getProcessType() )){
				check = false;
				Exception exception = new TaskInfoIsNotForReadException( id );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if(check){
			if( !okrUserCache.getLoginIdentityName() .equals( okrTask.getTargetIdentity() )){
				check = false;
				Exception exception = new TaskProcessPermissionException( okrUserCache.getLoginIdentityName(), id );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if(check){
			try{
				//处理待阅信息
				okrTaskService.processRead( okrTask );
				try {
					okrWorkDynamicsService.processReadDynamic(
							okrTask, 
							"阅知工作汇报", 
							effectivePerson.getName(),
							okrUserCache.getLoginIdentityName() ,
							okrUserCache.getLoginIdentityName() , 
							"阅知了工作汇报：" + okrTask.getTitle(),
							"工作汇报阅知成功！");
				} catch (Exception e) {
					logger.warn("system save reportDynamic got an exception.");
					logger.error(e);
				}
				result.setData( new WrapOutId(id) );
			}catch( Exception e ){
				check = false;
				Exception exception = new ReadProcessException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}