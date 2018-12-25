package com.x.strategydeploy.assemble.control.configsys;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.core.entity.StrategyConfigSys;

public class BaseAction extends StandardJaxrsAction {

	private static  Logger logger = LoggerFactory.getLogger(BaseAction.class);

	public static class Wi extends StrategyConfigSys {
		private static final long serialVersionUID = -8900474702398209214L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		public static WrapCopier<Wi, StrategyConfigSys> copier = WrapCopierFactory.wi(Wi.class, StrategyConfigSys.class, null, Wi.Excludes);

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField = "sequencenumber";

		public String getSequenceField() {
			return sequenceField;
		}

		public void setSequenceField(String sequenceField) {
			this.sequenceField = sequenceField;
		}
	}

	public static class Wo extends StrategyConfigSys {
		private static final long serialVersionUID = -1912555201109659676L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<StrategyConfigSys, Wo> copyToWo = WrapCopierFactory.wo(StrategyConfigSys.class, Wo.class, null, Wo.Excludes);

		private Long rank = 0L;

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField = "sequencenumber";

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

	}
}
