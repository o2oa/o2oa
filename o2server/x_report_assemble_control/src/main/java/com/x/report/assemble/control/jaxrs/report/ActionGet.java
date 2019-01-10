package com.x.report.assemble.control.jaxrs.report;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure;
import com.x.report.assemble.control.jaxrs.report.element.PermissionInfo;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionQueryWithReportId;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportIdEmpty;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionReportNotExists;
import com.x.report.assemble.control.service.WorkConfigUtilService;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Detail;
import com.x.report.core.entity.Report_I_Ext_Content;
import com.x.report.core.entity.Report_I_Ext_ContentDetail;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;

/**
 * 根据ID获取指定的汇报完整信息
 * @author O2LEE
 *
 */
public class ActionGet extends BaseAction {

	private Gson gson = XGsonBuilder.instance();
	private static Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String reportId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Report_I_Base report_base = null;
		Boolean check = true;

		if( reportId == null || reportId.isEmpty() ){
			check = false;
			Exception exception = new ExceptionReportIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				report_base = report_I_ServiceAdv.get( reportId );
				if( report_base == null ) {
					check = false;
					Exception exception = new ExceptionReportNotExists( reportId );
					result.error( exception );
				}else {
					wrap = Wo.copier.copy( report_base );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionQueryWithReportId( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//查询汇报详细信息
			AtomicReference<Report_I_Detail> report_I_Detail = new AtomicReference<>();
			report_I_Detail.set(report_I_ServiceAdv.getDetail(reportId));
			if( report_I_Detail.get() != null ) {
				WoReportDetail woDetail = WoReportDetail.copier.copy(report_I_Detail.get());
				List<PermissionInfo> readerList, authorList;
				
				//将read和author转为对象列表
				Type type = new TypeToken<ArrayList<PermissionInfo>>() {}.getType();
				if( StringUtils.isNotEmpty(report_I_Detail.get().getReaders()) && report_I_Detail.get().getReaders().length() > 10 ) {
					readerList = gson.fromJson( report_I_Detail.get().getReaders(), type );
					woDetail.setReaders( null );// JSON内容不输出，只输出转换过的LIST
					wrap.setReaderList( readerList );
				}
				
				if( StringUtils.isNotEmpty(report_I_Detail.get().getAuthors()) && report_I_Detail.get().getAuthors().length() > 10  ) {
					authorList = gson.fromJson( report_I_Detail.get().getAuthors(), type );
					woDetail.setAuthors( null );// JSON内容不输出，只输出转换过的LIST
					wrap.setAuthorList( authorList );
				}
				
				wrap.setDetail( woDetail );
			}
		}
		if( check ) {
			List<CompanyStrategyMeasure.WoMeasuresInfo> measureInfoList_thisMonth =  new WorkConfigUtilService().getMeasureInfoWithUnit_thisMonth( report_base.getProfileId(),  report_base.getTargetUnit() );
			wrap.setSelectableMeasures( measureInfoList_thisMonth );
			
			List<CompanyStrategyMeasure.WoMeasuresInfo> measureInfoList_nextMonth =  new WorkConfigUtilService().getMeasureInfoWithUnit_nextMonth( report_base.getProfileId(),  report_base.getTargetUnit() );
			if( ListTools.isNotEmpty( measureInfoList_nextMonth )) {
				wrap.setNextMonth_selectableMeasures( measureInfoList_nextMonth );
			}else {
				wrap.setNextMonth_selectableMeasures( measureInfoList_thisMonth );
			}
		}
		//THISMONTH
		//获取所有的工作和举措关联信息, 组织工作所有的工作计划，完成情况，下周期工作计划信息一并输出
		if( check ) {
			Report_I_WorkInfoDetail workInfoDetail = null;
			List<WoReport_I_WorkInfo> woWorkInfoList;
			List<Report_I_WorkInfo> workInfoList;
			List<Report_C_WorkPlan> planList = null;
			List<Report_C_WorkProg> progList = null;
			
			//当月工作信息、计划、工作总结
			List<String> ids = report_I_WorkInfoServiceAdv.listIdsWithReport(reportId, "THISMONTH");
			if (ListTools.isNotEmpty(ids)) {
				workInfoList = report_I_WorkInfoServiceAdv.list(ids);
				if (ListTools.isNotEmpty(workInfoList)) {
					woWorkInfoList = WoReport_I_WorkInfo.copier.copy(workInfoList);
					
					for (WoReport_I_WorkInfo work : woWorkInfoList) {
						//查询工作的详细说明						
						workInfoDetail = report_I_WorkInfoServiceAdv.getDetailWithWorkInfoId(reportId, work.getId());
						if( workInfoDetail != null ) {
							work.setWorkDescribe(workInfoDetail.getDescribe());
							work.setWorkPlanSummary(workInfoDetail.getWorkPlanSummary());
							work.setWorkProgSummary(workInfoDetail.getWorkProgSummary());
						}
						//查询该工作中所有人的计划列表
						ids = report_C_WorkPlanServiceAdv.listWithReportAndWorkInfoId( reportId, work.getId() );
						if (ListTools.isNotEmpty(ids)) {
							planList = report_C_WorkPlanServiceAdv.list(ids);
							if( ListTools.isNotEmpty(planList) ) {
								work.setPlanList( WoReport_C_WorkPlan.copier.copy(planList));
								for( WoReport_C_WorkPlan plan : work.getPlanList() ) {
									plan.setPlanContent( report_C_WorkPlanServiceAdv.getPlanContentWithPlanId(plan.getId()));
								}
							}
						}
						
						//查询该工作中所有人的工作总结列表
						ids = report_C_WorkProgServiceAdv.listWithReportAndWorkInfoId( reportId, work.getId() );
						if (ListTools.isNotEmpty(ids)) {
							progList = report_C_WorkProgServiceAdv.list(ids);
							if( ListTools.isNotEmpty(progList) ) {
								work.setProgList( WoReport_C_WorkProg.copier.copy(progList));
								for( WoReport_C_WorkProg prog : work.getProgList() ) {
									prog.setProgressContent( report_C_WorkProgServiceAdv.getProgressContentWithProgId( prog.getId() ));
								}
							}
						}
					}
					
					SortTools.asc( woWorkInfoList, "orderNumber");
					wrap.setThisMonth_workList(woWorkInfoList);
				}
			}
		}
		
		//NEXTMONTH
		//获取所有的工作和举措关联信息, 组织工作所有的工作计划，完成情况，下周期工作计划信息一并输出
		if( check ) {
			Report_I_WorkInfoDetail workInfoDetail = null;
			List<WoReport_I_WorkInfo> woWorkInfoList;
			List<Report_I_WorkInfo> workInfoList;
			List<Report_C_WorkPlanNext> planNextList = null; 
			//当月工作信息、计划、工作总结
			List<String> ids = report_I_WorkInfoServiceAdv.listIdsWithReport(reportId, "NEXTMONTH");
			if (ListTools.isNotEmpty(ids)) {
				workInfoList = report_I_WorkInfoServiceAdv.list(ids);
				if (ListTools.isNotEmpty(workInfoList)) {
					woWorkInfoList = WoReport_I_WorkInfo.copier.copy(workInfoList);
					for (WoReport_I_WorkInfo work : woWorkInfoList) {
						//查询工作的详细说明
						workInfoDetail = report_I_WorkInfoServiceAdv.getDetailWithWorkInfoId(reportId, work.getId());
						if( workInfoDetail != null ) {
							work.setWorkDescribe(workInfoDetail.getDescribe());
							work.setWorkPlanSummary(workInfoDetail.getWorkPlanSummary());
							work.setWorkProgSummary(workInfoDetail.getWorkProgSummary());
						}
						//查询该工作中所有人的下月计划列表
						ids = report_C_WorkPlanNextServiceAdv.listWithReportAndWorkInfoId( reportId, work.getId() );
						if (ListTools.isNotEmpty(ids)) {
							planNextList = report_C_WorkPlanNextServiceAdv.list(ids);
							if( ListTools.isNotEmpty(planNextList) ) {
								work.setPlanNextList( WoReport_C_WorkPlanNext.copier.copy(planNextList));
								for( WoReport_C_WorkPlanNext plan : work.getPlanNextList() ) {
									plan.setPlanContent( report_C_WorkPlanNextServiceAdv.getPlanContentWithPlanId(plan.getId()));
								}
							}
						}
					}
					
					SortTools.asc( woWorkInfoList, "orderNumber");
					wrap.setNextMonth_workList( woWorkInfoList );
				}
			}
		}
		
		//获取员工填写的汇报扩展信息
		if( check ) {
			List<Report_I_Ext_Content>  contentList = report_I_Ext_ContentServiceAdv.listWithReportId(reportId, "员工", null);
			if(ListTools.isNotEmpty( contentList )) {
				List<WoReport_I_Ext_Content>  woContentList = WoReport_I_Ext_Content.copier.copy(contentList);
				List<Report_I_Ext_ContentDetail>   detailList = null;
				for( WoReport_I_Ext_Content woContent : woContentList ) {
					//查询该分类下所有填写的信息列表
					detailList = report_I_Ext_ContentServiceAdv.listDetailWithContentId(woContent.getId());
					if(ListTools.isNotEmpty( detailList )) {
						for( Report_I_Ext_ContentDetail detail : detailList ) {
							if("员工关爱".equals(detail.getContentType())) {
								woContent.setGuanai(detail.getContent());
							}
							if("服务客户".equals(detail.getContentType())) {
								woContent.setFuwu(detail.getContent());
							}
							if("意见建议".equals(detail.getContentType())) {
								woContent.setYijian( detail.getContent() );
							}
						}
					}
				}
				SortTools.asc( woContentList, "orderNumber");
				wrap.setWoReport_I_Ext_Contents( woContentList );
			}
		}
		
		//获取汇报的扩展信息总结
		if( check ) {
			List<Report_I_Ext_Content>  contentList = report_I_Ext_ContentServiceAdv.listWithReportId(reportId, "汇总", null);
			if(ListTools.isNotEmpty( contentList )) {
				List<WoReport_I_Ext_Content>  woContentList = WoReport_I_Ext_Content.copier.copy(contentList);
				List<Report_I_Ext_ContentDetail>   detailList = null;
				for( WoReport_I_Ext_Content woContent : woContentList ) {
					//查询该分类下所有填写的信息列表
					detailList = report_I_Ext_ContentServiceAdv.listDetailWithContentId(woContent.getId());
					if(ListTools.isNotEmpty( detailList )) {
						for( Report_I_Ext_ContentDetail detail : detailList ) {
							if("员工关爱".equals( detail.getContentType()) ) {
								woContent.setGuanai( detail.getContent() );
							}
							if("服务客户".equals( detail.getContentType()) ) {
								woContent.setFuwu( detail.getContent() );
							}
							if("意见建议".equals( detail.getContentType()) ) {
								woContent.setYijian( detail.getContent() );
							}
						}
					}
				}
				
				SortTools.asc( woContentList, "orderNumber");
				wrap.setWoReport_I_Ext_Contents_sumamry(woContentList);
			}
		}
		
		//获取上个月汇报的扩展信息总结
		if( check && StringUtils.isNotEmpty( report_base.getLastReportId() ) ) {
			List<Report_I_Ext_Content>  contentList = report_I_Ext_ContentServiceAdv.listWithReportId(report_base.getLastReportId(), "汇总", null);
			if(ListTools.isNotEmpty( contentList )) {
				List<WoReport_I_Ext_Content>  woContentList = WoReport_I_Ext_Content.copier.copy(contentList);
				List<Report_I_Ext_ContentDetail>   detailList = null;
				for( WoReport_I_Ext_Content woContent : woContentList ) {
					//查询该分类下所有填写的信息列表
					detailList = report_I_Ext_ContentServiceAdv.listDetailWithContentId(woContent.getId());
					if(ListTools.isNotEmpty( detailList )) {
						for( Report_I_Ext_ContentDetail detail : detailList ) {
							if("员工关爱".equals(detail.getContentType())) {
								woContent.setGuanai(detail.getContent());
							}
							if("服务客户".equals(detail.getContentType())) {
								woContent.setFuwu(detail.getContent());
							}
							if("意见建议".equals(detail.getContentType())) {
								woContent.setYijian(detail.getContent());
							}
						}
					}
				}
				SortTools.asc( woContentList, "orderNumber");
				wrap.setWoReport_I_Ext_Contents_sumamry_lastMonth(woContentList);
			}
		}	
		
		result.setData( wrap );
		return result;
	}

	public static class Wo extends Report_I_Base  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<>();

		@FieldDescribe("组织关联当月的全部举措信息列表")
		private List<CompanyStrategyMeasure.WoMeasuresInfo> selectableMeasures = null;
		
		@FieldDescribe("组织关联下月的全部举措信息列表")
		private List<CompanyStrategyMeasure.WoMeasuresInfo> nextMonth_selectableMeasures = null;
		
		@FieldDescribe("本月重点工作列表")
		private List<WoReport_I_WorkInfo>thisMonth_workList = null;
		
		@FieldDescribe("下月重点工作列表")
		private List<WoReport_I_WorkInfo>nextMonth_workList = null;

		@FieldDescribe("汇报权限读者列表(PermissionInfo)")
		private List<PermissionInfo> readerList = null;

		@FieldDescribe("汇报作者权限列表(PermissionInfo)")
		private List<PermissionInfo> authorList = null;

		@FieldDescribe("汇报详细内容")
		private WoReportDetail detail = null;
		
		@FieldDescribe("员工汇报扩展信息")
		List<WoReport_I_Ext_Content>  WoReport_I_Ext_Contents = null;
		
		@FieldDescribe("汇报扩展信息汇总")
		List<WoReport_I_Ext_Content>  WoReport_I_Ext_Contents_sumamry = null;
		
		@FieldDescribe("上个月汇报扩展信息汇总")
		List<WoReport_I_Ext_Content>  WoReport_I_Ext_Contents_sumamry_lastMonth = null;

		public static WrapCopier<Report_I_Base, Wo> copier = WrapCopierFactory.wo( Report_I_Base.class, Wo.class, null,Wo.Excludes);

		public WoReportDetail getDetail() {
			return detail;
		}
		public void setDetail(WoReportDetail detail) {
			this.detail = detail;
		}
		public List<PermissionInfo> getReaderList() { return readerList; }
		public void setReaderList(List<PermissionInfo> readerList) {
			this.readerList = readerList;
		}
		public List<PermissionInfo> getAuthorList() {
			return authorList;
		}
		public void setAuthorList(List<PermissionInfo> authorList) {
			this.authorList = authorList;
		}
		public List<WoReport_I_WorkInfo> getThisMonth_workList() {
			return thisMonth_workList;
		}
		public List<WoReport_I_WorkInfo> getNextMonth_workList() {
			return nextMonth_workList;
		}
		public void setThisMonth_workList(List<WoReport_I_WorkInfo> thisMonth_workList) {
			this.thisMonth_workList = thisMonth_workList;
		}
		public void setNextMonth_workList(List<WoReport_I_WorkInfo> nextMonth_workList) {
			this.nextMonth_workList = nextMonth_workList;
		}
		public List<CompanyStrategyMeasure.WoMeasuresInfo> getSelectableMeasures() {
			return selectableMeasures;
		}		
		public List<CompanyStrategyMeasure.WoMeasuresInfo> getNextMonth_selectableMeasures() {
			return nextMonth_selectableMeasures;
		}
		public void setNextMonth_selectableMeasures(List<CompanyStrategyMeasure.WoMeasuresInfo> nextMonth_selectableMeasures) {
			this.nextMonth_selectableMeasures = nextMonth_selectableMeasures;
		}
		public void setSelectableMeasures(List<CompanyStrategyMeasure.WoMeasuresInfo> selectableMeasures) {
			this.selectableMeasures = selectableMeasures;
		}
		public List<WoReport_I_Ext_Content> getWoReport_I_Ext_Contents() {
			return WoReport_I_Ext_Contents;
		}
		public void setWoReport_I_Ext_Contents(List<WoReport_I_Ext_Content> woReport_I_Ext_Contents) {
			WoReport_I_Ext_Contents = woReport_I_Ext_Contents;
		}
		public List<WoReport_I_Ext_Content> getWoReport_I_Ext_Contents_sumamry() {
			return WoReport_I_Ext_Contents_sumamry;
		}
		public void setWoReport_I_Ext_Contents_sumamry(List<WoReport_I_Ext_Content> woReport_I_Ext_Contents_sumamry) {
			WoReport_I_Ext_Contents_sumamry = woReport_I_Ext_Contents_sumamry;
		}
		public List<WoReport_I_Ext_Content> getWoReport_I_Ext_Contents_sumamry_lastMonth() {
			return WoReport_I_Ext_Contents_sumamry_lastMonth;
		}
		public void setWoReport_I_Ext_Contents_sumamry_lastMonth(
				List<WoReport_I_Ext_Content> woReport_I_Ext_Contents_sumamry_lastMonth) {
			WoReport_I_Ext_Contents_sumamry_lastMonth = woReport_I_Ext_Contents_sumamry_lastMonth;
		}
	}

