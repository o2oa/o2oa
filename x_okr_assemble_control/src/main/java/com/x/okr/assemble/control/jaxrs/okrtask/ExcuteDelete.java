package com.x.okr.assemble.control.jaxrs.okrtask;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrTask;
import com.x.organization.core.express.Organization;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrTask okrTask = null;
		Boolean check = true;
		Organization organization = new Organization();
		Boolean hasPermission = false;
		OkrUserCache okrUserCache = null;
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch (Exception e ) {
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
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new TaskIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				okrTask = okrTaskService.get( id );
				if( okrTask == null ){
					check = false;
					Exception exception = new TaskNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch ( Exception e) {
				check = false;
				Exception exception = new TaskQueryByIdException( e , id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				hasPermission = organization.role().hasAny( effectivePerson.getName(),"OkrSystemAdmin" );
				if( !hasPermission ){
					check = false;
					Exception exception = new InsufficientPermissionsException( effectivePerson.getName(),"OkrSystemAdmin" );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new OkrSystemAdminCheckException( e , effectivePerson.getName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check && hasPermission ){
			try{
				okrTaskService.delete( id );
				result.setData( new WrapOutId(id) );
			}catch(Exception e){
				Exception exception = new TaskDeleteException( e , id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( "工作汇报".equals( okrTask.getDynamicObjectType() )){
				try{
					List<String> workTypeList = new ArrayList<String>();
					workTypeList.add( okrTask.getWorkType() );
					okrWorkReportTaskCollectService.checkReportCollectTask( okrTask.getTargetIdentity(), workTypeList );
				}catch( Exception e ){
					logger.warn( "待办信息删除成功，但对汇报者进行汇报待办汇总发生异常。", e );
				}
			}
		}
		if( check ){
			try{
				okrWorkDynamicsService.taskDynamic(
						okrTask.getCenterId(), 
						okrTask.getCenterTitle(), 
						okrTask.getWorkId(), 
						okrTask.getWorkTitle(), 
						okrTask.getTitle(), 
						id, 
						"删除待办待阅", 
						effectivePerson.getName(), 
						"删除待办待阅：" + okrTask.getTitle(), 
						"管理员删除待办待阅操作成功！", 
						okrTask.getTargetName(), 
						okrTask.getTargetIdentity()
				);
			}catch(Exception e){
				Exception exception = new OkrOperationDynamicSaveException( e, id );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}