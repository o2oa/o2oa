package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrConfigWorkType;
import com.x.okr.entity.OkrTask;

/**
 * 类 名：OkrWorkReportBaseInfoService<br/>
 * 实体类：OkrWorkReportBaseInfo<br/>
 * 作 者：Liyi<br/>
 * 单 位：O2 Team<br/>
 * 日 期：2016-05-20 17:17:27
 **/
public class OkrWorkReportTaskCollectService {

	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrTaskService okrTaskService = new OkrTaskService();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrConfigWorkTypeService okrConfigWorkTypeService = new OkrConfigWorkTypeService();
	private DateOperation dateOperation = new DateOperation();

	public boolean checkAllReportCollectTask() throws Exception {
		
		//List<String> taskTypeList = new ArrayList<String>();
		List<String> targetIdentities = null;
		List<OkrConfigWorkType> workTypeList = null;
		List<String> workTypeNameList = new ArrayList<String>();
		//taskTypeList.add( "工作汇报" );
		//taskTypeList.add( "工作汇报汇总" );
		
		//logger.debug( "系统尝试对所有用户进行工作汇报汇总待办进行核对......" );
		// 获取工作汇报待办以及所有工作汇报汇总待办涉及到的所有员工身份
		//targetIdentities = okrTaskService.listDistinctIdentity( taskTypeList );
		// 获取工作所有干系人身份
		targetIdentities = okrWorkPersonService.listDistinctIdentity();
		
		// 遍历所有的员工身份，查询该员工的汇总待办是否已经存在，如果存在则不作处理，如果不存在，则删除汇总待办
		//taskTypeList.clear();
		//taskTypeList.add( "工作汇报" );
		
		workTypeList = okrConfigWorkTypeService.listAll();		
		if( workTypeList != null && !workTypeList.isEmpty() ){
			for( OkrConfigWorkType workType : workTypeList ){
				workTypeNameList.add( workType.getWorkTypeName() );
			}
		}
		if (targetIdentities != null && !targetIdentities.isEmpty()) {
			for (String targetIdentity : targetIdentities) {
				checkReportCollectTask( targetIdentity, workTypeNameList );
			}
		}
		
		return true;
	}

	public boolean checkReportCollectTask( String userIdentity, List<String> workTypeList ) throws Exception {

		List<String> taskTypeList = new ArrayList<String>();
		List<OkrTask> okrTasks = null;
		OkrTask okrTask = null;
		Long taskCount = 0L;
		Business business = null;
		String personName = okrUserManagerService.getPersonNameByIdentity( userIdentity );
		
		if ( personName != null ) {
			//logger.debug( "对用户[" + userIdentity + "]进行工作汇报汇总待办进行核对......" );
			// 遍历所有的员工身份，查询该员工的汇总待办是否已经存在，如果存在则不作处理，如果不存在，则删除汇总待办
			taskTypeList.clear();
			taskTypeList.add( "工作汇报" );
			
			//根据不同的工作类别进行汇总
			for( String workType : workTypeList ){			
				taskCount = okrTaskService.getNotReportConfirmTaskCount( taskTypeList, userIdentity, workType );
				// 查询汇总待办是否存在，如果不存在，则新增一条汇总待办
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					business = new Business(emc);
					//查询该员工是否已经存在工作汇报汇总信息
					taskTypeList.clear();
					taskTypeList.add( "工作汇报汇总" );
					okrTasks = business.okrTaskFactory().listTaskByTaskType( taskTypeList, userIdentity, workType );
					emc.beginTransaction( OkrTask.class );
					if ( taskCount != null && taskCount > 0 ) {
						if ( okrTasks == null || okrTasks.isEmpty() ) {
							okrTask = new OkrTask();
							okrTask.setTitle( "重点工作布置落实推进情况反馈( " + workType + " )" );
							okrTask.setDynamicObjectTitle( "重点工作布置落实推进情况反馈( " + workType + " )" );
							okrTask.setTargetIdentity(userIdentity);
							okrTask.setTargetName( personName );
							okrTask.setTargetUnitName( okrUserManagerService.getUnitNameByIdentity(userIdentity));
							okrTask.setTargetTopUnitName( okrUserManagerService.getTopUnitNameByIdentity(userIdentity));
							okrTask.setActivityName( "汇总" );
							okrTask.setArriveDateTime(new Date());
							okrTask.setArriveDateTimeStr(dateOperation.getNowDateTime());
							okrTask.setDynamicObjectType( "工作汇报汇总" );
							okrTask.setProcessType( "TASK" );
							okrTask.setStatus( "正常" );
							okrTask.setCenterId( "" );
							okrTask.setCenterTitle( "" );
							okrTask.setWorkId( "" );
							okrTask.setWorkTitle( "" );
							okrTask.setDynamicObjectId( "" );
							okrTask.setWorkType( workType );
							okrTask.setViewUrl( "" );
							emc.persist(okrTask, CheckPersistType.all);
						}else{							
							for( int i=0;  i<okrTasks.size(); i++ ){
								if( i == 0 ){
									okrTask = okrTasks.get( 0 );
									okrTask.setArriveDateTime(new Date());
									okrTask.setArriveDateTimeStr(dateOperation.getNowDateTime());
									emc.check(okrTask, CheckPersistType.all);
								}else{
									emc.remove( okrTasks.get(i), CheckRemoveType.all );
								}
							}
						}
					} else {// 删除该用户的汇总待办
						for( int i=0;  i<okrTasks.size(); i++ ){
							emc.remove( okrTasks.get(i), CheckRemoveType.all );
						}
					}
					emc.commit();
				} catch (Exception e) {
					throw e;
				}
			}			
		}
		return true;
	}
}
