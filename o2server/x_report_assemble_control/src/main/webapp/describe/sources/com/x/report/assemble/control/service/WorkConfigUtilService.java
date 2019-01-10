package com.x.report.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoCompanyStrategy;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoMeasuresInfo;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoCompanyStrategyWorks;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoMeasuresInfoInWork;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoStrategyDeployInMeasure;

/**
 * 从汇报概要文件的详细信息中查询信息的服务类
 * 
 * @author O2LEE
 *
 */
public class WorkConfigUtilService{

	private UserManagerService userManagerService = new UserManagerService();
	private Report_P_ProfileServiceAdv report_P_ProfileServiceAdv = new Report_P_ProfileServiceAdv();
	private Gson gson = XGsonBuilder.instance();
	
	/**
	 * 根据汇报概要文件ID，以及指定的工作ID，在概要文件详细信息中查询需要的工作信息进行返回
	 * @param profileId
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	public WoCompanyStrategyWorks getWorkInfo( String profileId, String workId ) throws Exception {
		//先获取所有的工作信息SNAP
		String json = report_P_ProfileServiceAdv.getDetailValue( profileId, "STRATEGY", "STRATEGY_WORK" );
		List<WoCompanyStrategyWorks> works = gson.fromJson(json, new TypeToken<List<WoCompanyStrategyWorks>>() {}.getType());
		if( works != null && !works.isEmpty() ) {
			for( WoCompanyStrategyWorks work : works ) {
				if( work.getId().equalsIgnoreCase( workId )) {
					return work;
				}
			}
		}
		return null;
	}

	/**
	 * 根据概要文件ID，以及指定的身份，查询该身份所属组织涉及的所有工作信息列表
	 * @param profileId
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	private List<WoCompanyStrategyWorks> listWorkInfo( EffectivePerson effectivePerson, String profileId, String identity ) throws Exception {
		List<WoCompanyStrategyWorks> works = new ArrayList<>();
		//先获取当前身份所属的组织，如果组织为空，则退出
		String unitName = userManagerService.getUnitNameByIdentity(identity);
		//先获取所有的工作信息SNAP
		String json = report_P_ProfileServiceAdv.getDetailValue( profileId, "STRATEGY", "STRATEGY_WORK" );
		List<WoCompanyStrategyWorks> allWorks = gson.fromJson( json, new TypeToken<List<WoCompanyStrategyWorks>>() {}.getType() );
		if( allWorks != null && !allWorks.isEmpty() ) {
			for( WoCompanyStrategyWorks work : allWorks ) {
				if( work.getKeyworkunit().equalsIgnoreCase( unitName ) ) {
					works.add( work );
				}
			}
		}
		return works;
	}

	/**
	 * 根据概要文件ID，以及指定的身份，查询该身份所属组织涉及的工作信息列表
	 * @param effectivePerson
	 * @param profileId
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public List<WoCompanyStrategyWorks> listWorkInfoWithIdentity(EffectivePerson effectivePerson, String profileId, String identity ) throws Exception {
		return listWorkInfo( effectivePerson, profileId, identity );
	}

	/**
	 * 根据概要文件ID，以及指定的个人标识，查询个人所有身份身份所属组织涉及的所有工作信息列表
	 * @param effectivePerson 
	 * @param profileId
	 * @param distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<WoCompanyStrategyWorks> listWorkInfoWithPerson(EffectivePerson effectivePerson, String profileId, String distinguishedName) throws Exception {
		List<String> identities = userManagerService.listIdentitiesWithPerson( distinguishedName );
		List<WoCompanyStrategyWorks> works = new ArrayList<>();
		List<WoCompanyStrategyWorks> worksForIdentity = null;
		Boolean exists = false;
		if( identities != null && !identities.isEmpty()  ) {
			for( String identity : identities ) {
				worksForIdentity = listWorkInfo( effectivePerson, profileId, identity );
				if( worksForIdentity != null && !worksForIdentity.isEmpty() ) {
					for( WoCompanyStrategyWorks _work : worksForIdentity ) {
						exists = false;
						for( WoCompanyStrategyWorks work : works ) {
							if( _work.getId().equalsIgnoreCase( work.getId() )) {
								exists = true;
							}
						}
						if( !exists ) {
							works.add( _work );
						}
					}
				}
			}
		}
		return works;
	}

	/**
	 * 根据概要文件ID，从概要文件详细信息获取快照，并且从快照信息中根据举措ID，获取举措信息列表<br/>
	 * 举措信息中需要组织举措所属的公司重点部署信息对象
	 * 
	 * @param profileId
	 * @param measureslist
	 * @return
	 * @throws Exception
	 */
	public List<WoMeasuresInfoInWork> listMeasuresInfoCompose( String profileId, List<String> measureslist ) throws Exception {
		if( measureslist == null || measureslist.isEmpty() ) {
			return new ArrayList<>();
		}
		WoMeasuresInfoInWork measuresInfoInWork = null;
		List<WoMeasuresInfoInWork> measuresInfoList = new ArrayList<>();
		//先获取所有的工作信息SNAP
		String json = report_P_ProfileServiceAdv.getDetailValue( profileId, "STRATEGY", "STRATEGY_MEASURE" );
		List<WoCompanyStrategy> allstrategyDeploys = gson.fromJson( json, new TypeToken<List<WoCompanyStrategy>>() {}.getType() );
		if( allstrategyDeploys != null && !allstrategyDeploys.isEmpty() ) {
			for( String measuresId : measureslist ) {
				for( WoCompanyStrategy strategyDeploy : allstrategyDeploys ) {
					//查询该公司重点信息中所有的举措信息，遍历所有举措信息，查询需要的信息
					if( strategyDeploy.getMeasureList() != null && !strategyDeploy.getMeasureList().isEmpty() ) {
						for( WoMeasuresInfo measuresInfo : strategyDeploy.getMeasureList() ) {
							if( measuresInfo.getId().equalsIgnoreCase( measuresId ) ) {
								measuresInfoInWork = WoMeasuresInfoInWork.copier.copy( measuresInfo );
								measuresInfoInWork.setStrategyDeploy( WoStrategyDeployInMeasure.copier.copy( strategyDeploy ) );
								measuresInfoList.add( measuresInfoInWork );
							}
						}
					}
				}
			}
		}
		return measuresInfoList;
	}

