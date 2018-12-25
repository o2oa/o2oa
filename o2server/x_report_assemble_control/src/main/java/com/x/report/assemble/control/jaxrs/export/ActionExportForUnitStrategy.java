package com.x.report.assemble.control.jaxrs.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import com.x.report.core.entity.Report_I_WorkInfo;

/**
 * 按导出层级组织一个对象
 * 组织 - 战略重点 - 举措 ： 每个月的完成情况
 * @author O2LEE
 */
public class ActionExportForUnitStrategy extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionExportForUnitStrategy.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,  String year ) {
		ActionResult<Wo> result = new ActionResult<>();
		List<WoCompanyStrategy>        strategies = null;
		List<WoCompanyStrategyWorks>  unitWorks = null;
		ExpMeasuresInfo expMeasuresInfo = null;
		ExpUnitInfo expUnitInfo = null;
		ExpWork expWork = null;
		
		//将组织、工作和举措做成HashMap便于获取
		//组织包含工作，工作包含举措，举措内，则是每个工作的完成情况
		//工作与举措的关系，由工作完成情况所属的汇报信息与举措的关联决定
		Map<String, ExpUnitInfo> expUnitInfoMap = new HashMap<>();
		Map<String, ExpMeasuresInfo> all_expMeasuresInfoMap = new HashMap<>();
		Map<String, ExpWork> expWorkMap = null;
		Boolean check = true;
		
