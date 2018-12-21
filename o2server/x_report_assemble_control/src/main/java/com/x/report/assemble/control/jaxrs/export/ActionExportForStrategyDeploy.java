package com.x.report.assemble.control.jaxrs.export;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoCompanyStrategy;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoMeasuresInfo;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoCompanyStrategyWorks;
import com.x.report.assemble.control.jaxrs.export.exception.ExceptionDataNotExists;
import com.x.report.assemble.control.jaxrs.export.exception.ExceptionDataQuery;
import com.x.report.core.entity.Report_C_WorkProg;

/**
 * 按导出层级组织一个对象
 * 战略重点 - 举措 - 组织 ： 每个月的完成情况
 * @author O2LEE
 */
public class ActionExportForStrategyDeploy extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionExportForStrategyDeploy.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,  String year ) {
		ActionResult<Wo> result = new ActionResult<>();
		//组织一个用于导出的总体对象列表
		List<ExpCompanyStrategy>  expCompanyStrateies = new ArrayList<>();
		List<WoCompanyStrategy>        strategies = null;
		List<WoCompanyStrategyWorks>  unitWorks = null;
		List<Report_C_WorkProg> workProgList = null;
		ExpCompanyStrategy expCompanyStrategy = null;
		ExpMeasuresInfo expMeasuresInfo = null;
		Boolean check = true;
		
