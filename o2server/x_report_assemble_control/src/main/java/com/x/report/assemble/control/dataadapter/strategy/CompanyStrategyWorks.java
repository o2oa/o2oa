package com.x.report.assemble.control.dataadapter.strategy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.report.assemble.control.ThisApplication;
import com.x.strategydeploy.core.entity.KeyworkInfo;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

/**
 * 从公司战略系统中获取所有的部门重点工作信息列表
 * 
 * @author O2LEE
 */
public class CompanyStrategyWorks {

	/**
	 * TODO 从战略管理系统中获取所有的部门重点工作信息列表
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public List<WoCompanyStrategyWorks> all(String year, String month) throws UnsupportedEncodingException, Exception {
		String serviceUri = "keyworkextra/listbyyearandmonth/year/" + year + "/month/" + month;
//		ActionResponse resp = ThisApplication.context().applications().getQuery(x_strategydeploy_assemble_control.class, serviceUri );
		ActionResponse resp = ThisApplication.context().applications().getQuery("x_strategydeploy_assemble_control",
				serviceUri);
		List<WoCompanyStrategyWorks> wos = resp.getDataAsList(WoCompanyStrategyWorks.class);
		return wos;
	}

	/**
	 * TODO 从战略管理系统中获取所有的部门重点工作信息列表
	 * 
	 * @param year
	 * @return
	 */
	public List<WoCompanyStrategyWorks> all(String year) throws UnsupportedEncodingException, Exception {
		String serviceUri = "keyworkextra/listbyyear/" + year;
//		ActionResponse resp = ThisApplication.context().applications().getQuery(x_strategydeploy_assemble_control.class, serviceUri );
		ActionResponse resp = ThisApplication.context().applications().getQuery("x_strategydeploy_assemble_control",
				serviceUri);
		List<WoCompanyStrategyWorks> wos = resp.getDataAsList(WoCompanyStrategyWorks.class);
		return wos;
	}

	public static class WoCompanyStrategyWorks extends KeyworkInfo {

		private static final long serialVersionUID = -3236185242950790725L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<KeyworkInfo, WoCompanyStrategyWorks> copier = WrapCopierFactory.wo(KeyworkInfo.class,
				WoCompanyStrategyWorks.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("工作所属的战略举措信息列表")
		private List<WoMeasuresInfoInWork> measuresobjlist;

		public List<WoMeasuresInfoInWork> getMeasuresobjlist() {
			return measuresobjlist;
		}

		public void setMeasuresobjlist(List<WoMeasuresInfoInWork> measuresobjlist) {
			this.measuresobjlist = measuresobjlist;
		}
	}

	public static class WoMeasuresInfoInWork extends MeasuresInfo {

		private static final long serialVersionUID = -3236185242950790725L;

		public static List<String> Excludes = new ArrayList<String>();

		@FieldDescribe("战略举措所属的公司重点部署信息列表")
		private WoStrategyDeployInMeasure strategyDeploy = null;

		public static WrapCopier<MeasuresInfo, WoMeasuresInfoInWork> copier = WrapCopierFactory.wo(MeasuresInfo.class,
				WoMeasuresInfoInWork.class, null, JpaObject.FieldsInvisible);

		public WoStrategyDeployInMeasure getStrategyDeploy() {
			return strategyDeploy;
		}

		public void setStrategyDeploy(WoStrategyDeployInMeasure strategyDeploy) {
			this.strategyDeploy = strategyDeploy;
		}
	}

	public static class WoStrategyDeployInMeasure extends StrategyDeploy {

		private static final long serialVersionUID = -3236185242950790725L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<StrategyDeploy, WoStrategyDeployInMeasure> copier = WrapCopierFactory
				.wo(StrategyDeploy.class, WoStrategyDeployInMeasure.class, null, JpaObject.FieldsInvisible);

	}
}
