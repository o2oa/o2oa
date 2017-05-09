package com.x.crm.assemble.control;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.crm.assemble.control.wrapin.WrapInClue;
import com.x.crm.assemble.control.wrapin.WrapInCrmBaseConfig;
import com.x.crm.assemble.control.wrapin.WrapInCrmRegion;
import com.x.crm.assemble.control.wrapin.WrapInCustomerBaseInfo;
import com.x.crm.assemble.control.wrapout.WrapOutClue;
import com.x.crm.assemble.control.wrapout.WrapOutCrmBaseConfig;
import com.x.crm.assemble.control.wrapout.WrapOutCustomerBaseConfig;
import com.x.crm.assemble.control.wrapout.WrapOutCustomerBaseInfo;
import com.x.crm.assemble.control.wrapout.WrapOutOpportunity;
import com.x.crm.assemble.control.wrapout.WrapOutRegion;
import com.x.crm.core.entity.Clue;
import com.x.crm.core.entity.CrmBaseConfig;
import com.x.crm.core.entity.CrmRegion;
import com.x.crm.core.entity.CustomerBaseInfo;
import com.x.crm.core.entity.Opportunity;

public class WrapCrmTools {

	/*
	 * 客户基本信息
	 * */
	public static BeanCopyTools<CustomerBaseInfo, WrapOutCustomerBaseInfo> CustomerBaseInfoOutCopier = BeanCopyToolsBuilder.create(CustomerBaseInfo.class, WrapOutCustomerBaseInfo.class, null, WrapOutCustomerBaseInfo.Excludes);
	public static BeanCopyTools<WrapInCustomerBaseInfo, CustomerBaseInfo> CustomerBaseInfoInCopier = BeanCopyToolsBuilder.create(WrapInCustomerBaseInfo.class, CustomerBaseInfo.class, null, WrapInCustomerBaseInfo.Excludes);
	/*
	 * 商机基本信息
	 * opportunity
	 * */
	public static BeanCopyTools<Opportunity, WrapOutOpportunity> OpportunityOutCopier = BeanCopyToolsBuilder.create(Opportunity.class, WrapOutOpportunity.class, null, WrapOutOpportunity.Excludes);

	/*
	 * 线索基本信息
	 * */
	public static BeanCopyTools<Clue, WrapOutClue> ClueOutCopier = BeanCopyToolsBuilder.create(Clue.class, WrapOutClue.class, null, WrapOutClue.Excludes);
	public static BeanCopyTools<WrapInClue, Clue> clueInCopier = BeanCopyToolsBuilder.create(WrapInClue.class, Clue.class, null, WrapInClue.Excludes);

	/*
	 * 基本配置信息
	 * */
	public static BeanCopyTools<CrmBaseConfig, WrapOutCrmBaseConfig> CrmBaseConfigOutCopier = BeanCopyToolsBuilder.create(CrmBaseConfig.class, WrapOutCrmBaseConfig.class, null, WrapOutCrmBaseConfig.Excludes);
	public static BeanCopyTools<WrapInCrmBaseConfig, CrmBaseConfig> CrmBaseConfigInCopier = BeanCopyToolsBuilder.create(WrapInCrmBaseConfig.class, CrmBaseConfig.class, null, WrapInCrmBaseConfig.Excludes);

	/*
	 * 基本配置信息根节点信息
	 * */
	public static BeanCopyTools<CrmBaseConfig, WrapOutCustomerBaseConfig> CrmBaseConfigRootOutCopier = BeanCopyToolsBuilder.create(CrmBaseConfig.class, WrapOutCustomerBaseConfig.class, null, WrapOutCustomerBaseConfig.Excludes);

	/*
	 * 省，市，区，县
	 * */
	public static BeanCopyTools<WrapInCrmRegion, CrmRegion> CrmRegionInCopier = BeanCopyToolsBuilder.create(WrapInCrmRegion.class, CrmRegion.class, null, WrapInCrmRegion.Excludes);
	public static BeanCopyTools<CrmRegion, WrapOutRegion> CrmRegionOutCopier = BeanCopyToolsBuilder.create(CrmRegion.class, WrapOutRegion.class, null, WrapOutRegion.Excludes);
}