		//查询所有的部门和工作信息，放入Map里，并且组织好组织和工作之间的关系，将工作分别放入指定的组织中
		//一个工作只属于指定的一个组织
		if( check ) {
			try {
				unitWorks = companyStrategyWorks.all(year);
				if( ListTools.isEmpty( unitWorks ) ) {
					check = false;
					Exception exception = new ExceptionDataNotExists( year );
					result.error( exception );
				}else {
					for( WoCompanyStrategyWorks work : unitWorks ) {
						if( StringUtils.isNotEmpty( work.getKeyworkunit())) {
							//将组织放入Map
							expUnitInfo = expUnitInfoMap.get( work.getKeyworkunit());
							if( expUnitInfo == null ) {
								expUnitInfo = new ExpUnitInfo( work.getKeyworkunit(), 1, new HashMap<>() );
								expUnitInfoMap.put( work.getKeyworkunit(), expUnitInfo );
							}
							// expUnitInfo 已经不可能为空了
							// 将工作放入组织信息的Map中的工作列表里
							expWorkMap = expUnitInfo.getExpWorkMap();
							if( expWorkMap == null ) {
								expWorkMap = new HashMap<>();
							}
							expWork = expWorkMap.get( work.getId() );
							if( expWork == null ) {
								expWork = new ExpWork( work.getId(), work.getKeyworktitle(), work.getKeyworkunit(), new HashMap<>());
								expWorkMap.put( work.getId(), expWork );
							}
						}
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDataQuery( e, year );
				result.error( exception );
			}
		}
		
		//將所有的举措信息放入Map里备用
		if( check ) {
			Map<Integer, ExpWorkMonthProg> expWorkMonthProgMap = null;
			try {
				strategies = companyStrategyMeasure.all( year );
				if( ListTools.isEmpty( strategies ) ) {
					check = false;
					Exception exception = new ExceptionDataNotExists( year );
					result.error( exception );
				}else {
					//创建所有的举措导出信息列表对象
					for( WoCompanyStrategy strategy : strategies ) {
						if( ListTools.isNotEmpty( strategy.getMeasureList() )) {
							for( WoMeasuresInfo measuresInfo : strategy.getMeasureList() ) {
								expWorkMonthProgMap = new HashMap<>();
								expMeasuresInfo = new ExpMeasuresInfo( measuresInfo.getId(), measuresInfo.getMeasuresinfotitle(), expWorkMonthProgMap );
								all_expMeasuresInfoMap.put( expMeasuresInfo.getId(), expMeasuresInfo );
								//初始化12个月的工作完成情况
								for( int i=1 ; i<=12 ; i++ ) {
									expWorkMonthProgMap.put( i, new ExpWorkMonthProg( i, new ArrayList<>()) );
								}
							}
						}
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDataQuery( e, year );
				result.error( exception );
			}
		}
		
		//获取该年份所有的工作完成情况，遍历所有的工作完成情况，形成工作和举措的关联，并且形成每个举措每个月份的工作完成情况
		if( check ) {
			Report_I_WorkInfo report_I_WorkInfo = null;
			List<String> ids = null;
			List<Report_C_WorkProg>  progList = null;
			
			try {
				ids = report_C_WorkProgServiceAdv.listWithYear( year );
				if(ListTools.isNotEmpty( ids )) {
					progList = report_C_WorkProgServiceAdv.list(ids);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//遍历所有的工作完成情况
			if( ListTools.isNotEmpty( progList )) {
				String keyWorkId = null;
				String workUnitName = null;
				String person = null;
				String content = null;
				String title = null;
				String progId = null;
				int month = 0;
				List<String> measuresIds = null;
				for( Report_C_WorkProg prog : progList ) {
					//判断该工作情况与举措的关联，以及与工作的关联，确定把该工作情况放到哪个工作的举措的完成情况列表中
					//查询该完成情况的汇报信息
					try {
						report_I_WorkInfo = report_I_WorkInfoServiceAdv.get( prog.getWorkInfoId() );
						if( report_I_WorkInfo != null ) {
							keyWorkId = report_I_WorkInfo.getKeyWorkId();
							workUnitName = report_I_WorkInfo.getWorkUnit();
							measuresIds = report_I_WorkInfo.getMeasuresList();
							month = Integer.parseInt(prog.getMonth());
							person = prog.getTargetPerson();
							progId = prog.getId();
							title = prog.getTitle();
							content = report_C_WorkProgServiceAdv.getProgressContentWithProgId(prog.getId());
							composeProg( workUnitName, keyWorkId, measuresIds, progId, title, month, person, content, expUnitInfoMap,  all_expMeasuresInfoMap );
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		if( check ) {
			Set<Entry<String, ExpUnitInfo>> expUnitInfo_set = expUnitInfoMap.entrySet();
			Iterator<Entry<String, ExpUnitInfo>> expUnitInfo_iterator = expUnitInfo_set.iterator();
			Set<Entry<String, ExpWork>> expWork_set = null;
			Iterator<Entry<String, ExpWork>> expWork_iterator = null;
			Map<String, ExpMeasuresInfo> expMeasuresInfoMap = null; 
			Set<Entry<String, ExpMeasuresInfo>> expMeasuresInfo_set = null;
			Iterator<Entry<String, ExpMeasuresInfo>> expMeasuresInfo_iterator = null;
			
			if( expUnitInfoMap != null ){
				expUnitInfo_set = expUnitInfoMap.entrySet();
				expUnitInfo_iterator = expUnitInfo_set.iterator();
				while( expUnitInfo_iterator.hasNext() ) { //遍历组织
					expUnitInfo = (ExpUnitInfo)expUnitInfo_iterator.next().getValue();
					expWorkMap = expUnitInfo.getExpWorkMap();
					if( expWorkMap != null && !expWorkMap.isEmpty()) {
						expWork_set = expWorkMap.entrySet();
						expWork_iterator = expWork_set.iterator();
						while( expWork_iterator.hasNext() ) { //遍历工作
							expWork = (ExpWork)expWork_iterator.next().getValue();
							expMeasuresInfoMap = expWork.getExpMeasuresInfoMap();
							if( expMeasuresInfoMap != null && !expMeasuresInfoMap.isEmpty() ) {
								expMeasuresInfo_set = expMeasuresInfoMap.entrySet();
								expMeasuresInfo_iterator = expMeasuresInfo_set.iterator();
								while( expMeasuresInfo_iterator.hasNext() ) { //遍历举措
									expMeasuresInfo = (ExpMeasuresInfo)expMeasuresInfo_iterator.next().getValue();
									expWork.setRowCount( expWork.getRowCount() + 1 );
									expUnitInfo.setRowCount( expUnitInfo.getRowCount() + 1 );
								}
							}else {
								if( expWork.getRowCount() == 0 ) {
									expWork.setRowCount( 1 );
								}else {
									expWork.setRowCount( expWork.getRowCount() + 1 );
								}
							}
						}
					}else {
						if( expUnitInfo.getRowCount() == 0 ) {
							expUnitInfo.setRowCount( 1 );
						}else {
							expUnitInfo.setRowCount( expUnitInfo.getRowCount() + 1 );
						}
					}
				}
			}
		}
		
		if( check && strategies != null &&unitWorks != null  ) { 
			try {
				byte[] byteArray = new UnitStrategyExportExcelWriter().writeExcel(expUnitInfoMap);
				Wo wo = new Wo( byteArray,  this.contentType(false, "部门工作完成情况统计表.xls"),  this.contentDisposition(false, "部门工作完成情况统计表.xls"));
				result.setData( wo );
			} catch (Exception e) {
				logger.warn("system export file got an exception");
				logger.error(e);
			}
		}
		return result;
	}
	
	/**
	 * 将工作完成情况添加到指定的工作的相应举措的指定月份中去
	 * @param workUnitName
	 * @param keyWorkId
	 * @param measuresIds
	 * @param month
	 * @param person
	 * @param content
	 * @param expUnitInfoMap
	 * @param expMeasuresInfoMap
	 */
	private Map<String, ExpUnitInfo>  composeProg(String workUnitName, String keyWorkId, List<String> measuresIds, String progId, String title, Integer month, String person,
			String content, Map<String, ExpUnitInfo> expUnitInfoMap, Map<String, ExpMeasuresInfo> all_expMeasuresInfoMap) {
		if( expUnitInfoMap == null ) {
			return expUnitInfoMap;
		}
		if( all_expMeasuresInfoMap == null ) {
			return expUnitInfoMap;
		}
		if( ListTools.isEmpty(measuresIds) ) {
			return expUnitInfoMap;
		}
		if( month == 0 ) {
			return expUnitInfoMap;
		}
		
		ExpMeasuresInfo unit_work_expMeasuresInfo = null;
		
		ExpUnitInfo expUnitInfo = expUnitInfoMap.get( workUnitName );
		if( expUnitInfo == null ) {
			return expUnitInfoMap;
		}
		
		Map<String, ExpWork> expWorkMap = expUnitInfo.getExpWorkMap();
		if( expWorkMap == null ) {
			return expUnitInfoMap;
		}
		
		ExpWork expWork = expWorkMap.get( keyWorkId );
		if( expWork == null ) {
			return expUnitInfoMap;
		}		
		
		Map<String, ExpMeasuresInfo> unit_work_expMeasuresInfoMap = expWork.getExpMeasuresInfoMap();
		if( unit_work_expMeasuresInfoMap == null ) {
			unit_work_expMeasuresInfoMap = new HashMap<>();
		}
		
		ExpMeasuresInfo expMeasuresInfo = null;
		for( String measuresId : measuresIds ) {
			unit_work_expMeasuresInfo = unit_work_expMeasuresInfoMap.get( measuresId );
			if( unit_work_expMeasuresInfo == null ) {
				//创建一个举措信息
				expMeasuresInfo = all_expMeasuresInfoMap.get( measuresId );
				if ( expMeasuresInfo != null ) {
					unit_work_expMeasuresInfo = new ExpMeasuresInfo(expMeasuresInfo.getId(), expMeasuresInfo.getTitle(), expMeasuresInfo.getExpWorkMonthProgMap());
					unit_work_expMeasuresInfoMap.put( measuresId, unit_work_expMeasuresInfo );
				}
			}
			//向举措(unit_work_expMeasuresInfo)里更新一个完成情况信息，如果没有的话
			addProg2ExpMeasuresInfo( unit_work_expMeasuresInfo, progId, title,  month, person, content );			
		}
		return expUnitInfoMap;
	}	
	
	private ExpMeasuresInfo addProg2ExpMeasuresInfo(ExpMeasuresInfo unit_work_expMeasuresInfo,String progId, String title, Integer month, String person, String content) {
		if( unit_work_expMeasuresInfo == null ) {
			return unit_work_expMeasuresInfo;
		}
		if( month == 0 ) {
			return unit_work_expMeasuresInfo;
		}
		if( StringUtils.isEmpty( content )) {
			content = "无完成情况";
		}
		if( StringUtils.isEmpty( person )) {
			person = "佚名用户";
		}
		Map<Integer, ExpWorkMonthProg> expWorkMonthProgMap = unit_work_expMeasuresInfo.getExpWorkMonthProgMap();
		if( expWorkMonthProgMap == null ) {
			expWorkMonthProgMap = new HashMap<>();
			unit_work_expMeasuresInfo.setExpWorkMonthProgMap(expWorkMonthProgMap);
			//初始化12个月的工作完成情况
			for( int i=1 ; 1<=12 ; i++ ) {
				expWorkMonthProgMap.put( (Integer)i, new ExpWorkMonthProg( i, new ArrayList<>()) );
			}
		}
		ExpWorkMonthProg expWorkMonthProg  =  expWorkMonthProgMap.get( month );
		if( expWorkMonthProg == null ) {
			expWorkMonthProg = new ExpWorkMonthProg(month,  new ArrayList<>());
		}
		
		List<ExpWorkProg>  expWorkProgs = expWorkMonthProg.getWorkProgs();
		if( expWorkProgs == null ) {
			expWorkProgs = new ArrayList<>();
		}
		Boolean exists = false;
		for( ExpWorkProg expProg : expWorkProgs ) {
			if( expProg.getId().equalsIgnoreCase( progId) && expProg.getPerson().equalsIgnoreCase(person) ) {
				exists = true;
			}
		}
		if( !exists ) {
			ExpWorkProg expProg = new ExpWorkProg(progId, title, person, content);
			expWorkProgs.add( expProg );
		}
		return unit_work_expMeasuresInfo;
	}
	
	public static class ExpUnitInfo {	
		private String title = null;
		private int rowCount = 0;
		private Map<String, ExpWork> expWorkMap = null;
		
		public ExpUnitInfo(String title, int rowCount, Map<String, ExpWork> expWorkMap) {
			super();
			this.title = title;
			this.rowCount = rowCount;
			this.expWorkMap = expWorkMap;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getRowCount() {
			return rowCount;
		}
		public void setRowCount(int rowCount) {
			this.rowCount = rowCount;
		}
		public Map<String, ExpWork> getExpWorkMap() {
			return expWorkMap;
		}
		public void setExpWorkMap(Map<String, ExpWork> expWorkMap) {
			this.expWorkMap = expWorkMap;
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
		private Map<Integer, ExpWorkMonthProg> expWorkMonthProgMap = null;
			
		public ExpMeasuresInfo(String id, String title, Map<Integer, ExpWorkMonthProg> expWorkMonthProgMap) {
			super();
			this.id = id;
			this.title = title;
			this.expWorkMonthProgMap = expWorkMonthProgMap;
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
		public String getTitle() {
			return title;
		}
		public void setId(String id) {
			this.id = id;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public Map<Integer, ExpWorkMonthProg> getExpWorkMonthProgMap() {
			return expWorkMonthProgMap;
		}
		public void setExpWorkMonthProgMap(Map<Integer, ExpWorkMonthProg> expWorkMonthProgMap) {
			this.expWorkMonthProgMap = expWorkMonthProgMap;
		}
		
	}
	
	public static class ExpWork {
		private String title = null;
		private String workId = null;
		private String unitName = null;
		private int rowCount = 0;
		private Map<String, ExpMeasuresInfo> expMeasuresInfoMap = null;		
		public ExpWork(String workId, String title, String unitName, Map<String, ExpMeasuresInfo> expMeasuresInfoMap) {
			super();
			this.title = title;
			this.unitName = unitName;
			this.workId = workId;
			this.expMeasuresInfoMap = expMeasuresInfoMap;
		}
		public int getRowCount() {
			return rowCount;
		}

		public void setRowCount(int rowCount) {
			this.rowCount = rowCount;
		}

		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getWorkId() {
			return workId;
		}
		public void setWorkId(String workId) {
			this.workId = workId;
		}
		public Map<String, ExpMeasuresInfo> getExpMeasuresInfoMap() {
			return expMeasuresInfoMap;
		}
		public void setExpMeasuresInfoMap(Map<String, ExpMeasuresInfo> expMeasuresInfoMap) {
			this.expMeasuresInfoMap = expMeasuresInfoMap;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
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
