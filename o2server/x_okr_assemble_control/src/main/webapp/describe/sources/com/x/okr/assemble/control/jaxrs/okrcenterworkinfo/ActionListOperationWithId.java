package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionListOperationWithId extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListOperationWithId.class );
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoQueryService = new OkrWorkBaseInfoQueryService();
	private OkrTaskService okrTaskService = new OkrTaskService();
	
	/**
	 * 1、中心工作是否存在
	 * 2、中心工作的状态：草稿，已部署， 已归档
	 * 3、当前用户在中心工作中的处理身份
	 * 
	 * 管理员（系统管理员，顶层组织工作管理员）
	 * 
	 * 创建具体工作CREATEWORK：
	 * 导入具体工作IMPORTWORK：
	 *     1）中心工作不存在的时候（后台不控制）
	 *     2）已部署，中心工作创建者|部署者
	 *     3）已归档，不能导入
	 *     
	 * 删除中心工作DELETE：
	 * 	   1）草稿状态：创建者|部署者可以删除
	 *     2）已部署：不能删除 （管理员可以）
	 *     3）已归档：不能删除 （管理员可以）
	 *     
	 * 部署中心工作DEPLOY：
	 * 	   1）当前用户在该中心工作下有未部署的工作时，显示部署中心工作
	 * 
	 * 归档中心工作ARCHIVE：
	 * 	   1）中心工作不是草稿，未归档
	 *     2）管理员和部署者|创建者
	 * 
	 * 关闭CLOSE：所有人都有
	 * 
	 * @param effectivePerson
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	protected List<String> execute( HttpServletRequest request, EffectivePerson effectivePerson, OkrUserCache okrUserCache, String id ) {

		List<String> operation = new ArrayList<>();
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<String> ids = null;
		 OkrCenterWorkInfo okrCenterWorkInfo = null;
		Boolean isTopUnitWorkAdmin = false;
		String status = null;
		String processStatus = null;
		Boolean confirm = true;
		
		try {
			okrCenterWorkInfo = okrCenterWorkQueryService.get( id );
		} catch (Exception e) {
			logger.error( e, effectivePerson, request, null);
		}
		operation.add( "VIEW" );
		//如果中心工作为空
		if( okrCenterWorkInfo == null ){
			operation.add( "CREATEWORK" );
			operation.add( "IMPORTWORK" );
			operation.add( "CLOSE" );
			return operation;
		}
		
		//中心工作不为空
		status = okrCenterWorkInfo.getStatus();
		processStatus = okrCenterWorkInfo.getProcessStatus();
		
		try {
			if( okrUserManagerService.isOkrWorkManager( okrUserCache.getLoginIdentityName() )){
				isTopUnitWorkAdmin = true;
			}
		} catch (Exception e ) {
			logger.error( e, effectivePerson, request, null);
		}
		
		//创建具体工作|导入具体工作
		if( !"已归档".equals( status )){
			if( okrCenterWorkInfo.getDeployerIdentity().equals( okrUserCache.getLoginIdentityName() )
			 ||	okrCenterWorkInfo.getCreatorIdentity().equals( okrUserCache.getLoginIdentityName() )
			){
				operation.add( "EDIT" );
				operation.add( "CREATEWORK" );
				operation.add( "IMPORTWORK" );
			}
		}
		
		//部署中心工作：当前用户在该中心工作下有未部署[草稿]的工作时，显示部署中心工作
		if( !"已归档".equals( status )){
			try {
				ids = okrWorkPersonService.listDistinctWorkIdsByWorkAndIdentity( okrCenterWorkInfo.getId(), null, okrUserCache.getLoginIdentityName(), "部署者", null );
				
				if( ids != null && !ids.isEmpty() ){
					okrWorkBaseInfoList = okrWorkBaseInfoQueryService.listByIds(ids);
					
					if( okrWorkBaseInfoList != null && !okrWorkBaseInfoList.isEmpty() ){
						for( OkrWorkBaseInfo work : okrWorkBaseInfoList ){
							if( work != null && "草稿".equals( work.getWorkProcessStatus() )){
								operation.add( "DEPLOY" );
								confirm = false;
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error( e, effectivePerson, request, null);
			}
		}		
		//归档中心工作
		if( !"草稿".equals( processStatus ) && !"已归档".equals( status ) ){
			//如果用户是管理,或者是部署者
			if( okrUserCache.isOkrManager() || isTopUnitWorkAdmin || okrUserCache.getLoginIdentityName().equals( okrCenterWorkInfo.getDeployerIdentity() )){
				operation.add( "ARCHIVE" );
			}
		}
		//删除
		if( "草稿".equals( processStatus )){
			operation.add( "DELETE" );
		}else{
			if( okrUserCache.isOkrManager() || isTopUnitWorkAdmin ){
				operation.add( "DELETE" );
			}
		}
		if( confirm ){
			//看看用户是否仍有该中心工作的工作确认的待办
			try {
				ids = okrTaskService.listIdsByCenterAndPerson( okrCenterWorkInfo.getId(), okrUserCache.getLoginIdentityName(), "中心工作") ;
				if( ids != null && !ids.isEmpty() ){
					operation.add( "CONFIRM" );
				}
			} catch (Exception e) {
				logger.error( e, effectivePerson, request, null);
			}
		}
		operation.add( "CLOSE" );
		return operation;
	}
}