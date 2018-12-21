package com.x.report.assemble.control.jaxrs.reportstat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_P_MeasureInfo;

/**
 * 根据ID获取指定的汇报完整信息
 * @author O2LEE
 *
 */
public class ActionStatUnitWorkReport extends BaseAction {
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String year ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wo wrap = null;
		List<String> ids = null;
		List<Report_I_Base> report_list = null;
		Boolean check = true;

		if( StringUtils.isEmpty( year ) ){
			year = dateOperation.getYear( new Date() );
		}
		
		//获取当年所有的月度汇报信息
		if( check ){
			ids = report_I_ServiceAdv.listIdsWithYear( year, false  );
			if( ListTools.isNotEmpty(ids)) {
				report_list = report_I_ServiceAdv.listWithIds(ids);
			}
			if( ListTools.isEmpty(report_list)) { //没有汇报信息，直接返回
				return result;
			}
		}
		
		//遍历汇报信息，获取所有的组织名称，并且按组织名称归类到map里
		if( check ){
			List<Wo_StatWork> works = null;
			for( Report_I_Base report : report_list ) {
				wrap = findWrapInListWithUnitName( report.getTargetUnit(), wos );
				if( wrap == null ) {
					wrap = new Wo();
					wrap.setUnitName(report.getTargetUnit() );
					wrap.setWorkTotal( 0 );
					wrap.setWorks( new ArrayList<>());
					wos.add( wrap );
				}
				
				//组织该部门所有的工作的完成情况列表
				works = composeWorkInfo( report, wrap.getWorks(), "THISMONTH" );
				
				if( ListTools.isNotEmpty( works )) {
					wrap.setWorkTotal( works.size() );
					wrap.setWorks( works );
				}
			}
		}
		result.setData( wos );
		return result;
	}

	/**
	 * 根据汇报查询所有的工作以及已经组织过的完成情况，新增其他月份完成情况信息。
	 * @param report
	 * @param list 
	 * @return
	 * @throws Exception 
	 */
	private List<Wo_StatWork> composeWorkInfo( Report_I_Base report, List<Wo_StatWork> stat_works, String workMonthFlag  ) throws Exception {
		//查询该汇报所有的工作情况
		Wo_StatWork stat_work = null;
		List<Report_I_WorkInfo> works = null;
		List<String> ids = report_I_WorkInfoServiceAdv.listIdsWithReport( report.getId(), workMonthFlag );		
		List<Wo_StatMeasure> measures = null;
		
		if( ListTools.isNotEmpty( ids )) {
			works = report_I_WorkInfoServiceAdv.list(ids);
		}
		if( ListTools.isNotEmpty( works )) {
			for( Report_I_WorkInfo work : works ) {
				stat_work = findWorkInListWithWorkId( work.getId(), stat_works );
				if( stat_work == null ) {
					stat_work = new Wo_StatWork();
					stat_work.setMeasures(new ArrayList<>());
					stat_work.setMeasureTotal(0);
					stat_work.setWorkId( work.getKeyWorkId() );
					stat_work.setWorkName( work.getWorkTitle() );
					stat_works.add( stat_work );
				}
				
				//组织该工作该月的完成情况信息
				measures = composeMesureInfo( report, work.getMeasuresList(), stat_work );
				
				if( ListTools.isNotEmpty( measures )) {
					stat_work.setMeasures(measures);
					stat_work.setMeasureTotal(measures.size());
				}
			}
		}		
		return stat_works;
	}

	

	/**
	 * 组织举措统计列表
	 * @param report 汇报信息
	 * @param measureIds 此次汇报该工作涉及的举措ID列表
	 * @param stat_work  工作统计对象
	 * @return
	 * @throws Exception 
	 */
	private List<Wo_StatMeasure> composeMesureInfo(Report_I_Base report, List<String> measureIds,Wo_StatWork stat_work) throws Exception {
		Wo_StatMeasure stat_measure = null;
		List<String> ids = null;
		Wo_ProgMonth progMonth = null;
		List<Report_C_WorkProg> progList = null;
		List<Report_P_MeasureInfo> measure_list = null;
		List<Wo_StatMeasure> stat_measures = stat_work.getMeasures();
		String person = null;
		String prog_detail = null;
		 
		if( ListTools.isNotEmpty(measureIds)) {
			measure_list = report_P_MeasureInfoServiceAdv.list(measureIds );
		}
		
		if( ListTools.isNotEmpty(measure_list)) {
			//将该工作在这个月所有的举措的完成情况填写为一样的
			//查询该汇报中，该工作的所有个人的完成情况
			ids = report_C_WorkProgServiceAdv.listWithReportAndWorkId( report.getId(), stat_work.getWorkId() );
			if( ListTools.isNotEmpty( ids )) {
				progList = report_C_WorkProgServiceAdv.list(ids);
			}			
			
			for( Report_P_MeasureInfo measure : measure_list) {
				stat_measure = findMeasureInListWithMeasureId( measure.getId(), stat_measures );
				if( stat_measure == null ) {
					stat_measure = new Wo_StatMeasure();
					stat_measure.setMeasureId( measure.getId() );
					stat_measure.setMeasureName( measure.getTitle() );
					stat_measure.setProgMonth( new ArrayList<>());
					stat_measures.add( stat_measure );
				}
				
				if( ListTools.isNotEmpty( progList )) {
					progMonth = findProgMonthFromListWithYearMonth( report.getYear(), report.getMonth(), stat_measure.getProgMonth() );
					if( progMonth == null ) {
						progMonth = new Wo_ProgMonth();
						progMonth.setYear(report.getYear());
						progMonth.setMonth(report.getMonth());
						progMonth.setProgPerson( new ArrayList<>());
					}				
				}
				
				for( Report_C_WorkProg prog : progList ) {
					person = prog.getTargetPerson();
					prog_detail = report_C_WorkProgServiceAdv.getProgressContentWithProgId(prog.getId());
					addProgPersonToList( person, prog_detail, progMonth );
				}
			}
		}
		return stat_measures;
	}

	/**
	 * 将个人完成情况添加到具体工作具体举措的具体月份完成情况中
	 * @param person
	 * @param prog_detail
	 * @param progMonth
	 */
	private void addProgPersonToList(String person, String prog_detail, Wo_ProgMonth progMonth) {
		List<Wo_ProgPerson>  progPersonList = progMonth.getProgPerson();
		Wo_ProgPerson progPerson = null;
		if( progPersonList == null ) {
			progPersonList = new ArrayList<>();
		}
		Boolean exists = false;
		for( Wo_ProgPerson _progPerson :  progPersonList ) {
			if( person.equalsIgnoreCase( _progPerson.getPerson() )) {
				exists = true;
			}
		}
		if( !exists ) {
			progPerson = new Wo_ProgPerson();
			progPerson.setDetail(prog_detail);
			progPerson.setPerson(person);
		}
	}

	private Wo findWrapInListWithUnitName(String targetUnit, List<Wo> wos) {
		if( ListTools.isEmpty( wos)) {
			return null;
		}
		for( Wo wo : wos ) {
			if( targetUnit.equalsIgnoreCase( wo.getUnitName() )) {
				return wo;
			}
		}
		return null;
	}
	
	private Wo_StatWork findWorkInListWithWorkId(String workId, List<Wo_StatWork> stat_works) {
		if( ListTools.isEmpty( stat_works)) {
			return null;
		}
		for( Wo_StatWork work : stat_works ) {
			if( workId.equalsIgnoreCase( work.getWorkId() )) {
				return work;
			}
		}
		return null;
	}
	
	private Wo_StatMeasure findMeasureInListWithMeasureId(String id, List<Wo_StatMeasure> stat_measures) {
		if( ListTools.isEmpty( stat_measures)) {
			return null;
		}
		for( Wo_StatMeasure measure : stat_measures ) {
			if( id.equalsIgnoreCase( measure.getMeasureId() )) {
				return measure;
			}
		}
		return null;
	}
	
	private Wo_ProgMonth findProgMonthFromListWithYearMonth(String year, String month, List<Wo_ProgMonth> progMonthList) {
		if( ListTools.isEmpty( progMonthList)) {
			return null;
		}
		for( Wo_ProgMonth progMonth : progMonthList ) {
			if(  year.equals( progMonth.getYear() ) && month.equals( progMonth.getMonth() )) {
				return progMonth;
			}
		}
		return null;
	}
	
	public static class Wo {

		@FieldDescribe("汇报部门名称")
		private String unitName = null;

		@FieldDescribe("重点工作数量")
		private Integer workTotal = 0;

		@FieldDescribe("重点工作列表")
		private List<Wo_StatWork> works = null;

		public String getUnitName() {
			return unitName;
		}

		public Integer getWorkTotal() {
			return workTotal;
		}

		public List<Wo_StatWork> getWorks() {
			return works;
		}

		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}

		public void setWorkTotal(Integer workTotal) {
			this.workTotal = workTotal;
		}

		public void setWorks(List<Wo_StatWork> works) {
			this.works = works;
		}
	}

	public static class Wo_StatWork{
		@FieldDescribe("工作名称")
		private String workName = null;
		
		@FieldDescribe("工作名称")
		private String workId = null;

		@FieldDescribe("关联举措数量")
		private Integer measureTotal = 0;

		@FieldDescribe("举措实际完成情况")
		private List<Wo_StatMeasure> measures = null;

		public String getWorkName() {
			return workName;
		}

		public Integer getMeasureTotal() {
			return measureTotal;
		}

		public List<Wo_StatMeasure> getMeasures() {
			return measures;
		}

		public void setWorkName(String workName) {
			this.workName = workName;
		}

		public void setMeasureTotal(Integer measureTotal) {
			this.measureTotal = measureTotal;
		}

		public void setMeasures(List<Wo_StatMeasure> measures) {
			this.measures = measures;
		}

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}
	}

	public static class Wo_StatMeasure {
		@FieldDescribe("举措名称")
		private String measureName = null;
		
		@FieldDescribe("举措Id")
		private String measureId = null;

		@FieldDescribe("月份完成情况")
		private List<Wo_ProgMonth> progMonth = null;

		public String getMeasureName() {
			return measureName;
		}
		
		public List<Wo_ProgMonth> getProgMonth() {
			return progMonth;
		}

		public void setMeasureName(String measureName) {
			this.measureName = measureName;
		}

		public void setProgMonth(List<Wo_ProgMonth> progMonth) {
			this.progMonth = progMonth;
		}

		public String getMeasureId() {
			return measureId;
		}

		public void setMeasureId(String measureId) {
			this.measureId = measureId;
		}		
	}
	
	public static class Wo_ProgMonth {
		
		@FieldDescribe("年份")
		private String year = "2017";
		
		@FieldDescribe("月份")
		private String month = "1";

		@FieldDescribe("月份个人完成情况")
		private List<Wo_ProgPerson> progPerson = null;

		public String getMonth() {
			return month;
		}

		public List<Wo_ProgPerson> getProgPerson() {
			return progPerson;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public void setProgPerson(List<Wo_ProgPerson> progPerson) {
			this.progPerson = progPerson;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}
		
	}
	
	public static class Wo_ProgPerson {
		
		@FieldDescribe("个人标识")
		private String person = null;

		@FieldDescribe("月份个人完成情况")
		private String detail = null;

		public String getPerson() {
			return person;
		}

		public String getDetail() {
			return detail;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}		
	}
}