package com.x.strategydeploy.assemble.control.strategy;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionDeleteAndMeasuers extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionDeleteAndMeasuers.class);

	public static class Wo extends WoId {
		List<String> MeasuresInfoids = new ArrayList<String>();

		public List<String> getMeasuresInfoids() {
			return MeasuresInfoids;
		}

		public void setMeasuresInfoids(List<String> measuresInfoids) {
			MeasuresInfoids = measuresInfoids;
		}
		
	}

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String _id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			boolean IsExist = false;

			List<String> _measuresInfoids  = new ArrayList<String>();
			
			if (null == _id || _id.isEmpty()) {
				IsExist = false;
			} else {
				IsExist = business.strategyDeployFactory().IsExistById(_id);
			}
			
			if (IsExist) {
				List<MeasuresInfo>  MeasuresInfoList = business.measuresInfoFactory().getListByParentId(_id);
				String _measuresInfoId = "";
				for (MeasuresInfo measuresInfo : MeasuresInfoList) {
					_measuresInfoId = measuresInfo.getId();
					measuresInfo = emc.find(_measuresInfoId, MeasuresInfo.class);
					emc.beginTransaction(MeasuresInfo.class);
					emc.remove(measuresInfo);
					emc.commit();
					_measuresInfoids.add(_measuresInfoId);
				}	
				
				StrategyDeploy strategydeploy = emc.find(_id, StrategyDeploy.class);
				emc.beginTransaction(StrategyDeploy.class);
				emc.remove(strategydeploy);
				emc.commit();
				wo.setId(_id);
				wo.setMeasuresInfoids(_measuresInfoids);
				result.setData(wo);
			} else {
				//throw new Exception("strategydeploy is not Exist !");
				Exception exception = new Exception("strategydeploy is not Exist !");
				result.error(exception);
			}

		}

		return result;

	}

}