	public WoMeasuresInfo getMeasureInfo(String profileId, String measureId) throws Exception {
		//先获取所有的战备部署信息SNAP
		String json = report_P_ProfileServiceAdv.getDetailValue( profileId, "STRATEGY", "STRATEGY_MEASURE" );
		List<WoCompanyStrategy> allstrategyDeploys = gson.fromJson( json, new TypeToken<List<WoCompanyStrategy>>() {}.getType() );
		if( ListTools.isNotEmpty(allstrategyDeploys) ) {
			for( WoCompanyStrategy strategyDeploy : allstrategyDeploys ) {
				//查询该公司重点信息中所有的举措信息，遍历所有举措信息，查询需要的信息
				if( ListTools.isNotEmpty(strategyDeploy.getMeasureList())) {
					for( WoMeasuresInfo measuresInfo : strategyDeploy.getMeasureList() ) {
						if( measureId.equalsIgnoreCase( measuresInfo.getId() ) ) {
							return measuresInfo;
						}
					}
				}
			}
		}
		return null;
	}
	
	public List<WoMeasuresInfo> getMeasureInfoWithUnit_thisMonth(String profileId, String unitName ) throws Exception {
		if( StringUtils.isEmpty( unitName )) {
			throw new Exception("unitName is null!");
		}
		List<WoMeasuresInfo> woMeasureList = new ArrayList<>();
		String work_json = report_P_ProfileServiceAdv.getDetailValue( profileId, "STRATEGY", "STRATEGY_MEASURE" );
		List<WoCompanyStrategy> allStrategies = gson.fromJson( work_json, new TypeToken<List<WoCompanyStrategy>>() {}.getType() );
		if( ListTools.isNotEmpty( allStrategies ) ) {
			for( WoCompanyStrategy strategy : allStrategies ) {
				if( ListTools.isNotEmpty(strategy.getMeasureList()) ){
					for( WoMeasuresInfo woMeasuresInfo : strategy.getMeasureList() ) {
						if( ListTools.isNotEmpty( woMeasuresInfo.getDeptlist() ) &&  woMeasuresInfo.getDeptlist().contains( unitName ) ) {
							woMeasureList.add(woMeasuresInfo);
						}
					}
				}
			}
		}
		return woMeasureList;
	}
	
	public List<WoMeasuresInfo> getMeasureInfoWithUnit_nextMonth(String profileId, String unitName ) throws Exception {
		if( StringUtils.isEmpty( unitName )) {
			throw new Exception("unitName is null!");
		}
		List<WoMeasuresInfo> woMeasureList = new ArrayList<>();
		String work_json = report_P_ProfileServiceAdv.getDetailValue( profileId, "STRATEGY", "STRATEGY_MEASURE_NEXTMONTH" );
		List<WoCompanyStrategy> allStrategies = gson.fromJson( work_json, new TypeToken<List<WoCompanyStrategy>>() {}.getType() );
		if( ListTools.isNotEmpty( allStrategies ) ) {
			for( WoCompanyStrategy strategy : allStrategies ) {
				if( ListTools.isNotEmpty(strategy.getMeasureList()) ){
					for( WoMeasuresInfo woMeasuresInfo : strategy.getMeasureList() ) {
						if( ListTools.isNotEmpty( woMeasuresInfo.getDeptlist() ) &&  woMeasuresInfo.getDeptlist().contains( unitName ) ) {
							woMeasureList.add(woMeasuresInfo);
						}
					}
				}
			}
		}
		return woMeasureList;
	}
}