	public static class WoReportDetail extends Report_I_Detail  {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<>();
		public static WrapCopier<Report_I_Detail, WoReportDetail> copier = WrapCopierFactory.wo( Report_I_Detail.class, WoReportDetail.class, null,WoReportDetail.Excludes);
	}

	public static class WoReport_I_WorkInfo extends Report_I_WorkInfo  {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<>();
		
		@FieldDescribe("当月计划列表")
		private List<WoReport_C_WorkPlan> planList = null;
		
		@FieldDescribe("工作总结列表")
		private List<WoReport_C_WorkProg> progList = null;
		
		@FieldDescribe("下月计划列表")
		private List<WoReport_C_WorkPlanNext> planNextList = null;
		
		@FieldDescribe("工作详细说明")
		private String workDescribe = null;
		
		@FieldDescribe("工作完成情况总结")
		private String workProgSummary = null;
		
		@FieldDescribe("工作计划汇总")
		private String workPlanSummary = null;
		
		public static WrapCopier<Report_I_WorkInfo, WoReport_I_WorkInfo> copier = WrapCopierFactory.wo( Report_I_WorkInfo.class, WoReport_I_WorkInfo.class, null, WoReport_I_WorkInfo.Excludes);
		
		public List<WoReport_C_WorkPlan> getPlanList() {
			return planList;
		}
		public List<WoReport_C_WorkProg> getProgList() {
			return progList;
		}
		public void setPlanList(List<WoReport_C_WorkPlan> planList) {
			this.planList = planList;
		}
		public void setProgList(List<WoReport_C_WorkProg> progList) {
			this.progList = progList;
		}
		public String getWorkDescribe() {
			return workDescribe;
		}
		public void setWorkDescribe(String workDescribe) {
			this.workDescribe = workDescribe;
		}
		public List<WoReport_C_WorkPlanNext> getPlanNextList() {
			return planNextList;
		}
		public void setPlanNextList(List<WoReport_C_WorkPlanNext> planNextList) {
			this.planNextList = planNextList;
		}
		public String getWorkProgSummary() {
			return workProgSummary;
		}
		public String getWorkPlanSummary() {
			return workPlanSummary;
		}
		public void setWorkProgSummary(String workProgSummary) {
			this.workProgSummary = workProgSummary;
		}
		public void setWorkPlanSummary(String workPlanSummary) {
			this.workPlanSummary = workPlanSummary;
		}
	}
	
