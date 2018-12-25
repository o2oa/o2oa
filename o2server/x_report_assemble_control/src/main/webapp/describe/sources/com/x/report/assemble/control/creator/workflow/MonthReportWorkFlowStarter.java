package com.x.report.assemble.control.creator.workflow;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.dataadapter.workflow.WorkFlowStarter;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;
import com.x.report.assemble.control.service.Report_S_SettingServiceAdv;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_P_Profile;

public class MonthReportWorkFlowStarter {

	private static Logger logger = LoggerFactory.getLogger( MonthReportWorkFlowStarter.class );
	private Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	private Report_S_SettingServiceAdv report_S_SettingServiceAdv = new Report_S_SettingServiceAdv();
	

	/**
	 * 启动所有汇报流程
	 * 1、查询所有已经生成过的未启动流程的文档列表
	 * 2、遍历所有的生成的文档，发起流程
	 * 
	 * @param effectivePerson
	 * @param recordProfile
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	public Report_P_Profile startWorkFlow( EffectivePerson effectivePerson, Report_P_Profile recordProfile ) throws Exception {
		if( recordProfile == null ) {
			logger.info( "概要文件为空，无法搜索需要启动流程的汇报文档。" );
			return null;
		}
		
		String json = null;
		String wf_workId = null;
		String personReportMonthWorkFlowId = null;
		String unitReportMonthWorkFlowId = null;
		String processId = null;
		List<String> ids = null;
		Report_I_Base report = null;
		WorkFlowStarter workFlowStarter = new WorkFlowStarter();
		JsonParser jsonParser = new JsonParser();
		
		//说明有汇报文档需要启动流程，查询个人月度汇报绑定的流程
		personReportMonthWorkFlowId = report_S_SettingServiceAdv.getValueByCode( "PERSONMONTH_REPORT_WORKFLOW" );
		
		//说明有汇报文档需要启动流程，查询组织月度汇报绑定的流程
		unitReportMonthWorkFlowId = report_S_SettingServiceAdv.getValueByCode( "UNITMONTH_REPORT_WORKFLOW" );
		
		//查询指定的概要文件内，未启动流程的所有汇报文档ID列表
		ids = report_I_ServiceAdv.listIdsForStartWfInProfile( recordProfile.getId() );
		if( ids != null && !ids.isEmpty() ) {
			for( String id : ids ) {
				try {
					report = report_I_ServiceAdv.get( id );
					if( report != null ) {
						
						json = "{";
						json += "\"application\":\"工作汇报\",";
						if( "UNIT".equalsIgnoreCase( report.getReportObjType() )) {
							if( unitReportMonthWorkFlowId == null || unitReportMonthWorkFlowId.isEmpty() || "NONE".equalsIgnoreCase(unitReportMonthWorkFlowId)) {
								logger.warn( ">>>>>>>>>>组织月度汇报尚未绑定审批流程，请联系管理员先在系统配置中进行流程配置。" );
								return recordProfile;
							}
							processId = unitReportMonthWorkFlowId;
						}else {
							if( personReportMonthWorkFlowId == null || personReportMonthWorkFlowId.isEmpty() || "NONE".equalsIgnoreCase(personReportMonthWorkFlowId)) {
								logger.warn( ">>>>>>>>>>个人月度汇报尚未绑定审批流程，请联系管理员先在系统配置中进行流程配置。" );
								return recordProfile;
							}
							processId = personReportMonthWorkFlowId;
						}

						json += "\"process\":\""+ processId +"\",";
						json += "\"identity\":\""+report.getTargetIdentity() +"\",";
						json += "\"title\":\""+ report.getTitle() +"\",";
						json += "\"processing\":true,";
						json += "\"data\":{";
						json += 	"\"reportId\":\""+id+"\",";	
						json += 	"\"targetUnit\":\""+report.getTargetUnit()+"\"";	
						json +=   "}";
						json += "}";

						JsonElement data = jsonParser.parse( json ); // 将json字符串转换成JsonElement
						try {
							wf_workId = workFlowStarter.start( data );
							report_I_ServiceAdv.startWorkflowSuccess( id, processId, wf_workId );
							logger.info( ">>>>>>>>>>汇报文档'"+id+"'启动审批流程成功，WF_WORKID:" + wf_workId );
							Thread.sleep(100);
							recordProfile.setStartWorkflowCount( recordProfile.getStartWorkflowCount() + 1 );
						}catch( Exception e ) {
							report_I_ServiceAdv.startWorkflowError( id, processId );
							logger.info( ">>>>>>>>>>汇报文档'"+id+"'启动审批流程失败。PROCESS：" + processId );
							e.printStackTrace();
						}
					}else {
						logger.warn( ">>>>>>>>>>汇报文档'"+id+"'不存在，无法启动流程。" );
					}
				}catch( Exception e ) {
					e.printStackTrace();
				}
			}
			return recordProfile;
		}else {
			logger.info( ">>>>>>>>>>未搜索到任何需要启动流程的汇报文档。" );
		}
		return recordProfile;
	}	
}
