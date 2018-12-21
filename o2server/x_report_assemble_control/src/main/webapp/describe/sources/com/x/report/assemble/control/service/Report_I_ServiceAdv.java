package com.x.report.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.lib.util.StringUtil;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.jaxrs.report.ActionSaveOpinion.WiOpinion;
import com.x.report.assemble.control.jaxrs.report.ActionSyncWorkFlowInfo;
import com.x.report.assemble.control.jaxrs.report.element.PermissionInfo;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanDetail;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_C_WorkPlanNextDetail;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_C_WorkProgDetail;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Detail;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_P_Permission;

/**
 * 汇报信息服务类
 * @author O2LEE
 *
 */
public class Report_I_ServiceAdv{

	private Gson gson = XGsonBuilder.instance();
	private Report_I_QueryService report_I_QueryService = new Report_I_QueryService();
	private Report_I_SaveService report_I_SaveService = new Report_I_SaveService();
	private UserManagerService userManagerService = new UserManagerService();
	
	/**
	 * 根据指定的ID获取汇报基础信息
	 * @param id 汇报基础信息ID列表
	 * @return
	 * @throws Exception
	 */
	public Report_I_Base get( String id ) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_QueryService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}
	public Report_I_Detail getDetail(String reportId ) throws Exception {
		if (reportId == null || reportId.isEmpty()) {
			throw new Exception("id is null.");
		}
		List<Report_I_Detail> detailList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			detailList = report_I_QueryService.listDetail(emc, reportId);
			if( detailList != null && !detailList.isEmpty() ) {
				return detailList.get( 0 );
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

    /**
     * 按条件查询汇报基础信息列表
     * @param reportType
     * @param reportObjType
     * @param targetPerson
     * @param targetUnit
     * @param year
     * @param month
     * @param week
     * @param reportDateString
     * @param createDateString
     * @return
     * @throws Exception
     */
	public List<Report_I_Base> list(String reportType, String reportObjType, String targetPerson, String targetUnit,
			String year, String month, String week, String reportDateString, String createDateString, Boolean listHiddenReport ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_QueryService.list(emc, reportType, reportObjType, targetPerson, targetUnit, year, month, week, 
					reportDateString, createDateString, listHiddenReport );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> lisViewableIdsWithFilter(String title, String reportType, String reportObjType, String year, String month,
			String week, List<String> activityList, List<String> targetList, List<String> currentPersonList, List<String> unitList, 
			String wfProcessStatus, int maxResultCount, Boolean listHiddenReport ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return report_I_QueryService.lisViewableIdsWithFilter(
					emc, title, reportType, reportObjType, year, month, week, activityList, targetList, currentPersonList, unitList, wfProcessStatus,
					maxResultCount, listHiddenReport
			);
		} catch ( Exception e ) {
			throw e;
		}
	}
	public Long countWithIds(List<String> viewAbleReportIds) throws Exception {
		if( viewAbleReportIds == null ){
			return 0L;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return report_I_QueryService.countWithIds( emc, viewAbleReportIds );
		} catch ( Exception e ) {
			throw e;
		}
	}
	public List<Report_I_Base> listNextWithDocIds( String id, Integer count, List<String> viewAbleReportIds,
			String orderField, String orderType, Boolean listHiddenReport) throws Exception {
		if( viewAbleReportIds == null || viewAbleReportIds.isEmpty() ){
			return null;
		}
		if( orderField == null || orderField.isEmpty() ){
			orderField = "targetName";
		}
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return report_I_QueryService.listNextWithDocIds( emc, id, count, viewAbleReportIds, orderField, orderType, listHiddenReport );
		} catch ( Exception e ) {
			throw e;
		}
	}
	public List<String> listIdsForStartWfInProfile(String profileId) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return report_I_QueryService.listIdsForStartWfInProfile( emc, profileId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Report_I_Base> listWithIds(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_QueryService.list(emc, ids);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void startWorkflowSuccess(String id, String processId, String wf_workId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			List<String> ids = null;
			Report_P_Permission permission = null;
			Business business = new Business( emc );
			Report_I_Base report = emc.find( id, Report_I_Base.class );
			if( report != null ) {
				emc.beginTransaction( Report_I_Base.class );
				emc.beginTransaction( Report_P_Permission.class );
				
				report.setWf_WorkId( wf_workId );
				report.setProcessId(processId);
				//report.setActivityName( "拟稿" );
				//report.setReportStatus( "汇报者填写" );
				report.setWfProcessDescription( "" );
				if( StringUtils.isNotEmpty( report.getWfProcessStatus() )) {
					report.setWfProcessStatus( "流转中" );
				}
				
				//所有的权限信息的状态也得更新一下
				ids =  business.report_P_PermissionFactory().listIdsWithReportId( id );
				if( ids != null && !ids.isEmpty() ) {
					for( String _id : ids ) {
						permission = emc.find( _id, Report_P_Permission.class );
						if( permission != null ) {
							permission.setReportStatus( report.getReportStatus() );
							permission.setWfProcessStatus( report.getWfProcessStatus() );
							emc.check( permission, CheckPersistType.all );
						}
					}
				}
				emc.check( report, CheckPersistType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public void startWorkflowError( String id, String processId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			List<String> ids = null;
			Report_P_Permission permission = null;
			Business business = new Business( emc );
			Report_I_Base report = emc.find( id, Report_I_Base.class );
			
			if( report != null ) {
				emc.beginTransaction( Report_I_Base.class );
				emc.beginTransaction( Report_P_Permission.class );
				
				report.setProcessId(processId);
				report.setReportStatus( "汇报流程启动失败！" );
				report.setWfProcessStatus( "错误" );
				report.setWfProcessDescription( "汇报流程启动失败！" );
				
				//所有的权限信息的状态也得更新一下
				ids =  business.report_P_PermissionFactory().listIdsWithReportId( id );
				if( ids != null && !ids.isEmpty() ) {
					for( String _id : ids ) {
						permission = emc.find( _id, Report_P_Permission.class );
						if( permission != null ) {
							permission.setReportStatus( "汇报流程启动失败！" );
							permission.setWfProcessStatus( "错误" );
							emc.check( permission, CheckPersistType.all );
						}
					}
				}
				emc.check( report, CheckPersistType.all );
				emc.commit();
			}
			
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public void updateWfInfo( String reportId, String wfProcessStatus, String wProcessDescription ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			List<String> ids = null;
			Report_P_Permission permission = null;
			Business business = new Business( emc );
			
			Report_I_Base report = emc.find( reportId, Report_I_Base.class );
			if( report != null ) {
				emc.beginTransaction( Report_P_Permission.class );
				
				report.setReportStatus( "系统错误" );
				report.setWfProcessStatus( wfProcessStatus );
				report.setWfProcessDescription( wProcessDescription );
				
				//更新permission状态
				//所有的权限信息的状态也得更新一下
				ids =  business.report_P_PermissionFactory().listIdsWithReportId( reportId );
				if( ids != null && !ids.isEmpty() ) {
					for( String _id : ids ) {
						permission = emc.find( _id, Report_P_Permission.class );
						if( permission != null ) {
							permission.setReportStatus( report.getReportStatus() );
							permission.setWfProcessStatus( report.getWfProcessStatus() );
							emc.check( permission, CheckPersistType.all );
						}
					}
				}
				
				emc.beginTransaction( Report_I_Base.class );
				emc.check( report, CheckPersistType.all );
				emc.commit();
			}
			
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据前台的传参，更新相关数据信息
	 * @param reportId  汇报ID
	 * @param reportStatus  汇报状态
	 * @param activityName  流程环节名称
	 * @param jobId    流程JOBID
	 * @param workflowLog   流转日志
	 * @param authorList 
	 * @param readerList 
	 * @throws Exception
	 */
	public void updateWfInfo( String reportId, String reportStatus, String activityName, String jobId, String workflowLog, List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			
			Report_I_Base report = emc.find( reportId, Report_I_Base.class );
			Report_I_Detail detail = emc.find( reportId, Report_I_Detail.class );
			
			if( report != null ) {
				emc.beginTransaction( Report_I_Detail.class );
				emc.beginTransaction( Report_I_Base.class );
				//更新最后提交时间
				report.setLastSubmitTime( new Date() );
				
				if( StringUtils.isNotEmpty( jobId )) {
					//更新report相关流程信息和状态信息
					report.setReportStatus( "审核中" );
					report.setWfProcessStatus( "流转中" );
					report.setWfProcessDescription( "" );
					
					//更新当前环节名称信息
					if( StringUtils.isNotEmpty( activityName ) ) {
						if( "结束".equals( activityName ) || "已完成".equals( activityName )) {
							report.setActivityName( "已完成" );
							//report.setReportStatus( "已完成" );
							report.setReportStatus( "董事长审阅" );
							report.setWfProcessStatus( "已完成" );
						}else if( "拟稿".equals( activityName ) ){
							report.setActivityName( activityName );
							report.setReportStatus( "汇报者填写" );
						}else {
							report.setActivityName( activityName );
						}
					}else {
						report.setActivityName( "未知" );
						report.setWfProcessDescription( "未获取到流程环节信息" );
					}
					
					//更新JOBID
					if( StringUtils.isNotEmpty( jobId ) ) {
						report.setWf_JobId( jobId );
					}else {
						report.setWf_JobId( "未知" );
						report.setWfProcessDescription( "未获取到流程Work信息" );
					}
					if( detail != null ) {
						if( StringUtils.isNotEmpty( workflowLog )) {
							//更新记录流转日志
							detail.setWorkflowLog( workflowLog );
						}
						//更新作者和读者信息
						if( ListTools.isNotEmpty( readerList )){
							detail.setReaders( gson.toJson( readerList ));
						}else {
							detail.setReaders( "{}" );
						}
						if( ListTools.isNotEmpty( authorList )){
							detail.setAuthors( gson.toJson( authorList ));
						}else {
							detail.setAuthors( "{}" );
						}
						detail.setId( report.getId() );
						emc.check( detail, CheckPersistType.all );
					}else {
						//保存记录流转日志
						detail = new Report_I_Detail();
						detail.setReportId(reportId);
						detail.setTitle( report.getTitle() );
						detail.setWorkflowLog( workflowLog );
						//更新作者和读者信息
						if( ListTools.isNotEmpty( readerList )){
							detail.setReaders( gson.toJson( readerList ));
						}else {
							detail.setReaders( "{}" );
						}
						if( ListTools.isNotEmpty( authorList )){
							detail.setAuthors( gson.toJson( authorList ));
						}else {
							detail.setAuthors( "{}" );
						}
						detail.setId( report.getId() );
						emc.persist( detail, CheckPersistType.all );
					}
				}
				
				//如果外部传入的汇报状态，以外部传入的数据为主
				if( StringUtils.isNotEmpty(reportStatus) ) {
					report.setReportStatus( reportStatus );
				}else {
					report.setReportStatus( "未知状态" );
				}
				
				emc.check( report, CheckPersistType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 后台请求获取指定流程的相关信息后，更新相关数据信息
	 * @param reportId
	 * @param wi
	 * @throws Exception
	 */
	public void updateWfInfo( String reportId, ActionSyncWorkFlowInfo.Wi wi ) throws Exception {
		String reportStatus = wi.getReportStatus();
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			
			Report_I_Base report = emc.find( reportId, Report_I_Base.class );
			Report_I_Detail detail = emc.find( reportId, Report_I_Detail.class );
			
			if( report != null ) {
				emc.beginTransaction( Report_I_Detail.class );
				emc.beginTransaction( Report_I_Base.class );
				emc.beginTransaction( Report_P_Permission.class );
				
				//更新report相关流程信息和状态信息
				report.setReportStatus( "审核中" );
				report.setWfProcessStatus( "流转中" );
				report.setWfProcessDescription( "" );
				
				//更新当前环节名称信息
				if( wi.getWork().getActivityName() != null ) {
					if( "结束".equals( wi.getWork().getActivityName() ) || "已完成".equals( wi.getWork().getActivityName() )) {
						report.setActivityName( "已完成" );
						report.setReportStatus( "已完成" );
						report.setWfProcessStatus( "已完成" );
					}else if( "拟稿".equals( wi.getWork().getActivityName() ) ){
						report.setActivityName( wi.getWork().getActivityName() );
						report.setReportStatus( "汇报者填写" );
					}else {
						report.setActivityName( wi.getWork().getActivityName() );
					}
				}else {
					report.setActivityName( "未知" );
					report.setWfProcessDescription( "未获取到流程环节信息" );
				}
				
				//更新JOBID
				if( wi.getWork() != null ) {
					report.setWf_JobId( wi.getWork().getJob() );
				}else {
					report.setWf_JobId( "未知" );
					report.setWfProcessDescription( "未获取到流程Work信息" );
				}
				
				//如果外部传入的汇报状态，以外部传入的数据为主
				if( reportStatus != null && !reportStatus.isEmpty() ) {
					report.setReportStatus( reportStatus );
				}
				
				//更新当前处理人信息
				StringBuffer sb_person = null;
				StringBuffer sb_identity = null;
				String person = null;
				
				//getManualTaskIdentityList 预测的处理人信息，目前这个事件中，待办信息仍未持久化到数据中
				//已完成的流程就不用去获取预测的处理人了
				if( !"已完成".equals( report.getReportStatus()) && ListTools.isNotEmpty( wi.getWork().getManualTaskIdentityList() ) ) {
					for( String identity : wi.getWork().getManualTaskIdentityList() ) {
						person = userManagerService.getPersonNameByIdentity( identity );
						if( sb_person == null ) {
							sb_person = new StringBuffer();
							sb_person.append( person );
						}else {
							sb_person.append( "," + person );
						}
						if( sb_identity == null ) {
							sb_identity = new StringBuffer();
							sb_identity.append( identity );
						}else {
							sb_identity.append( "," + identity );
						}
					}
				}
				
				//保存或者更新记录流转日志
				if( detail != null ) {
					detail.setWorkflowLog( gson.toJson( wi.getWorkLogList() ) );
					detail.setReportId( report.getId() );
					emc.check( report, CheckPersistType.all );
				}else {
					detail = new Report_I_Detail();
					detail.setTitle( report.getTitle() );
					detail.setWorkflowLog( gson.toJson( wi.getWorkLogList() ) );
					
					detail.setReportId( report.getId() );
					detail.setId( report.getId() );
					emc.persist( detail, CheckPersistType.all );
				}
				
				emc.check( report, CheckPersistType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listAllProcessingReportIds( Boolean listHiddenReport) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_QueryService.listAllProcessingReportIds( emc, listHiddenReport );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 保存工作汇报，需要做成一个事务
	 * @param report_I_Base
	 * @param report_I_Detail
	 * @param workPlanList
	 * @param workPlanDetailList
	 * @param workProgList
	 * @param workProgDetailList
	 * @param workPlanNextList
	 * @param workPlanDetailNextList
	 * @throws Exception
	 */
	public void save( Report_I_Base report_I_Base, Report_I_Detail report_I_Detail,
					 List<Report_C_WorkPlan> workPlanList, List<Report_C_WorkPlanDetail> workPlanDetailList,
					 List<Report_C_WorkProg> workProgList, List<Report_C_WorkProgDetail> workProgDetailList,
					 List<Report_C_WorkPlanNext> workPlanNextList, List<Report_C_WorkPlanNextDetail> workPlanDetailNextList ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			report_I_SaveService.save( emc, report_I_Base, report_I_Detail,
					workPlanList, workPlanDetailList, 
					workProgList, workProgDetailList,
					workPlanNextList, workPlanDetailNextList );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新汇报重点工作信息、汇报处理人信息、作者读者信息
	 * @param reportId
	 * @param propertyMap
	 */
	public void save(String reportId, List<Report_I_WorkInfo> workList, List<String> workreportPersonList, List<PermissionInfo> readerList, List<PermissionInfo> authorList ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			report_I_SaveService.save( emc, reportId, workList, workreportPersonList, readerList, authorList );
		} catch (Exception e) {
			throw e;
		}
	}

	public Report_I_Base save( Report_I_Base report_I_Base ) throws Exception {
		Report_I_Base report_S_Setting_old = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			report_S_Setting_old = emc.find( report_I_Base.getId(), Report_I_Base.class );
			if( report_S_Setting_old != null ){
				return report_I_SaveService.update( emc, report_I_Base, true );
			}else{
				return report_I_SaveService.create( emc, report_I_Base, true );
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	public List<String> listIdsWithYear(String year, Boolean listHiddenReport) throws Exception {
		if ( StringUtil.isEmpty(year) ) {
			throw new Exception("year is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return report_I_QueryService.listIdsWithYear(emc, year, listHiddenReport);
		} catch (Exception e) {
			throw e;
		}
	}
	public Report_I_Detail saveOpinion(Report_I_Base report_base, List<WiOpinion> opinions) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Report_I_Detail detail = null;
			List<Report_I_Detail> detailList = report_I_QueryService.listDetail(emc, report_base.getId() );
			if( ListTools.isNotEmpty( detailList ) ) {
				detail = detailList.get( 0 );
			}
			if( detail != null ) {
				emc.beginTransaction( Report_I_Detail.class );
				if( ListTools.isNotEmpty( opinions )) {
					//将意见列表转为Json
					detail.setOpinions(gson.toJson(opinions));
				}else {
					detail.setOpinions("{}");
				}
				
				detail.setId( report_base.getId() );
				detail.setReportId( report_base.getId() );
				emc.check( detail, CheckPersistType.all );
				emc.commit();
			}
			return detail;
		} catch ( Exception e ) {
			throw e;
		}
	}
	public List<String> listWithConditions( String year, String month, List<String> unitList, List<String> wfProcessStatus, 
			List<String> wfActivityNames, Boolean listHiddenReport ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return report_I_QueryService.listWithConditions( emc, year, month, unitList, wfProcessStatus, wfActivityNames, listHiddenReport );
		} catch ( Exception e ) {
			throw e;
		}
	}
	public List<String> listUnitNamesWithConditions(String year, String month, List<String> wfProcessStatus, 
			List<String> wfActivityNames, Boolean listHiddenReport ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return report_I_QueryService.listUnitNamesWithConditions( emc, year, month, wfProcessStatus, wfActivityNames, listHiddenReport );
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 修改指定的汇报信息状态
	 * @param reportId
	 * @param reportStatus
	 * @throws Exception
	 */
	public void modityReportStatus(String reportId, String reportStatus) throws Exception {
		if ( StringUtil.isEmpty( reportId ) ) {
			throw new Exception("reportId is empty.");
		}
		if ( StringUtil.isEmpty( reportStatus ) ) {
			reportStatus = "未知状态";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Report_I_Base report_I_Base = emc.find( reportId, Report_I_Base.class );
			emc.beginTransaction( Report_I_Base.class );
			report_I_Base.setReportStatus(reportStatus);
			emc.check( report_I_Base, CheckPersistType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}
	public boolean reportExists( String profileId, String unitName ) throws Exception {
		if ( StringUtil.isEmpty( profileId ) ) {
			throw new Exception("profileId is empty.");
		}
		if ( StringUtil.isEmpty( unitName ) ) {
			throw new Exception("unitName is empty.");
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			List<String> ids = business.report_I_BaseFactory().listIdsWithProfileIdAndUnitName(profileId, unitName );
			if( ListTools.isNotEmpty(ids)) {
				return true;
			}
		} catch ( Exception e ) {
			throw e;
		}
		return false;
	}
}