	public static class WoReport_C_WorkPlan extends Report_C_WorkPlan  {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<>();
		
		@FieldDescribe( "计划工作内容概述" )
		private String planContent = "";
		
		public static WrapCopier<Report_C_WorkPlan, WoReport_C_WorkPlan> copier = WrapCopierFactory.wo( Report_C_WorkPlan.class, WoReport_C_WorkPlan.class, null,WoReport_C_WorkPlan.Excludes);

		public String getPlanContent() {
			return planContent;
		}

		public void setPlanContent(String planContent) {
			this.planContent = planContent;
		}		
	}
	
	public static class WoReport_C_WorkProg extends Report_C_WorkProg  {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<>();
		
		@FieldDescribe( "工作进展总结" )
		private String progressContent = "";
		
		public static WrapCopier<Report_C_WorkProg, WoReport_C_WorkProg> copier = WrapCopierFactory.wo( Report_C_WorkProg.class, WoReport_C_WorkProg.class, null,WoReport_C_WorkProg.Excludes);

		public String getProgressContent() {
			return progressContent;
		}

		public void setProgressContent(String progressContent) {
			this.progressContent = progressContent;
		}
	}
	
	public static class WoReport_C_WorkPlanNext extends Report_C_WorkPlanNext  {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<>();
		
