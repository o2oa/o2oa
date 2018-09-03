package com.x.strategydeploy.assemble.control.strategy;

import java.util.ArrayList;
import java.util.List;


import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.service.StrategyDeployQueryService;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionListIncludeMeasuresByStrategyYearAndMeasuresDeptList extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListIncludeMeasuresByStrategyYearAndMeasuresDeptList.class);
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

	public static class WiAppendMeasuresDeptList extends StrategyDeploy {
		private static final long serialVersionUID = -5076990764713538973L;
		public static WrapCopier<Wi, StrategyDeploy> copier = WrapCopierFactory.wi(Wi.class, StrategyDeploy.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField = "sequencenumber";

		@FieldDescribe("升降序标志.")
		private String ordersymbol = "";

		private List<String> measuresdept = new ArrayList<String>();

		boolean islistall = false;

		public String getSequenceField() {
			return sequenceField;
		}

		public void setSequenceField(String sequenceField) {
			this.sequenceField = sequenceField;
		}

		public String getOrdersymbol() {
			return ordersymbol;
		}

		public void setOrdersymbol(String ordersymbol) {
			this.ordersymbol = ordersymbol;
		}

		public List<String> getMeasuresdept() {
			return measuresdept;
		}

		public void setMeasuresdept(List<String> measuresdept) {
			this.measuresdept = measuresdept;
		}

		public boolean isIslistall() {
			return islistall;
		}

		public void setIslistall(boolean islistall) {
			this.islistall = islistall;
		}

	}

	public ActionResult<List<WoIncludeMeasures>> execute(WiAppendMeasuresDeptList wi) throws Exception {
		ActionResult<List<WoIncludeMeasures>> result = new ActionResult<>();
		List<WoIncludeMeasures> wos = new ArrayList<WoIncludeMeasures>();
		List<StrategyDeploy> objs = new ArrayList<StrategyDeploy>();
		List<MeasuresInfo> _includeObjs = new ArrayList<MeasuresInfo>();

		List<String> MeasuresDeptList = new ArrayList<String>();

		boolean ispass = true;
		if (null == wi.getStrategydeployyear() || wi.getStrategydeployyear().isEmpty()) {
			Exception e = new Exception("strategydeployyear can not be blank.");
			result.error(e);
			ispass = false;
		}
		if (ispass) {

			//logger.info("ActionListIncludeMeasuresByStrategyYearAndMeasuresDeptList=============>ispass");
			String _year = wi.getStrategydeployyear();
			//logger.info("ActionListIncludeMeasuresByStrategyYearAndMeasuresDeptList=============>_year:" + _year);
			logger.info("ActionListIncludeMeasuresByStrategyYearAndMeasuresDeptList=============>wi.getMeasuresdept:" + wi.getMeasuresdept().toString());
			WrapCopier<StrategyDeploy, WoIncludeMeasures> wrapout_copier = WrapCopierFactory.wo(StrategyDeploy.class, WoIncludeMeasures.class, null, Wo.Excludes);
			objs = strategyDeployQueryService.getListByYear(_year);
			for (StrategyDeploy o : objs) {
				List<MeasuresInfo> _resObjs = new ArrayList<MeasuresInfo>();
				_includeObjs = ActionListIncludeMeasuresByStrategyYearAndMeasuresDeptList.getMeasuresInfoListByParentid(o.getId());
				WoIncludeMeasures _woincludemeasures = wrapout_copier.copy(o);
				//logger.info("wi.getMeasuresdept:" + wi.getMeasuresdept().toString());

				_woincludemeasures.setMeasureList(_includeObjs);
	/*			if (wi.islistall) {
					//
					_woincludemeasures.setMeasureList(_includeObjs);
				} else {
					if (null == wi.getMeasuresdept() || wi.getMeasuresdept().size() <= 0 || wi.getMeasuresdept().isEmpty()) {
						//没部门，就全部输出
						_woincludemeasures.setMeasureList(_includeObjs);
					} else {
						//_resObjs.clear();
						for (MeasuresInfo measuresInfo : _includeObjs) {
							MeasuresDeptList = measuresInfo.getDeptlist();
							String MeasuresDeptQianTou = measuresInfo.getMeasuresdutydept();//牵头部门
							int _tmpindex = wi.getMeasuresdept().indexOf(MeasuresDeptQianTou);
							//相关部门过滤,判断两个list 是否有交集。有交集的加入到结果集。
							//logger.info("MeasuresDeptList:"+MeasuresDeptList.toString());
							//logger.info("wi.getMeasuresdept():"+wi.getMeasuresdept().toString());
							//logger.info("MeasuresDeptQianTou:" + MeasuresDeptQianTou);

							logger.info("aaaa:" + measuresInfo.getFormatsequencenumber() + ":" + measuresInfo.getDeptlist().toString());

							//MeasuresDeptList.retainAll(wi.getMeasuresdept());

							if (_tmpindex >= 0) {
								_resObjs.add(measuresInfo);
							} else {
								MeasuresDeptList.retainAll(wi.getMeasuresdept());
								if (MeasuresDeptList.size() > 0) {
									_resObjs.add(measuresInfo);
								}
							}

						}
						if (!_resObjs.isEmpty()) {
							_woincludemeasures.setMeasureList(_resObjs);
						}

					}
				}*/

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

	public boolean isHasRetain(List<String> list1, List<String> list2) {
		return false;
	}
}
