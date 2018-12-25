package com.x.strategydeploy.assemble.control.strategy;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.service.StrategyDeployQueryService;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionListIncludeMeasures extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionListIncludeMeasures.class);
	StrategyDeployQueryService strategyDeployQueryService = new StrategyDeployQueryService();

	//输出
	public static class WoIncludeMeasures extends Wo {
		private static final long serialVersionUID = 5984201591919271788L;
		public List<MeasuresInfo> measureList = new ArrayList<MeasuresInfo>();

		public List<MeasuresInfo> getMeasureList() {
			return measureList;
		}

		public void setMeasureList(List<MeasuresInfo> measureList) {
			this.measureList = measureList;
		}
	}

	public ActionResult<List<WoIncludeMeasures>> execute(Wi wi) throws Exception {
		ActionResult<List<WoIncludeMeasures>> result = new ActionResult<>();
		List<WoIncludeMeasures> wos = new ArrayList<WoIncludeMeasures>();
		List<StrategyDeploy> objs = new ArrayList<StrategyDeploy>();
		List<MeasuresInfo> _includeObjs = new ArrayList<MeasuresInfo>();
		boolean ispass = true;
		if (null == wi.getStrategydeployyear() || wi.getStrategydeployyear().isEmpty()) {
			Exception e = new Exception("strategydeployyear can not be blank.");
			result.error(e);
			ispass = false;
		}
		if (ispass) {
			String _year = wi.getStrategydeployyear();
			WrapCopier<StrategyDeploy, WoIncludeMeasures> wrapout_copier = WrapCopierFactory.wo(StrategyDeploy.class, WoIncludeMeasures.class, null, Wo.Excludes);
			objs = strategyDeployQueryService.getListByYear(_year);
			for (StrategyDeploy o : objs) {
				_includeObjs = ActionListIncludeMeasures.getMeasuresInfoListByParentid(o.getId());
				WoIncludeMeasures _woincludemeasures = wrapout_copier.copy(o);
				_woincludemeasures.setMeasureList(_includeObjs);
				wos.add(_woincludemeasures);
			}
			result.setData(wos);
		}

		return result;
	}

	protected static List<MeasuresInfo> getMeasuresInfoListByParentid(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.measuresInfoFactory().getListByParentId(id);
		} catch (Exception e) {
			throw e;
		}
	}

}
