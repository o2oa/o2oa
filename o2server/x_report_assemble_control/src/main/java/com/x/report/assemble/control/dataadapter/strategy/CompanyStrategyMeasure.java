package com.x.report.assemble.control.dataadapter.strategy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.report.assemble.control.ThisApplication;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

/**
 * 从公司战略系统中获取举措以及公司工作重点配置信息
 * 
 * @author O2LEE
 */
public class CompanyStrategyMeasure{

	/**
	 * TODO 从战略管理系统中获取所有的公司战略工作重点举措信息列表
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedEncodingException 
	 * http://dev.o2oa.io:20020/x_strategydeploy_assemble_control/jaxrs/strategydeployextra/listbyyear
	 */
	public List<WoCompanyStrategy> all( String year ) throws UnsupportedEncodingException, Exception {
		String serviceUrl = "strategydeployextra/listbyyear/";
		String dataJson = "{\"strategydeployyear\":\""+year+"\"}";

//		ActionResponse resp = ThisApplication.context().applications().putQuery(x_strategydeploy_assemble_control.class, serviceUrl, dataJson );
		ActionResponse resp = ThisApplication.context().applications().putQuery("x_strategydeploy_assemble_control", serviceUrl, dataJson );
		List<WoCompanyStrategy> wos = resp.getDataAsList( WoCompanyStrategy.class );
		return wos;
	}
	
	public static class WoCompanyStrategy extends StrategyDeploy {
		
		private static final long serialVersionUID = -6853697322562403034L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<StrategyDeploy, WoCompanyStrategy> copier = WrapCopierFactory.wo(StrategyDeploy.class, WoCompanyStrategy.class, null, WoCompanyStrategy.Excludes);

		private Long rank = 0L;

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField = "sequencenumber";
		
		@FieldDescribe("战略工作列表.")
		private List<WoMeasuresInfo> measureList = null;

		@FieldDescribe("战略举措列表.")
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

		public List<WoMeasuresInfo> getMeasureList() {
			return measureList;
		}

		public void setMeasureList(List<WoMeasuresInfo> measureList) {
			this.measureList = measureList;
		}
		
	}
	
	public static class WoMeasuresInfo extends MeasuresInfo {
		
		private static final long serialVersionUID = -1124004693944906073L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<MeasuresInfo, WoMeasuresInfo> copier = WrapCopierFactory.wo(MeasuresInfo.class, WoMeasuresInfo.class, null, WoMeasuresInfo.Excludes);

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

}
