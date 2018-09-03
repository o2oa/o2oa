package com.x.strategydeploy.assemble.control.keywork;

import java.util.ArrayList;
import java.util.List;

//import com.x.base.core.bean.WrapCopier;
//import com.x.base.core.bean.WrapCopierFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.service.KeyWorkOperationService;
import com.x.strategydeploy.core.entity.KeyworkInfo;
import com.x.strategydeploy.core.entity.MeasuresInfo;

public class BaseAction extends StandardJaxrsAction {
	private static  Logger logger = LoggerFactory.getLogger(BaseAction.class);

	public final static Integer DEFAULT_COUNT = 20;

	protected KeyWorkOperationService keyWorkOperationService = new KeyWorkOperationService();

	public static class Wi extends KeyworkInfo {
		
		private static final long serialVersionUID = 2346933142093715244L;
		public static WrapCopier<Wi, KeyworkInfo> copier = WrapCopierFactory.wo(Wi.class, KeyworkInfo.class, null, JpaObject.FieldsInvisible);

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

	public static class Wo extends KeyworkInfo {
		private static final long serialVersionUID = -3236185242950790725L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<KeyworkInfo, Wo> copier = WrapCopierFactory.wo(KeyworkInfo.class, Wo.class, null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField = "sequencenumber";

		@FieldDescribe("举措标题列表.")
		private List<String> measurestitlelist;

		private List<MeasuresInfo> measuresobjlist;

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

		public List<String> getMeasurestitlelist() {
			return measurestitlelist;
		}

		public void setMeasurestitlelist(List<String> measurestitlelist) {
			this.measurestitlelist = measurestitlelist;
		}

		public List<MeasuresInfo> getMeasuresobjlist() {
			return measuresobjlist;
		}

		public void setMeasuresobjlist(List<MeasuresInfo> measuresobjlist) {
			this.measuresobjlist = measuresobjlist;
		}

		public List<String> getActions() {
			return actions;
		}

		public void setActions(List<String> actions) {
			this.actions = actions;
		}

	}

	public static class WoStringList extends WrapStringList {

	}

}
