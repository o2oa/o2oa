package com.x.okr.assemble.control.jaxrs.workimport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.common.excel.reader.IRowReader;
import com.x.okr.assemble.control.ThisApplication;
import org.apache.commons.lang3.StringUtils;

public class WorkImportExcelReader implements IRowReader{
	
	/* 业务逻辑实现方法
	 * @see com.eprosun.util.excel.IRowReader#getRows(int, int, java.util.List)
	 */
	public void getRows( int sheetIndex, int curRow, List<String> colmlist, String fileKey, int startRow ) {
		StringBuffer sb = new StringBuffer();
		for( String col : colmlist ){
			if( col == null ){
				col = "null";
			}
			if( sb.toString().isEmpty() ){
				sb.append( "["+ col + "]" );
			}else{
				sb.append( ", [" + col + "]" );
			}
		}
		
		if( curRow < startRow ){
			return;
		}
		
		CacheImportFileStatus cacheImportFileStatus = getCacheMap( fileKey );
		
		if( cacheImportFileStatus.getDetailList() == null ){
			cacheImportFileStatus.setDetailList( new ArrayList<CacheImportRowDetail>());
		}
		
		List<CacheImportRowDetail> workDataList =  cacheImportFileStatus.getDetailList();
		
		if( colmlist!= null && colmlist.size() > 0 ){
			
			cacheImportFileStatus.setRowCount( curRow );
			
//			String title = ""; //工作标题
			//String parentWorkId = ""; //上级工作ID
			//String deployerIdentity = "";//部署者身份
			//String creatorIdentity = "";//创建者身份
			//工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）
			String workDateTimeType = "长期工作";
			String completeDateLimitStr = ""; //工作完成日期-字符串，显示用：yyyy-mm-dd
			String responsibilityIdentity = ""; //主责人身份
			String cooperateIdentity = "";//协助人身份，可能多值，用逗号分隔
			String readLeaderIdentity = ""; //阅知领导身份，可能多值，用逗号分隔
//			String workType = ""; //工作类别
//			String workLevel = "";//工作级别
			String workDetail = ""; //工作详细描述(山西：事项分解及描述)
//			String dutyDescription = "";//职责描述
			String progressAction = "";//具体行动举措
			String landmarkDescription = "";//里程碑标志说明
			String resultDescription = "";//交付成果说明
//			String majorIssuesDescription = "";//重点事项说明
			String progressPlan = "";//进展计划时限说明
			String reportCycle = null;//汇报周期:不需要汇报|每月汇报|每周汇报
			//周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00
			String reportDayInCycleStr = null;
			Date completeDateLimit = null;
			boolean checkSuccess = true;			
			DateOperation dateOperation = new DateOperation();			
			CacheImportRowDetail cacheImportRowDetail = new CacheImportRowDetail();			
			if( !colmlist.get(0).isEmpty() && !colmlist.get(2).isEmpty()){				
				workDateTimeType = "长期工作";  //工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）
				cacheImportRowDetail.setWorkDateTimeType(workDateTimeType);
				if(ListTools.isNotEmpty(colmlist) ){
					completeDateLimitStr = colmlist.get(0).trim();     //工作完成时限-字符串，显示用：yyyy-mm-dd
					try{
						completeDateLimit = dateOperation.getDateFromString( completeDateLimitStr );
						cacheImportRowDetail.setCompleteDateLimit(completeDateLimit);
						completeDateLimitStr = dateOperation.getDateStringFromDate( completeDateLimit,  "yyyy-MM-dd" );
						cacheImportRowDetail.setCompleteDateLimitStr(completeDateLimitStr);
					}catch(Exception e){
						checkSuccess = false;
						cacheImportRowDetail.setDescription( "工作完成时限不是正常的日期格式：" + completeDateLimitStr );
					}
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 1 ){
					reportCycle = colmlist.get(1).trim();              //汇报周期:不需要汇报|每月汇报|每周汇报
					cacheImportRowDetail.setReportCycle(reportCycle);
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 2 ){
					reportDayInCycleStr = colmlist.get(2).trim();      //周期汇报时间：每月的几号(1-31)，每周的星期几(1-7)，启动时间由系统配置设定，比如：10:00
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 3 ){
					responsibilityIdentity = colmlist.get(3).trim();   //主责人身份
					cacheImportRowDetail.setResponsibilityIdentity(responsibilityIdentity);
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 4 ){
					cooperateIdentity = colmlist.get(4).trim();        //协助人身份，可能多值，用逗号分隔
					cacheImportRowDetail.setCooperateIdentity(cooperateIdentity);
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 5 ){
					readLeaderIdentity = colmlist.get(5).trim();       //阅知领导身份，可能多值，用逗号分隔
					cacheImportRowDetail.setReadLeaderIdentity(readLeaderIdentity);
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 6 ){
					workDetail = colmlist.get(6).trim();               //工作详细描述（山西：事项分解及描述）
					cacheImportRowDetail.setWorkDetail(workDetail);
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 7 ){
					progressAction = colmlist.get(7).trim();           //具体行动举措
					cacheImportRowDetail.setProgressAction(progressAction);
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 8 ){
					landmarkDescription = colmlist.get(8).trim();     //里程碑标志说明（山西：预期里程碑(阶段性)结果标志）
					cacheImportRowDetail.setLandmarkDescription(landmarkDescription);
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 9 ){
					progressPlan = colmlist.get(9).trim();            //进展计划时限说明
					cacheImportRowDetail.setProgressPlan(progressPlan);
				}
				if( ListTools.isNotEmpty(colmlist) && colmlist.size() > 10 ){
					resultDescription = colmlist.get(10).trim();       //交付成果说明
					cacheImportRowDetail.setResultDescription(resultDescription);
				}
				
				//检查所有导入的参数的合法性
				if( checkSuccess && (StringUtils.isEmpty(completeDateLimitStr)) ){
					checkSuccess = false;
					cacheImportRowDetail.setDescription( "工作完成时限不能为空！" );
				}
				if( checkSuccess && ( StringUtils.isEmpty(reportCycle) ) ){
					checkSuccess = false;
					cacheImportRowDetail.setDescription( "工作汇报周期不能为空！" );
				}
				if( checkSuccess && ( StringUtils.isEmpty( reportDayInCycleStr ) ) ){
					checkSuccess = false;
					cacheImportRowDetail.setDescription( "工作汇报日期不能为空！" );
				}
				if( checkSuccess && ( StringUtils.isEmpty( responsibilityIdentity ) ) ){
					checkSuccess = false;
					cacheImportRowDetail.setDescription( "工作负责人身份不能为空！" );
				}
				if( checkSuccess && ( StringUtils.isEmpty( workDetail ) ) ){
					checkSuccess = false;
					cacheImportRowDetail.setDescription( "事项分解及描述不能为空！" );
				}
				if( checkSuccess && ( StringUtils.isEmpty( progressAction ) ) ){
					progressAction = "暂无具体行动举措。";
				}
				
				//检查部分参数的有效性
				if( checkSuccess ){
					try{
						cacheImportRowDetail.setReportDayInCycle(Integer.parseInt( reportDayInCycleStr ));
					}catch(Exception e){
						checkSuccess = false;
						cacheImportRowDetail.setDescription( "工作汇报日期不是数字：" + reportDayInCycleStr );
					}
				}
				
				if( checkSuccess ){
					if( "每月汇报".equals( reportCycle )){
						if( cacheImportRowDetail.getReportDayInCycle() < 1 || cacheImportRowDetail.getReportDayInCycle() > 31 ){
							checkSuccess = false;
							cacheImportRowDetail.setDescription( "每月工作汇报日期不正常：" + cacheImportRowDetail.getReportDayInCycle());
						}
					}else if( "每周汇报".equals( reportCycle )){
						if( cacheImportRowDetail.getReportDayInCycle() < 1 || cacheImportRowDetail.getReportDayInCycle() > 7 ){
							checkSuccess = false;
							cacheImportRowDetail.setDescription( "每周工作汇报日期不正常：" + cacheImportRowDetail.getReportDayInCycle());
						}
						//用户填写的是 1-周一， 7-周日
						//实际换算，2-周一，3-周二，4-周三，5-周四，6-周五，7-周六，1-周日
						if( cacheImportRowDetail.getReportDayInCycle() == 1 ){//周一
							cacheImportRowDetail.setReportDayInCycle(2);
						}else if( cacheImportRowDetail.getReportDayInCycle() == 2 ){//周二
							cacheImportRowDetail.setReportDayInCycle(3);
						}else if( cacheImportRowDetail.getReportDayInCycle() == 3 ){//周三
							cacheImportRowDetail.setReportDayInCycle(4);
						}else if( cacheImportRowDetail.getReportDayInCycle() == 4 ){//周四
							cacheImportRowDetail.setReportDayInCycle(5);
						}else if( cacheImportRowDetail.getReportDayInCycle() == 5 ){//周五
							cacheImportRowDetail.setReportDayInCycle(6);
						}else if( cacheImportRowDetail.getReportDayInCycle() == 6 ){//周六
							cacheImportRowDetail.setReportDayInCycle(7);
						}else if( cacheImportRowDetail.getReportDayInCycle() == 7 ){//周日
							cacheImportRowDetail.setReportDayInCycle(1);
						}
					}else{
						checkSuccess = false;
						cacheImportRowDetail.setDescription( "工作汇报周期不正常：" + reportCycle );
					}
				}
				if( checkSuccess ){
					cacheImportRowDetail.setCheckStatus( "success" );         //设置数据检查状态为正常
				}else{
					cacheImportFileStatus.setErrorCount( ( cacheImportFileStatus.getErrorCount() + 1 ) );
					cacheImportRowDetail.setCheckStatus( "failture" );         //设置数据检查状态为正常
				}
				workDataList.add( cacheImportRowDetail );
			}
		}
	}

	private CacheImportFileStatus getCacheMap( String fileKey ) {		
		Map<String, CacheImportFileStatus> cacheMap = null;		
		if ( ThisApplication.getImportFileStatusMap() ==  null ) {
			ThisApplication.setImportFileStatusMap( new HashMap<String, CacheImportFileStatus>() );
		}
		cacheMap = ThisApplication.getImportFileStatusMap();		
		if( cacheMap.get( fileKey ) == null ){
			cacheMap.put( fileKey, new CacheImportFileStatus() );
		}		
		return cacheMap.get( fileKey );
	}
}