		@FieldDescribe( "计划工作内容概述" )
		private String planContent = "";
		
		public static WrapCopier<Report_C_WorkPlanNext, WoReport_C_WorkPlanNext> copier = WrapCopierFactory.wo( Report_C_WorkPlanNext.class, WoReport_C_WorkPlanNext.class, null,WoReport_C_WorkPlanNext.Excludes);
		
		public String getPlanContent() {
			return planContent;
		}

		public void setPlanContent(String planContent) {
			this.planContent = planContent;
		}
	}
	
	public static class WoReport_I_Ext_Content extends Report_I_Ext_Content  {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<>();
		public static WrapCopier<Report_I_Ext_Content , WoReport_I_Ext_Content> copier =  WrapCopierFactory.wo( Report_I_Ext_Content .class, WoReport_I_Ext_Content.class, null,WoReport_I_Ext_Content.Excludes);
		@FieldDescribe("员工关爱具体内容")
		private String guanai="";
		
		@FieldDescribe("服务客户具体内容")
		private String fuwu="";
		
		@FieldDescribe("意见建议具体内容")
		private String yijian="";

		public String getGuanai() {
			return guanai;
		}

		public String getFuwu() {
			return fuwu;
		}

		public String getYijian() {
			return yijian;
		}

		public void setGuanai(String guanai) {
			this.guanai = guanai;
		}

		public void setFuwu(String fuwu) {
			this.fuwu = fuwu;
		}

		public void setYijian(String yijian) {
			this.yijian = yijian;
		}
	}
}
	