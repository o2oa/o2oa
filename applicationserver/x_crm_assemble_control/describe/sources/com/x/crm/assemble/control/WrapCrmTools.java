package com.x.crm.assemble.control;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.crm.assemble.control.jaxrs.customer.BaseAction;
import com.x.crm.assemble.control.wrapin.WrapInClue;
import com.x.crm.assemble.control.wrapin.WrapInCrmBaseConfig;
import com.x.crm.assemble.control.wrapin.WrapInCrmRegion;
import com.x.crm.assemble.control.wrapin.WrapInCustomerBaseInfo;
import com.x.crm.assemble.control.wrapin.WrapInOpportunity;
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
	 */
	public static WrapCopier<CustomerBaseInfo, WrapOutCustomerBaseInfo> CustomerBaseInfoOutCopier = WrapCopierFactory
			.wo(CustomerBaseInfo.class, WrapOutCustomerBaseInfo.class, null, WrapOutCustomerBaseInfo.Excludes);
	// public static WrapCopier<WrapInCustomerBaseInfo, CustomerBaseInfo>
	// CustomerBaseInfoInCopier_update =
	// WrapCopierFactory.create(WrapInCustomerBaseInfo.class,
	// CustomerBaseInfo.class);
	public static WrapCopier<BaseAction.Wi, CustomerBaseInfo> CustomerBaseInfoInCopier_update = WrapCopierFactory
			.wi(BaseAction.Wi.class, CustomerBaseInfo.class, null, JpaObject.FieldsUnmodify);
	// public static WrapCopier<WrapInCustomerBaseInfo, CustomerBaseInfo>
	// CustomerBaseInfoInCopier_create =
	// WrapCopierFactory.create(WrapInCustomerBaseInfo.class,
	// CustomerBaseInfo.class, null, WrapInCustomerBaseInfo.Excludes);
	public static WrapCopier<BaseAction.Wi, CustomerBaseInfo> CustomerBaseInfoInCopier_create = WrapCopierFactory
			.wi(BaseAction.Wi.class, CustomerBaseInfo.class, null, WrapInCustomerBaseInfo.Excludes);
	/*
	 * 商机基本信息 opportunity
	 */
	public static WrapCopier<Opportunity, WrapOutOpportunity> OpportunityOutCopier = WrapCopierFactory
			.wo(Opportunity.class, WrapOutOpportunity.class, null, WrapOutOpportunity.Excludes);
	public static WrapCopier<WrapInOpportunity, Opportunity> OpportunityInCopier = WrapCopierFactory
			.wi(WrapInOpportunity.class, Opportunity.class, null, WrapInOpportunity.Excludes);

	/*
	 * 线索基本信息
	 */
	public static WrapCopier<Clue, WrapOutClue> ClueOutCopier = WrapCopierFactory.wo(Clue.class, WrapOutClue.class,
			null, WrapOutClue.Excludes);
	public static WrapCopier<WrapInClue, Clue> clueInCopier = WrapCopierFactory.wi(WrapInClue.class, Clue.class, null,
			WrapInClue.Excludes);

	/*
	 * 基本配置信息
	 */
	public static WrapCopier<CrmBaseConfig, WrapOutCrmBaseConfig> CrmBaseConfigOutCopier = WrapCopierFactory
			.wo(CrmBaseConfig.class, WrapOutCrmBaseConfig.class, null, WrapOutCrmBaseConfig.Excludes);
	public static WrapCopier<WrapInCrmBaseConfig, CrmBaseConfig> CrmBaseConfigInCopier = WrapCopierFactory
			.wi(WrapInCrmBaseConfig.class, CrmBaseConfig.class, null, WrapInCrmBaseConfig.Excludes);

	/*
	 * 基本配置信息根节点信息
	 */
	public static WrapCopier<CrmBaseConfig, WrapOutCustomerBaseConfig> CrmBaseConfigRootOutCopier = WrapCopierFactory
			.wo(CrmBaseConfig.class, WrapOutCustomerBaseConfig.class, null, WrapOutCustomerBaseConfig.Excludes);

	/*
	 * 省，市，区，县
	 */
	public static WrapCopier<WrapInCrmRegion, CrmRegion> CrmRegionInCopier = WrapCopierFactory
			.wi(WrapInCrmRegion.class, CrmRegion.class, null, WrapInCrmRegion.Excludes);
	public static WrapCopier<CrmRegion, WrapOutRegion> CrmRegionOutCopier = WrapCopierFactory.wo(CrmRegion.class,
			WrapOutRegion.class, null, WrapOutRegion.Excludes);
}
