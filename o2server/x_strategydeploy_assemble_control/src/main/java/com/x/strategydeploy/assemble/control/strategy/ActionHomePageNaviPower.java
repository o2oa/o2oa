package com.x.strategydeploy.assemble.control.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.factory.ConfigFactory;
import com.x.strategydeploy.core.entity.StrategyConfigSys;

public class ActionHomePageNaviPower extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionHomePageNaviPower.class);

	private static String UNITNAME_COMPANYLEADER = "公司管理层";
	private static String GROUPNAME_STRATEGYWRITER = "strategy_writer_g";
	private static String GROUPNAME_COMPANYBUSINESSMANAGER = "strategy_companybusinessmanager_g";
	private static String UNITDUTY_DEPTLEADER_1 = "部主管";
	private static String UNITDUTY_DEPTLEADER_2 = "战略负责人";

	private static String UNITDUTY_DEPTREPORTER_1 = "部门战略管理员";
	private static String UNITDUTY_DEPTREPORTER_2 = "月度汇报员";

	private static String GROUPNAME_DEPTREPORTER_FOR_EMPLOYEE = "DeptReporter"; //普通人员升级为各部汇报人

	//输出
	//	public static class WoUserNaviPower extends UserNaviPower {
	//		private static final long serialVersionUID = 7053749990982581367L;
	//		public UserNaviPower userNaviPower;
	//
	//		public UserNaviPower getUserNaviPower() {
	//			return userNaviPower;
	//		}
	//
	//		public void setUserNaviPower(UserNaviPower userNaviPower) {
	//			this.userNaviPower = userNaviPower;
	//		}
	//
	//	}

	//输出
	public static class WoUserNaviPower extends UserNaviPower {
		List<String> navilist = new ArrayList<String>();

		public List<String> getNavilist() {
			return navilist;
		}

		public void setNavilist(List<String> navilist) {
			this.navilist = navilist;
		}

	}

	public ActionResult<WoUserNaviPower> execute(EffectivePerson effectivePerson) throws Exception {

		ActionResult<WoUserNaviPower> result = new ActionResult<>();
		//WoUserNaviPower data = new WoUserNaviPower();

		WoUserNaviPower userNaviPower = new WoUserNaviPower();
		String distinguishedName = effectivePerson.getDistinguishedName();
		String unique = "";
		if (StringUtils.indexOf(distinguishedName, "@") >= 0) {
			unique = StringUtils.split(distinguishedName, "@")[1];
		} else {
			unique = distinguishedName;
		}

		logger.info("distinguishedName:" + distinguishedName + " unique:" + unique);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			boolean _isCommonEmployee = true;

			////是否是公司管理层1
			List<String> unitList = new ArrayList<String>();
			List<String> _tmpUnitList = new ArrayList<String>();
			unitList = business.organization().unit().listWithPerson(effectivePerson);
			_tmpUnitList = ActionHomePageNaviPower.getStringList(unitList, 0);
			if (null == _tmpUnitList || _tmpUnitList.isEmpty()) {
				userNaviPower.setCompanyLeader(false);
			} else {
				if (_tmpUnitList.indexOf(UNITNAME_COMPANYLEADER) >= 0) {
					userNaviPower.setCompanyLeader(true);
					_isCommonEmployee = false;
				} else {
					userNaviPower.setCompanyLeader(false);
				}
			}
			////是否是公司管理层1

			//是否是公司战略管理员2
			List<String> personList = new ArrayList<String>();
			List<String> _tmpPersonList = new ArrayList<String>();
			personList = business.organization().person().listWithGroup(GROUPNAME_STRATEGYWRITER);
			_tmpPersonList = ActionHomePageNaviPower.getStringList(personList, 1);
			for (String string : _tmpPersonList) {
				logger.info(string);
			}
			if (null == _tmpPersonList || _tmpPersonList.isEmpty()) {
				userNaviPower.setCompanyStrategyManager(false);
			} else {
				if (_tmpPersonList.indexOf(unique) >= 0) {
					userNaviPower.setCompanyStrategyManager(true);
					_isCommonEmployee = false;
				} else {
					userNaviPower.setCompanyStrategyManager(false);
				}
			}
			//是否是公司战略管理员2

			//是否是公司事务管理员3
			List<String> personList2 = new ArrayList<String>();
			List<String> _tmpPersonList2 = new ArrayList<String>();
			personList2 = business.organization().person().listWithGroup(GROUPNAME_COMPANYBUSINESSMANAGER);
			for (String string : personList2) {
				logger.info("personList2:" + string);
			}

			_tmpPersonList2 = ActionHomePageNaviPower.getStringList(personList2, 1);
			if (null == _tmpPersonList2 || _tmpPersonList2.isEmpty()) {
				userNaviPower.setCompanyBusinessManager(false);
			} else {
				if (_tmpPersonList2.indexOf(unique) >= 0) {
					userNaviPower.setCompanyBusinessManager(true);
					_isCommonEmployee = false;
				} else {
					userNaviPower.setCompanyBusinessManager(false);
				}
			}
			//是否是公司事务管理员3

			//是否是战略负责人(部主管)4
			List<String> identityList = business.organization().identity().listWithPerson(effectivePerson);
			List<String> unitDutyList = new ArrayList<String>();
			for (int i = 0; i < identityList.size(); i++) {
				logger.info("identity:" + identityList.get(i));
				List<String> c = business.organization().unitDuty().listNameWithIdentity(identityList.get(i));
				unitDutyList.addAll(c);
			}
			logger.info("=================================");
			for (String string : unitDutyList) {
				logger.info("unitDutyList:" + string);
			}

			if (null == unitDutyList || unitDutyList.isEmpty()) {
				userNaviPower.setDeptLeader(false);
			} else {
				if (unitDutyList.indexOf(UNITDUTY_DEPTLEADER_1) >= 0 || unitDutyList.indexOf(UNITDUTY_DEPTLEADER_2) >= 0) {
					userNaviPower.setDeptLeader(true);
					_isCommonEmployee = false;
				} else {
					userNaviPower.setDeptLeader(false);
				}
			}
			//是否是战略负责人(部主管)4

			//是否是各部月度汇报员(部门战略管理员)5
			List<String> identityList2 = business.organization().identity().listWithPerson(effectivePerson);
			List<String> unitDutyList2 = new ArrayList<String>();
			for (int i = 0; i < identityList2.size(); i++) {
				logger.info("identity2:" + identityList2.get(i));
				List<String> c = business.organization().unitDuty().listNameWithIdentity(identityList2.get(i));
				unitDutyList2.addAll(c);
			}
			logger.info("=================================");
			for (String string : unitDutyList2) {
				logger.info("unitDutyList2:" + string);
			}

			if (null == unitDutyList2 || unitDutyList2.isEmpty()) {
				userNaviPower.setDeptStrategyManager(false);
			} else {
				if (unitDutyList2.indexOf(UNITDUTY_DEPTREPORTER_1) >= 0 || unitDutyList2.indexOf(UNITDUTY_DEPTREPORTER_2) >= 0) {
					userNaviPower.setDeptStrategyManager(true);
					_isCommonEmployee = false;
				} else {
					userNaviPower.setDeptStrategyManager(false);
				}
			}
			//是否是各部月度汇报员(部门战略管理员)5

			//是否是各部汇报人6
			List<String> personList3 = new ArrayList<String>();
			List<String> _tmpPersonList3 = new ArrayList<String>();
			personList3 = business.organization().person().listWithGroup(GROUPNAME_DEPTREPORTER_FOR_EMPLOYEE);
			_tmpPersonList3 = ActionHomePageNaviPower.getStringList(personList3, 1);
			if (null == _tmpPersonList3 || _tmpPersonList3.isEmpty()) {
				userNaviPower.setDeptReporter(false);
			} else {
				if (_tmpPersonList3.indexOf(unique) >= 0) {
					userNaviPower.setDeptReporter(true);
					_isCommonEmployee = false;
				} else {
					userNaviPower.setDeptReporter(false);
				}
			}
			//是否是汇报人6

			//是否是普通员工7
			userNaviPower.setCommonEmployee(_isCommonEmployee);
			//是否是普通员工7

			List<String> navilist = new ArrayList<String>();
			String Navi = "Navi";
			if (userNaviPower.isCommonEmployee()) {

				/*				navilist.add("主页");
								//navilist.add("年度工作纲要部署");
								//navilist.add("年度工作纲要总览");
								//navilist.add("月度工作汇报部署");
								//navilist.add("月度工作汇报总览");
								navilist.add("战略信息");
								//navilist.add("办公中心");
				*/
				navilist = ActionHomePageNaviPower.getNaviListByAlias(business, "CommonEmployee" + Navi);
				userNaviPower.setNavilist(navilist);
			}

			if (userNaviPower.isCompanyLeader()) {

				/*	
					navilist.add("主页");
					//navilist.add("年度工作纲要部署");
					navilist.add("年度工作纲要总览");
					//navilist.add("月度工作汇报部署");
					navilist.add("月度工作汇报总览");
					navilist.add("战略信息");
					navilist.add("办公中心");*/
				navilist = ActionHomePageNaviPower.getNaviListByAlias(business, "CompanyLeader" + Navi);
				userNaviPower.setNavilist(navilist);
			}

			if (userNaviPower.isCompanyBusinessManager) {

				/*				navilist.add("主页");
								//navilist.add("年度工作纲要部署");
								navilist.add("年度工作纲要总览");
								navilist.add("月度工作汇报部署");
								navilist.add("月度工作汇报总览");
								navilist.add("战略信息");
								navilist.add("办公中心");*/
				navilist = ActionHomePageNaviPower.getNaviListByAlias(business, "CompanyBusinessManager" + Navi);
				userNaviPower.setNavilist(navilist);
			}

			if (userNaviPower.isDeptLeader) {

				/*				navilist.add("主页");
								//navilist.add("年度工作纲要部署");
								navilist.add("年度工作纲要总览");
								navilist.add("月度工作汇报部署");
								navilist.add("月度工作汇报总览");
								navilist.add("战略信息");
								navilist.add("办公中心");*/
				navilist = ActionHomePageNaviPower.getNaviListByAlias(business, "DeptLeader" + Navi);
				userNaviPower.setNavilist(navilist);
			}

			if (userNaviPower.isDeptStrategyManager) {

				/*				navilist.add("主页");
								//navilist.add("年度工作纲要部署");
								navilist.add("年度工作纲要总览");
								navilist.add("月度工作汇报部署");
								navilist.add("月度工作汇报总览");
								navilist.add("战略信息");
								navilist.add("办公中心");*/
				navilist = ActionHomePageNaviPower.getNaviListByAlias(business, "DeptStrategyManager" + Navi);
				userNaviPower.setNavilist(navilist);
			}

			if (userNaviPower.isDeptReporter()) {

				/*				navilist.add("主页");
								//navilist.add("年度工作纲要部署");
								navilist.add("年度工作纲要总览");
								navilist.add("月度工作汇报部署");
								navilist.add("月度工作汇报总览");
								navilist.add("战略信息");
								navilist.add("办公中心");*/
				navilist = ActionHomePageNaviPower.getNaviListByAlias(business, "DeptReporter" + Navi);
				userNaviPower.setNavilist(navilist);
			}

			if (userNaviPower.isCompanyStrategyManager || effectivePerson.isManager()) {

				/*				navilist.add("主页");
								navilist.add("年度工作纲要部署");
								navilist.add("年度工作纲要总览");
								navilist.add("月度工作汇报部署");
								navilist.add("月度工作汇报总览");
								navilist.add("战略信息");
								navilist.add("办公中心");*/
				navilist = ActionHomePageNaviPower.getNaviListByAlias(business, "CompanyStrategyManager" + Navi);
				userNaviPower.setNavilist(navilist);
			}

			result.setData(userNaviPower);

		} catch (Exception e) {
			logger.error(e);
			result.error(e);
		}

		return result;

	}

	public static List<String> getStringList(List<String> original_List, int index) {
		List<String> resultList = new ArrayList<String>();
		for (String string : original_List) {
			resultList.add(StringUtils.split(string, "@")[index]);
		}
		return resultList;
	}

	/*
	 * 导航的配置格式
	 * 主页#年度工作纲要总览#月度工作汇报部署#月度工作汇报总览#战略信息#办公中心
	 * */
	public static List<String> getNaviListByAlias(Business business, String _alias) throws Exception {
		ConfigFactory _configFactory = business.configFactory();
		List<StrategyConfigSys> configlist = new ArrayList<StrategyConfigSys>();
		configlist = _configFactory.listByAlias(_alias);
		if (!configlist.isEmpty() && configlist.size() >= 1) {
			StrategyConfigSys config = _configFactory.listByAlias(_alias).get(0);
			String describe = config.getDescribe();
			String[] _stringArray = StringUtils.split(describe, "#");
			return Arrays.asList(_stringArray);
		} else {
			return null;
		}

	}

}
