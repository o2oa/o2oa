package com.x.okr.assemble.control.jaxrs.okrtask;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionReadProcess;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskInfoIsNotForRead;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskNotExists;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskProcessPermission;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskQueryById;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrTask;

public class ActionReadProcess extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionReadProcess.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult< Wo > result = new ActionResult<>();
		OkrTask okrTask = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionTaskIdEmpty();
				result.error( exception );
			}
		}
		
		if(check){
			//判断待阅信息是否存在
			try{
				okrTask = okrTaskService.get(id);
			}catch(Exception e){
				check = false;
				Exception exception = new ExceptionTaskQueryById( e , id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if(check){
			if( okrTask == null ){
				check = false;
				Exception exception = new ExceptionTaskNotExists( id );
				result.error( exception );
			}
		}
		
		if(check){
			//判断是否为待阅信息
			if( !"READ".equals( okrTask.getProcessType() )){
				check = false;
				Exception exception = new ExceptionTaskInfoIsNotForRead( id );
				result.error( exception );
			}
		}
		
		if(check){
			if( !okrUserCache.getLoginIdentityName() .equals( okrTask.getTargetIdentity() )){
				check = false;
				Exception exception = new ExceptionTaskProcessPermission( okrUserCache.getLoginIdentityName(), id );
				result.error( exception );
			}
		}
		
		if(check){
			try{//处理待阅信息
				okrTaskService.processRead( okrTask );
				result.setData( new Wo(id) );
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionReadProcess( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if(check){
			if( okrTask != null ) {
				WrapInWorkDynamic.sendWithTask( 
						okrTask, 
						effectivePerson.getDistinguishedName(),
						okrUserCache.getLoginUserName(),
						okrUserCache.getLoginUserName(),
						"阅知工作汇报",
						"工作汇报阅知成功！"
				);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
	
}