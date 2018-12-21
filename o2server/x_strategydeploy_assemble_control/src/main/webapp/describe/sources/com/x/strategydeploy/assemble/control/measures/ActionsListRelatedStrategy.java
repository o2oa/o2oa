package com.x.strategydeploy.assemble.control.measures;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.service.MeasuresInfoQueryService;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionsListRelatedStrategy extends BaseAction {
	MeasuresInfoQueryService measuresInfoQueryService = new MeasuresInfoQueryService();

	public static class WoRelatedStrategy extends Wo {
		private static final long serialVersionUID = 3904083154713027185L;
		public StrategyDeploy strategydeploy = new StrategyDeploy();

		public StrategyDeploy getStrategydeploy() {
			return strategydeploy;
		}

		public void setStrategydeploy(StrategyDeploy strategydeploy) {
			this.strategydeploy = strategydeploy;
		}
	}

	public ActionResult<List<WoRelatedStrategy>> execute(Wi wi) throws Exception {
		ActionResult<List<WoRelatedStrategy>> result = new ActionResult<>();
		List<WoRelatedStrategy> wos = new ArrayList<WoRelatedStrategy>();
		List<MeasuresInfo> objs = new ArrayList<MeasuresInfo>();
		StrategyDeploy _relatedObjs = new StrategyDeploy();
		boolean ispass = true;
		String year = "";
		if (null == wi.getMeasuresinfoyear() || wi.getMeasuresinfoyear().isEmpty()) {
			Exception e = new Exception("measuresinfoyear can not be blank.");
			result.error(e);
			ispass = false;
		}
		if (ispass) {
			year = wi.getMeasuresinfoyear();
			objs = measuresInfoQueryService.getListByYear(year);
			WrapCopier<MeasuresInfo, WoRelatedStrategy> wrapout_copier = WrapCopierFactory.wo(MeasuresInfo.class, WoRelatedStrategy.class, null, Wo.Excludes);
			for (MeasuresInfo o : objs) {
				_relatedObjs = ActionsListRelatedStrategy.getStrategyDeployById(o.getMeasuresinfoparentid());
				WoRelatedStrategy _woRelatedStrategy = wrapout_copier.copy(o);
				_woRelatedStrategy.setStrategydeploy(_relatedObjs);
				wos.add(_woRelatedStrategy);
			}
			result.setData(wos);
		}

		return result;
	}

	protected static StrategyDeploy getStrategyDeployById(String _id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.strategyDeployFactory().getById(_id);
		} catch (Exception e) {
			throw e;
		}
	}
}