		//查询所有的部门工作信息
		if( check ) {
			try {
				unitWorks = companyStrategyWorks.all(year);
				if( ListTools.isEmpty( unitWorks ) ) {
					check = false;
					Exception exception = new ExceptionDataNotExists( year );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDataQuery( e, year );
				result.error( exception );
			}
		}
		//查询所有的公司重点和举措信息, 并且转换为一个简单的信息对象列表
		if( check ) {
			try {
				strategies = companyStrategyMeasure.all( year );
				if( ListTools.isEmpty( strategies ) ) {
					check = false;
					Exception exception = new ExceptionDataNotExists( year );
					result.error( exception );
				}else {
					for( WoCompanyStrategy strategy : strategies ) {
						expCompanyStrategy = new ExpCompanyStrategy(strategy.getId(), strategy.getStrategydeploytitle(),  new ArrayList<>());
						if( ListTools.isNotEmpty( strategy.getMeasureList() )) {
							for( WoMeasuresInfo measuresInfo : strategy.getMeasureList() ) {
								expMeasuresInfo = new ExpMeasuresInfo(measuresInfo.getId(), measuresInfo.getMeasuresinfotitle(), new ArrayList<>());
								//组织这个举措所涉及到的所有组织和汇报信息
								for( WoCompanyStrategyWorks unitWork : unitWorks ) {
									if(  unitWork.getMeasureslist() != null && unitWork.getMeasureslist().contains( measuresInfo.getId() )) {
										//说明该工作与当前举措有关系
										//查询该部门工作在当前年份的所有工作情况信息列表
										workProgList = report_C_WorkProgServiceAdv.listWithKeyWorkIdAndYear(unitWork.getId(), year);
										if( ListTools.isNotEmpty(workProgList)) {
											//按月份存储到指定的对象里
											expMeasuresInfo = addWorkProgInMeasureList(expMeasuresInfo, unitWork.getKeyworkunit(),workProgList );
											if( ListTools.isNotEmpty( expMeasuresInfo.getUnits() )) {
												expMeasuresInfo.setRowCount( expMeasuresInfo.getUnits().size());
											}
										}
									}
								}
								if( expMeasuresInfo.getRowCount() == 0 ) {
									expMeasuresInfo.setRowCount( 1 );
								}
								expCompanyStrategy.getMeasures().add( expMeasuresInfo );
								expCompanyStrategy.setRowCount(expCompanyStrategy.getRowCount() + expMeasuresInfo.getRowCount() );
							}
						}
						if( expCompanyStrategy.getRowCount() == 0 ) {
							expCompanyStrategy.setRowCount( 1 );
						}
						expCompanyStrateies.add( expCompanyStrategy );
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDataQuery( e, year );
				result.error( exception );
			}
		}
		
		if( check && strategies != null &&unitWorks != null  ) {
			try {
				byte[] byteArray = new StrategyDeployExportExcelWriter().writeExcel(expCompanyStrateies);
				Wo wo = new Wo( byteArray,  this.contentType(false, "公司战略汇报情况统计表.xls"),  this.contentDisposition(false, "公司战略汇报情况统计表.xls"));
				result.setData( wo );
			} catch (Exception e) {
				logger.warn("system export file got an exception");
				logger.error(e);
			}
		}
		return result;
	}


	private ExpMeasuresInfo addWorkProgInMeasureList(ExpMeasuresInfo expMeasuresInfo, String unitName, List<Report_C_WorkProg> workProgList) throws Exception {
		//先把组织信息加入进去，如果已经有了，就取出来，不用再新增了
		List<ExpUnitInfo>  expUnitInfos = expMeasuresInfo.getUnits();
		ExpUnitInfo expUnit = null;
		Boolean unitExists = false;
		if( expUnitInfos == null ) {
			expUnitInfos = new ArrayList<>();
		}
		for( ExpUnitInfo _expUnit : expUnitInfos ) {
			if( StringUtils.equals( _expUnit.getTitle(), unitName)) {
				unitExists = true;
				expUnit = _expUnit;
			}
		}
		if( !unitExists ) {
			expUnit = new ExpUnitInfo( unitName, new ArrayList<>());
			expUnitInfos.add( expUnit );
		}
		addWorkProgInExpUnit(expUnit, workProgList);
		return expMeasuresInfo;
	}


	private void addWorkProgInExpUnit(ExpUnitInfo expUnit, List<Report_C_WorkProg> workProgList) throws Exception {
		if( expUnit == null || workProgList == null ) {
			return;
		}
		List<ExpWorkMonthProg>  progList = expUnit.getWorkMonthProgs();
		if( progList == null ) {
			progList = new ArrayList<>();
			expUnit.setWorkMonthProgs(progList);
		}
		Boolean monthExists = false;
		ExpWorkMonthProg monthProg = null;
		for(Report_C_WorkProg prog : workProgList ) {
			monthExists = false;
			for( ExpWorkMonthProg _monthProg : progList ) {
				int month_prog =  Integer.parseInt(prog.getMonth());
				if(_monthProg.month ==month_prog) {
					monthExists = true;
					monthProg = _monthProg;
				}
			}
			if( !monthExists ) {
				int month_prog =  Integer.parseInt(prog.getMonth());
				monthProg = new ExpWorkMonthProg(month_prog, new ArrayList<>());
				progList.add( monthProg );
			}
			addProgContentToMonthProg(monthProg, prog);
		}
	}


	private void addProgContentToMonthProg(ExpWorkMonthProg monthProg, Report_C_WorkProg prog) throws Exception {
		if( monthProg == null || prog == null ) {
			return;
		}
		List<ExpWorkProg>  progContentList = monthProg.getWorkProgs();
		if( progContentList == null ) {
			progContentList = new ArrayList<>();
			monthProg.setWorkProgs(progContentList);
		}
		Boolean exists = false;
		for( ExpWorkProg workProg : progContentList ) {
			if(StringUtils.equals( workProg.getId(), prog.getId())) {
				exists = true;
			}
		}
		if( !exists ) {
			String content = report_C_WorkProgServiceAdv.getProgressContentWithProgId(prog.getId());
			progContentList.add( new ExpWorkProg(prog.getId(), "", prog.getTargetPerson(),content ));
		}
	}
	
	public static class ExpCompanyStrategy {	
		private String id = null;
		private String title = null;
		private int rowCount = 0;
		private List<ExpMeasuresInfo>  measures = null;
		
		public ExpCompanyStrategy(String id, String title, List<ExpMeasuresInfo> measures) {
			super();
			this.id = id;
			this.title = title;
			this.measures = measures;
		}
		
		public int getRowCount() {
			return rowCount;
		}

		public void setRowCount(int rowCount) {
			this.rowCount = rowCount;
		}

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}
		public List<ExpMeasuresInfo> getMeasures() {
			return measures;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setMeasures(List<ExpMeasuresInfo> measures) {
			this.measures = measures;
		}
	}
	
	public static class ExpMeasuresInfo {
		private String id = null;
		private String title = null;
		private int rowCount = 0;
		private List<ExpUnitInfo> units = null;
		public ExpMeasuresInfo(String id, String title, List<ExpUnitInfo> units) {
			super();
			this.id = id;
			this.title = title;
			this.units = units;
		}
		
		public int getRowCount() {
			return rowCount;
		}

		public void setRowCount(int rowCount) {
			this.rowCount = rowCount;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}
		public List<ExpUnitInfo> getUnits() {
			return units;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setUnits(List<ExpUnitInfo> units) {
			this.units = units;
		}
	}

	public static class ExpUnitInfo {	
		private String title = null;
		private List<ExpWorkMonthProg> workMonthProgs = null;
		
		public ExpUnitInfo(String title, List<ExpWorkMonthProg> workMonthProgs) {
			super();
			this.title = title;
			this.workMonthProgs = workMonthProgs;
		}
		public String getTitle() {
			return title;
		}
		public List<ExpWorkMonthProg> getWorkMonthProgs() {
			return workMonthProgs;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setWorkMonthProgs(List<ExpWorkMonthProg> workMonthProgs) {
			this.workMonthProgs = workMonthProgs;
		}
	}
	
	public static class ExpWorkMonthProg {
		private int month = 0;		
		private List<ExpWorkProg> workProgs = null;		
		public ExpWorkMonthProg(int month, List<ExpWorkProg> workProgs) {
			super();
			this.month = month;
			this.workProgs = workProgs;
		}
		public int getMonth() {
			return month;
		}
		public List<ExpWorkProg> getWorkProgs() {
			return workProgs;
		}
		public void setMonth(int month) {
			this.month = month;
		}
		public void setWorkProgs(List<ExpWorkProg> workProgs) {
			this.workProgs = workProgs;
		}
	}
	
	public static class ExpWorkProg {
		private String id = null;
		private String title = null;
		private String person = null;
		private String content = null;
		public ExpWorkProg(String id, String title, String person, String content) {
			super();
			this.id = id;
			this.title = title;
			this.person = person;
			this.content = content;
		}
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}
		public String getPerson() {
			return person;
		}
		public String getContent() {
			return content;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setPerson(String person) {
			this.person = person;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}
	
	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}
}
