package com.x.crm.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	//客户
	public static class Customer {
		//基本信息
		public static class CustomerBaseInfo {
			public static final String table = "CRM_CUSTOMER_BASE_INFO";
		}

		//这应该是什么信息
		public static class CustomerOtherInfo {
		}
	}

	//商机
	public static class Opportunity {
		public static class OpportunityBaseInfo {
			public static final String table = "CRM_OPPORTUNITY_BASE_INFO";
		}
	}

	//销售线索
	public static class Clue {
		public static class ClueBaseInfo {
			public static final String table = "CRM_CLUE_BASE_INFO";
		}

	}

	public static class CrmConfig {
		public static class CrmBaseConfig {
			public static final String table = "CRM_BASECONFIG";
		}

		public static class CrmBaseConfigChildNode {
			public static final String table = "CRM_BASECONFIG_CHILDNODE";
		}

	}

	public static class CrmRegion {
		public static class CrmRegionConfig {
			public static final String table = "CRM_REGION_CONFIG";
		}
	}

}