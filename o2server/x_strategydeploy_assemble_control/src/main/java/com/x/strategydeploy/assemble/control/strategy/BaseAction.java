package com.x.strategydeploy.assemble.control.strategy;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.bean.WrapCopier;
//import com.x.base.core.bean.WrapCopierFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.service.StrategyDeployOperationService;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class BaseAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	public final static Integer DEFAULT_COUNT = 20;

	protected StrategyDeployOperationService strategyDeployOperationService = new StrategyDeployOperationService();

	public static class Wi extends StrategyDeploy {
		private static final long serialVersionUID = -5076990764713538973L;
		public static WrapCopier<Wi, StrategyDeploy> copier = WrapCopierFactory.wi(Wi.class, StrategyDeploy.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField = "sequencenumber";

		@FieldDescribe("升降序标志.")
		private String ordersymbol = "";

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

	}

	public static class Wo extends StrategyDeploy {
		private static final long serialVersionUID = -6853697322562403034L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<StrategyDeploy, Wo> copier = WrapCopierFactory.wo(StrategyDeploy.class, Wo.class, null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField = "sequencenumber";

		private List<String> actions = new ArrayList<>();

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getSequenceField() {
			return sequenceField;
		}

		public void setSequenceField(String sequenceField) {
			this.sequenceField = sequenceField;
		}

		public List<String> getActions() {
			return actions;
		}

		public void setActions(List<String> actions) {
			this.actions = actions;
		}

	}

	public static class WoExcludesFieldsInvisible extends StrategyDeploy {
		private static final long serialVersionUID = 9006027912846898894L;
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsInvisible);
		public static WrapCopier<StrategyDeploy, WoExcludesFieldsInvisible> copier = WrapCopierFactory.wo(StrategyDeploy.class, WoExcludesFieldsInvisible.class, null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}


	static class WoUnitListAbstract extends GsonPropertyObject {

		@FieldDescribe("组织识别名")
		private List<String> unitList = new ArrayList<>();

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}
	
	public static class WoUnit extends WoUnitListAbstract {

	}
}
